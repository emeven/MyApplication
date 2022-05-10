package com.example.myapplication.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.text.Layout
import android.text.Selection
import android.text.Spannable
import android.text.StaticLayout
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * StaticLayoutTextView 支持正文双击点赞
 * 预渲染StaticLayout
 */
open class StaticLayoutTextView : View {
    private var mLayout: Layout? = null
    private var mWidth = 0
    private var mHeight = 0
    private var interceptSpanClick = false

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    /**
     * 设置已经渲染好的Layout
     *
     * @param layout
     */
    fun setLayout(layout: Layout?) {
        layout?.run {
            mLayout = layout
            if (this.width != mWidth || this.height != mHeight) {
                mWidth = this.width
                mHeight = this.height
                requestLayout()
            }
        }
    }

    private fun processTouchEvent(buffer: Spannable,
                                  event: MotionEvent,layout: Layout): Boolean {
        val action = event.action
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
            val x = event.x.toInt()
            val y = event.y.toInt()
            val line = layout.getLineForVertical(y)
            val off = layout.getOffsetForHorizontal(line, x.toFloat())
            val links = buffer.getSpans(off, off, ClickableSpan::class.java)
            if (links.isNotEmpty()) {
                val link = links[0]
                if (action == MotionEvent.ACTION_UP) {
                    link.onClick(this)
                }
                return true
            } else {
                Selection.removeSelection(buffer)
            }
        }
        return false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val superResult = super.onTouchEvent(event)
        var handled = false
        mLayout?.run {
            if(interceptSpanClick.not() && this.text is Spannable) {
                handled = processTouchEvent(text as Spannable, event,this)
            }
        }
        if(handled) return true
        return superResult
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        mLayout?.run {
            this.draw(canvas, null, null, 0)
        }
        canvas.restore()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (mLayout != null) {
            setMeasuredDimension(mWidth, mLayout!!.height)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    /**
     * setLayoutWidth
     */
    fun setLayoutWidth(width: Int) {
        mWidth = width
        requestLayout()
    }

    /**
     * 拦截Span点击事件
     */
    fun interceptSpanClick(intercept: Boolean){
        interceptSpanClick = intercept
    }
}