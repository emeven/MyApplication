package com.example.myapplication.activity

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.isDigitsOnly
import com.example.myapplication.R
import com.example.myapplication.extension.*
import com.example.myapplication.utils.BitmapCallback
import com.example.myapplication.utils.ShareBitmapHelper
import com.example.myapplication.widget.ShopCartBubbleView
import com.uber.autodispose.ScopeProvider
import kotlinx.android.synthetic.main.activity_tab_bar_test.*

class TabBarTestActivity : AppCompatActivity() {

    private var shopCartBubbleView: ShopCartBubbleView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab_bar_test)

        initPrice()

        initClicks()

        showBubbleGuide()
    }

    private fun initPrice() {
        val string = "mallOrderPackageTarget.packageId"
        // 分割 string
        val list = string.split('.')
        list.forEach {
            Log.i("even", it)
        }

        val s1 = "90"
        val s3 = "90.00"
        val s2 = "a90"
        s2.isDigitsOnly()
        Log.i("even", "s1 = ${s1.isDigitsOnly()}, s3 = ${s3.isDigitsOnly()}, s2 = ${s2.isDigitsOnly()}")

        val salePrice = "234.09"
        val spanString = SpannableString("¥ $salePrice")
        spanString.setSpan(AbsoluteSizeSpan(12.dp), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val pointIndex = spanString.indexOf('.')
        if (pointIndex >= 0) {
            spanString.setSpan(AbsoluteSizeSpan(12.dp), pointIndex, spanString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        testPrice.text = spanString
    }

    private fun initClicks() {
        anchorView.throttleClicks().subscribeWithCrash(ScopeProvider.UNBOUND) {
            shopCartBubbleView?.dismiss()
        }
    }

    private fun showBubbleGuide() {
        val shopGuideImage =
            "https://qimg.xiaohongshu.com/arkgoods/235318df3aea7db96f73d4533a7a6dfebd79a980?itemId=61c2f70f333ff6000170971e&imageView2/1/w/320/h/320/q/90.jpg"
        fetchBitmap(shopGuideImage) {
            anchorView.post {
                shopCartBubbleView =
                    ShopCartBubbleView(this, content = "查看购物车", anchorView = anchorView, drawable = BitmapDrawable(resources, it)) {
                        shopCartBubbleView = null
                    }
                shopCartBubbleView?.display()
                bitmapIv.setImageDrawable(BitmapDrawable(resources, it).apply {
                    setBounds(0, 0, 60.dp, 60.dp)
                })
            }
        }
    }

    private fun fetchBitmap(url: String, onSuccessAction: (Bitmap) -> Unit) {
        ShareBitmapHelper.fetchBmp(url, object : BitmapCallback {
            override fun onSuccess(bitmap: Bitmap) {
                onSuccessAction(convert2CircleBitmap(bitmap, 24.dp))
            }

            override fun onFail() {
                //do nothing
            }
        })
    }

    private fun convert2CircleBitmap(bitmap: Bitmap, newWidth: Int): Bitmap {
        val paint = Paint()
        paint.isAntiAlias = true
        val width = bitmap.width

        // scale bitmap
        val scaleWidth = newWidth.toFloat() / width
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleWidth)
        val scaleBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, width, matrix, true)

        val circleBitmap = Bitmap.createBitmap(newWidth, newWidth, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(circleBitmap)
        canvas.drawCircle(newWidth.toFloat() / 2, newWidth.toFloat() / 2, newWidth.toFloat() / 2, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(scaleBitmap, 0f, 0f, paint)

        // 灰色描边
        val paintAvatarBorder = Paint().apply {
            color =
                ResourcesCompat.getColor(resources, R.color.xhsTheme_colorGrayLevel1_alpha_5, null)
            style = Paint.Style.STROKE
            strokeWidth = 1.dpF
            isAntiAlias = true
        }
        val radius = newWidth / 2f
        canvas.drawCircle(radius, radius, radius - (0.5f).dpF, paintAvatarBorder)
        return circleBitmap
    }

    private fun scaleBitmap() {

    }

    override fun onResume() {
        super.onResume()
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, TabBarTestActivity::class.java)
            context.startActivity(intent)
        }
    }
}