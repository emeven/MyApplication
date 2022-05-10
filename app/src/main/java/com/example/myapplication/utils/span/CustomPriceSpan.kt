package com.example.myapplication.utils.span

import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextPaint
import android.text.style.ReplacementSpan
import kotlin.math.roundToInt

/**
 * 价格 Span, 支持设置字号、颜色、左间距、底部间距、划线
 */
class CustomPriceSpan(private val fontSize: Int, private val textColor: Int) : ReplacementSpan() {

    private var marginStart = 0

    private var isStrikethroughSpan = false

    private var bottomInterval: Float = 0f

    override fun getSize(paint: Paint, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        val newPaint = getCustomTextPaint(paint)
        return (newPaint.measureText(text, start, end) + marginStart).toInt()
    }

    override fun draw(canvas: Canvas, text: CharSequence?, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
        val newPaint = getCustomTextPaint(paint)
        val xPos = (x + marginStart).roundToInt().toFloat()
        val yPos = y - bottomInterval / 3

        canvas.drawText(text.toString(), start, end, xPos, yPos, newPaint)
    }

    private fun getCustomTextPaint(srcPaint: Paint): TextPaint {
        val textPaint = TextPaint(srcPaint)
        if (fontSize > 0) {
            textPaint.textSize = fontSize.toFloat()
        }
        textPaint.color = textColor
        if (isStrikethroughSpan) {
            textPaint.flags = Paint.STRIKE_THRU_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG
        }
        return textPaint
    }

    /**
     * setMarginStart
     */
    fun setMarginStart(marginStart: Int) {
        this.marginStart = marginStart
    }

    /**
     * setStrikethroughSpan
     */
    fun setStrikethroughSpan(isStrikethroughSpan: Boolean) {
        this.isStrikethroughSpan = isStrikethroughSpan
    }

    /**
     * setBottomInterval
     */
    fun setBottomInterval(bottomInterval: Float) {
        this.bottomInterval = bottomInterval
    }
}