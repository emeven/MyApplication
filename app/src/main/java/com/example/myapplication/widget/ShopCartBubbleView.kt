package com.example.myapplication.widget

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.example.myapplication.R
import com.example.myapplication.extension.*
import com.example.myapplication.utils.UIUtil
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.matrix_shop_cart_guide.view.*
import java.lang.ref.WeakReference

/**
 * 加购后购物tab 气泡引导
 *
 * @property content
 *
 * @constructor
 * @param context
 * @param attrs
 * @param defStyleAttr
 */
@SuppressLint("ViewConstructor")
class ShopCartBubbleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    val content: String, val drawable: Drawable,
    anchorView: View, val dismissCallback: () -> Unit
) : LinearLayout(context, attrs, defStyleAttr) {

    private val MESSAGE_DISAPPEAR = 1

    private var tempRect = Rect()
    private var bubbleRect = Rect()

    private val handler by lazy { ShopCartBubbleHandler(this) }

    private var hasDisplay = false

    val bubbleClicks = PublishSubject.create<Unit>()

    init {
        LayoutInflater.from(context).inflate(R.layout.matrix_shop_cart_guide, this, true)
        anchorView.getGlobalVisibleRect(tempRect)

        val measurePaint = Paint()
        measurePaint.textSize = (12.sp).toFloat()
        val bubbleTextWidth = measurePaint.measureText(content) + (24 + 4 + 4 + 8).dp

        val tabBarWidth = UIUtil.getScreenWidth() / 5
        (shopGuideLayout.layoutParams as LayoutParams).apply {
            setMargins((tabBarWidth * 1.5f - bubbleTextWidth / 2).toInt(), 0, 0, tempRect.height())
        }
    }

    /**
     * display bubble
     */
    fun display() {
        if (content.isBlank()) {
            shopGuideLayout.hide()
            return
        }

        shopGuideLayout.show()
        hasDisplay = true
        drawable.setBounds(0, 0, 24.dp, 24.dp)
        guideContent.setCompoundDrawables(drawable, null, null, null)
        guideContent.text = content

        val rootView = (context as Activity).findViewById<View>(android.R.id.content) as FrameLayout
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            Gravity.BOTTOM
        )
        rootView.addView(this, params)

//        handler.sendEmptyMessageDelayed(MESSAGE_DISAPPEAR, 3000L)
    }

    /**
     * dismiss bubble
     */
    fun dismiss() {
        handler.removeMessages(MESSAGE_DISAPPEAR)
        handler.sendEmptyMessage(MESSAGE_DISAPPEAR)
    }

    /**
     * isDisplay
     */
    fun isDisplay() = hasDisplay && isVisible()

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return true
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.actionMasked
        val eventX = event.rawX.toInt()
        val eventY = event.rawY.toInt()

        shopGuideLayout.getGlobalVisibleRect(bubbleRect)

        if (action == MotionEvent.ACTION_DOWN) {
            if (bubbleRect.contains(eventX, eventY)) {
                bubbleClicks.onNext(Unit)
                dismiss()
                return true
            }
        }
        dismiss()
        return false
    }

    /**
     * Handler
     *
     * @constructor
     * @param view
     */
    private class ShopCartBubbleHandler(view: View) : Handler() {

        private val viewWeakReference by lazy { WeakReference(view) }

        override fun handleMessage(message: Message) {
            val view = viewWeakReference.get() as? ShopCartBubbleView
            view?.let {
                if (it.isVisible()) {
                    it.hide()
                }
                it.dismissCallback.invoke()
            }
        }
    }
}