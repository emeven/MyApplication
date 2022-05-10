package com.example.myapplication.extension

import android.animation.Animator
import android.content.res.Resources
import android.graphics.Outline
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.annotation.RequiresApi
import com.example.myapplication.utils.UIUtil
import kotlin.math.roundToInt

val screenWidth = UIUtil.getScreenWidth()

/**
 * sp to float value
 */
inline val Float.spF: Float
    get() {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            this,
            Resources.getSystem().displayMetrics
        )
    }

/**
 * sp to int value
 */
inline val Int.sp: Int
    get() {
        return this.toFloat().sp
    }

/**
 * sp to int value
 */
inline val Float.sp: Int
    get() {
        return spF.toInt()
    }

/**
 * dp to float value
 */
inline val Float.dpF: Float
    get() {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this,
            Resources.getSystem().displayMetrics
        )
    }

/**
 * dp to float value
 */
inline val Int.dpF: Float
    get() {
        return toFloat().dpF
    }

/**
 * dp to int value
 */
inline val Int.dp: Int
    get() {
        return toFloat().dp
    }

/**
 * dp to int value
 */
inline val Float.dp: Int
    get() {
        return dpF.toInt()
    }

/**
 * 四舍五入获取像素
 */
inline val Int.dip: Int
    get() {
        return dpF.roundToInt()
    }

/**
 * 四舍五入获取像素
 */
inline val Int.dipF: Float
    get() {
        return dip.toFloat()
    }

fun View?.isVisible() = this?.visibility == View.VISIBLE

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun View?.setRoundCorner(radius: Float) {
    this?.apply {
        this.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(p0: View?, p1: Outline?) {
                p1?.setRoundRect(0, 0, p0?.width ?: 0, p0?.height ?: 0, radius)
            }
        }
        this.clipToOutline = true
    }
}

/**
 * 设置View的MarginStart
 *
 * @param marginStart
 */
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
fun View?.setMarginStart(marginStart: Int) {
    val param = this?.layoutParams ?: return
    if (param is ViewGroup.MarginLayoutParams) {
        param.marginStart = marginStart
    }
    this.layoutParams = param
}

/**
 * 设置View的MarginEnd
 *
 * @param marginEnd
 */
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
fun View?.setMarginEnd(marginEnd: Int) {
    val param = this?.layoutParams ?: return
    if (param is ViewGroup.MarginLayoutParams) {
        param.marginEnd = marginEnd
    }
    this.layoutParams = param
}

fun View?.setMarginBottom(marginBottom: Int) {
    val param = this?.layoutParams ?: return
    if (param is ViewGroup.MarginLayoutParams) {
        param.setMargins(param.leftMargin, param.topMargin, param.rightMargin, marginBottom)
    }
}

fun View?.setMarginTop(marginTop: Int) {
    val param = this?.layoutParams ?: return
    if (param is ViewGroup.MarginLayoutParams) {
        param.setMargins(param.leftMargin, marginTop, param.rightMargin, param.bottomMargin)
    }
}

inline fun Animator.doOnEnd(crossinline action: (animator: Animator) -> Unit) =
    addListener(onEnd = action)

inline fun Animator.addListener(
    crossinline onStart: (animator: Animator) -> Unit = {},
    crossinline onEnd: (animator: Animator) -> Unit = {},
    crossinline onCancel: (animator: Animator) -> Unit = {},
    crossinline onRepeat: (animator: Animator) -> Unit = {}
): Animator.AnimatorListener {
    val listener = object : Animator.AnimatorListener {
        override fun onAnimationStart(animator: Animator) = onStart(animator)
        override fun onAnimationEnd(animator: Animator) = onEnd(animator)
        override fun onAnimationCancel(animator: Animator) = onCancel(animator)
        override fun onAnimationRepeat(animator: Animator) = onRepeat(animator)
    }
    addListener(listener)
    return listener
}

///**
// * dp to float value
// */
//inline val Float.dpFScale: Float
//    get() {
//        return TypedValue.applyDimension(
//            TypedValue.COMPLEX_UNIT_PX,
//            this * screenWidth / 375,
//            Resources.getSystem().displayMetrics
//        )
//    }
//
///**
// * dp to float value
// */
//inline val Int.dpFScale: Float
//    get() {
//        return toFloat().dpFScale
//    }
//
///**
// * dp to int value
// */
//inline val Int.dpScale: Int
//    get() {
//        return toFloat().dpScale
//    }
//
///**
// * dp to int value
// */
//inline val Float.dpScale: Int
//    get() {
//        return dpFScale.toInt()
//    }


/**
 * dp to float value
 */
fun Float.dpFScale(designWidth: Int = 375): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_PX,
        this * screenWidth / 375,
        Resources.getSystem().displayMetrics
    )
}

/**
 * dp to float value
 */
fun Int.dpFScale(designWidth: Int = 375): Float {
    return toFloat().dpFScale(designWidth)
}

/**
 * dp to int value
 */
fun Int.dpScale(designWidth: Int = 375): Int {
    return toFloat().dpScale(designWidth)
}

/**
 * dp to int value
 */
fun Float.dpScale(designWidth: Int = 375): Int {
    return this.dpFScale(designWidth).toInt()
}

fun View?.hide() {
    this?.visibility = View.GONE
}

fun View?.show() {
    this?.visibility = View.VISIBLE
}