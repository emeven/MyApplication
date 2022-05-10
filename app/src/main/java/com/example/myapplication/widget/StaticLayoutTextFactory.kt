package com.example.myapplication.widget

import android.content.Context
import android.graphics.Paint
import android.os.Build
import android.text.*
import android.util.Log
import androidx.annotation.ColorInt
import com.example.myapplication.extension.dp
import com.example.myapplication.extension.dpF
import java.lang.reflect.InvocationTargetException


/**
 * StaticLayoutTextFactory
 */
object StaticLayoutTextFactory {

    private var hardCodeWidth = 180.dp

//    private val fontCache by lazy { TypefaceUtils.createTypeface(XYUtilsCenter.getApp(), Typeface.BOLD) }
//    private val normalFontCache by lazy { TypefaceUtils.createTypeface(XYUtilsCenter.getApp(), Typeface.NORMAL) }

    private const val maxLine = 2

    /**
     * 初始化staticLayout
     *
     * @param text
     * @param textColor
     * @param textSize
     * @param spacingAddition
     * @return
     */
    fun initLayout(text: CharSequence, @ColorInt textColor: Int, textSize:Float = 15.dpF, spacingAddition:Float =  4.dpF, includePad: Boolean = true, isBold: Boolean = true, context: Context): StaticLayout? {

        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        textPaint.density = context.resources.displayMetrics.density

        textPaint.textSize = textSize
//        textPaint.typeface = if (isBold) fontCache else normalFontCache
        textPaint.color = textColor

        var layout: StaticLayout? = null
        var richText = SpannableStringBuilder()
        try {
            //fix https://bugly.qq.com/v2/crash-reporting/crashes/900014990/3163538?pid=1 富文本里面的逻辑太复杂，暂时的修复策略
            richText = if (text is String) SpannableStringBuilder(text) else {
                if (text is SpannableStringBuilder) text else richText
            }
        } catch (e:StringIndexOutOfBoundsException) {
            Log.d("even_p", "$e")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            layout = StaticLayout.Builder.obtain(richText, 0, richText.length, textPaint, hardCodeWidth)
                    .setMaxLines(maxLine)
                    .setEllipsize(TextUtils.TruncateAt.END)
                    .setLineSpacing(spacingAddition, 1f)
                    .setIncludePad(includePad)
                    .setIndents(arrayListOf(4.dp).toIntArray(), listOf<Int>(0).toIntArray())
                    .build()
        } else {
            try {
                val constructor = Class.forName(StaticLayout::class.java.name)
                        .getConstructor(CharSequence::class.java, Int::class.javaPrimitiveType, Int::class.javaPrimitiveType,
                                TextPaint::class.java, Int::class.javaPrimitiveType, Layout.Alignment::class.java, TextDirectionHeuristic::class.java,
                                Float::class.javaPrimitiveType, Float::class.javaPrimitiveType, Boolean::class.javaPrimitiveType, TextUtils.TruncateAt::class.java, Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
                layout = constructor.newInstance(richText, 0, richText.length,
                        textPaint, hardCodeWidth, Layout.Alignment.ALIGN_NORMAL, TextDirectionHeuristics.FIRSTSTRONG_LTR,
                        1.0f, spacingAddition, includePad, TextUtils.TruncateAt.END, hardCodeWidth, maxLine) as StaticLayout
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InstantiationException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            }

        }

        return layout
    }
}
