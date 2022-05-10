package com.example.myapplication.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff.Mode.SRC_IN
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.annotation.WorkerThread
import com.facebook.common.executors.UiThreadImmediateExecutorService
import com.facebook.common.references.CloseableReference
import com.facebook.datasource.DataSource
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber
import com.facebook.imagepipeline.image.CloseableImage
import com.facebook.imagepipeline.request.ImageRequestBuilder
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executor

/**
 * Created by susion on 2018/3/22.
 */
object ShareBitmapHelper {

    /**
     * 分享视图转换成图片
     */
    @JvmStatic
    fun shareViewToBmpWithRatioViaPx(view: View, width: Int, height: Int): Bitmap? {
        return view.run {
            isDrawingCacheEnabled = true
            drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
            drawingCacheBackgroundColor = Color.TRANSPARENT

            measure(
                View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(height,
                    View.MeasureSpec.EXACTLY))
            layout(0, 0, view.measuredWidth, view.measuredHeight)
            buildDrawingCache()
            drawingCache
        }
    }

    @WorkerThread
    @JvmStatic
    private fun bmpToByteArray(bmp: Bitmap, needRecycle: Boolean): ByteArray {
        val output = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output)

        val result = output.toByteArray()
        try {
            output.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }

    /**
     * 创建一个固定大小的图片，居中裁剪。
     */
    @WorkerThread
    fun createBitmap(sourceBmp: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
        if (targetWidth <= 0 || targetHeight <= 0) {
            throw IllegalArgumentException("targetWidth or targetHeight can not <= 0")
        }
        val bmpWidth = sourceBmp.width
        val bmpHeight = sourceBmp.height
        if (targetWidth == bmpWidth && targetHeight == bmpHeight) {
            return sourceBmp
        }
        val newBmp = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
        val srcLeft: Int
        val srcTop: Int
        val srcRight: Int
        val srcBottom: Int

        val width: Int
        val height: Int
        if (bmpWidth / (bmpHeight * 1.0f) > targetWidth / (targetHeight * 1.0f)) {
            height = bmpHeight
            width = height * targetWidth / targetHeight

            srcTop = 0
            srcBottom = height

            srcLeft = (bmpWidth - width) / 2
            srcRight = width + (bmpWidth - width) / 2
        } else {
            width = bmpWidth
            height = width * targetHeight / targetWidth

            srcLeft = 0
            srcRight = width

            srcTop = (bmpHeight - height) / 2
            srcBottom = height + (bmpHeight - height) / 2
        }
        val canvas = Canvas(newBmp)
        canvas.drawBitmap(sourceBmp, Rect(srcLeft, srcTop, srcRight, srcBottom),
            Rect(0, 0, targetWidth, targetHeight), null)
        canvas.save()
        canvas.restore()
        return newBmp
    }

    /**
     * 裁剪小程序图片
     *
     * @param bmp
     * @return
     */
    @WorkerThread
    @JvmStatic
    fun clipBmpFitMiniProgram(bmp: Bitmap): Bitmap {
        return when {
            bmp.width * 4 == bmp.height * 5 -> bmp
            bmp.width * 4 > bmp.height * 5 -> {
                val targetWidth = (bmp.height * 1.25f).toInt()
                val x = ((bmp.width - bmp.height * 1.25f) / 2).toInt()
                Bitmap.createBitmap(bmp, x, 0, targetWidth, bmp.height)
            }
            else -> {
                val targetHeight = (bmp.width / 1.25f).toInt()
                val y = ((bmp.height - bmp.width / 1.25f) / 2).toInt()
                Bitmap.createBitmap(bmp, 0, y, bmp.width, targetHeight)
            }
        }
    }


    /**
     * 二分查找的方式查找满足要求的quality(即满足<[maxSize]的最大的quality)。
     */
    @WorkerThread
    private fun compressBmp(bmp: Bitmap, needRecycle: Boolean, maxSize: Int): ByteArray {
        var start = 0
        var end = 100
        val output = ByteArrayOutputStream()
        // 1. jpg 满足直接返回jpg
        bmp.compress(Bitmap.CompressFormat.JPEG, end, output)
        if (output.toByteArray().size <= maxSize) {
            return output.toByteArray()
        }
        var middle = 0
        // 2. 二分查找满足要求的quality
        while (start in 0 until end) {
            output.reset()
            val mid = (start + end) / 2
            if (middle == mid) {
                break
            }
            bmp.compress(Bitmap.CompressFormat.JPEG, mid, output)
            if (output.toByteArray().size == maxSize) {
                break
            } else if (output.toByteArray().size < maxSize) {
                // mid + 1 可能 > maxSize，这样就永远无法找到满足要求的了。这样的话，这里可能永远退不出循环。
                start = mid
            } else {
                end = mid - 1
            }
            middle = mid
        }
        output.reset()
        bmp.compress(Bitmap.CompressFormat.JPEG, if (start < 0) 0 else start, output)
        return output.toByteArray()
    }

    @WorkerThread
    private fun compressBmpPNG(bmp: Bitmap, quality: Int, needRecycle: Boolean): ByteArray {
        val output = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG, quality, output)
        val result = output.toByteArray()
        try {
            output.close()
        } catch (e: Exception) {
//            ShareSdkLog.logError(e)
        }

        return result
    }

    @WorkerThread
    @JvmStatic
    fun toRoundBitmap(bitmap: Bitmap): Bitmap {
        var width = bitmap.width
        var height = bitmap.height
        val roundPx: Float
        val left: Float
        val top: Float
        val right: Float
        val bottom: Float
        val dst_left: Float
        val dst_top: Float
        val dst_right: Float
        val dst_bottom: Float
        if (width <= height) {
            roundPx = (width / 2).toFloat()
            top = 0f
            bottom = width.toFloat()
            left = 0f
            right = width.toFloat()
            height = width
            dst_left = 0f
            dst_top = 0f
            dst_right = width.toFloat()
            dst_bottom = width.toFloat()
        } else {
            roundPx = (height / 2).toFloat()
            val clip = ((width - height) / 2).toFloat()
            left = clip
            right = width - clip
            top = 0f
            bottom = height.toFloat()
            width = height
            dst_left = 0f
            dst_top = 0f
            dst_right = height.toFloat()
            dst_bottom = height.toFloat()
        }
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val color = -0xbdbdbe
        val paint = Paint()
        val src = Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
        val dst = Rect(dst_left.toInt(), dst_top.toInt(), dst_right.toInt(), dst_bottom.toInt())
        val rectF = RectF(dst)
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint)
        paint.xfermode = PorterDuffXfermode(SRC_IN)
        canvas.drawBitmap(bitmap, src, dst, paint)
        return output
    }

    @WorkerThread
    @JvmStatic
    fun createSnapshotWithRatio(bitmap: Bitmap, w: Int, h: Int): Bitmap {
        val bmpWidthHeightRate = bitmap.width / (bitmap.height * 1.0f)
        if (bmpWidthHeightRate == w / (h * 1.0f)) {
            return bitmap
        }

        when {
            bmpWidthHeightRate < w / (h * 1.0f) -> {
                val width = bitmap.width
                val height = width * h / w
                if (width <= 0 || height <= 0) {
                    return bitmap
                }
                // height 实际比bitmap.height 要小
                val newBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(newBmp)
                canvas.drawBitmap(bitmap, Rect(0, (bitmap.height - height) / 2, width,
                    bitmap.height - (bitmap.height - height) / 2),
                    Rect(0, 0, width, height), null)
                canvas.save()
                canvas.restore()
                return newBmp
            }
            else -> {
                val height = bitmap.height
                val width = height * w / h
                if (width <= 0 || height <= 0) {
                    return bitmap
                }
                // width 实际比bitmap.width 要小
                val newBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(newBmp)
                canvas.drawBitmap(bitmap, Rect((bitmap.width - width) / 2, 0,
                    bitmap.width - (bitmap.width - width) / 2, height),
                    Rect(0, 0, width, height), null)
                canvas.save()
                canvas.restore()
                return newBmp
            }
        }
    }

    @WorkerThread
    @JvmStatic
    fun createSnapshotWithRatioViaTop(bitmap: Bitmap, w: Int, h: Int): Bitmap {
        val bmpWidthHeightRate = bitmap.width / (bitmap.height * 1.0f)
        if (bmpWidthHeightRate == w / (h * 1.0f)) {
            return bitmap
        }

        when {
            bmpWidthHeightRate < w / (h * 1.0f) -> {
                // height 实际比bitmap.height 要小
                val newBmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(newBmp)
                val width = bitmap.width
                canvas.drawBitmap(bitmap,
                    Rect(0, 0, width, width * h / w),
                    Rect(0, 0, w, h), null)
                canvas.save()
                canvas.restore()
                return newBmp
            }
            else -> {
                val width = bitmap.width
                val height = bitmap.height
                val finalHeight = height * w / width
                // width 实际比bitmap.width 要小
                val newBmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(newBmp)
                canvas.drawColor(Color.WHITE)
                canvas.drawBitmap(bitmap, Rect(0, 0, width, height),
                    Rect(0, (h - finalHeight) / 2, w, (h + finalHeight) / 2), null)
                canvas.save()
                canvas.restore()
                return newBmp
            }
        }
    }

    /**
     * 不使用Fresco原生直接加载设置 因原生在离屏模式下，不能触发加载显示
     */
    @JvmStatic
    fun fetchBmp(uriString: String?, bitmapCallback: BitmapCallback?, executor: Executor = UiThreadImmediateExecutorService.getInstance()) {
        val imagePipeline = Fresco.getImagePipeline()
        val dataSource = imagePipeline.fetchDecodedImage(
            ImageRequestBuilder.newBuilderWithSource(Uri.parse(uriString)).build(), null)
        dataSource.subscribe(object : BaseBitmapDataSubscriber() {
            override fun onFailureImpl(
                dataSource: DataSource<CloseableReference<CloseableImage>>) {
                bitmapCallback?.onFail()
            }

            override fun onNewResultImpl(bitmap: Bitmap?) {
                if (bitmap == null) {
                    bitmapCallback?.onFail()
                    return
                }
                /**
                 * pipeline回调回来的bitmap只能在这个回调函数的范围内使用，此回调函数执行完之后fresco会将此bitmap回收掉，
                 * 这一机制导致用该bitmap创建的BitmapDrawable在每次绘制view时，由于都会访问该bitmap,造成crash，
                 * 所以为避免此问题，利用回调回来的bitmap创建一个新的bitmap供其他对象使用。
                 **/
                val newBitmap = try {
                    bitmap.copy(bitmap.config, true)
                } catch (e: OutOfMemoryError) {
//                    ShareSdkLog.logError(Throwable("fetchBmp oom $uriString"))
                    null
                }
                if (newBitmap == null) {
                    bitmapCallback?.onFail()
                    return
                }
                bitmapCallback?.onSuccess(newBitmap)
            }

        }, executor)
    }
}

/**
 * Fresco 获取图片回调
 */
interface BitmapCallback {
    /**
     * 获取图片成功回调
     *
     * @param bitmap
     */
    fun onSuccess(bitmap: Bitmap)

    /**
     * 获取图片失败回调
     */
    fun onFail()
}