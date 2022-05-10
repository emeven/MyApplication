package com.example.myapplication.activity

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.AbsoluteSizeSpan
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.extension.dip
import com.example.myapplication.extension.dipF
import com.example.myapplication.extension.dp
import com.example.myapplication.extension.dpF
import com.example.myapplication.utils.logep
import com.example.myapplication.utils.span.CustomPriceSpan
import kotlinx.android.synthetic.main.activity_commodity_card.*

class CommodityCardActivity : AppCompatActivity() {

    private val numberFont by lazy {
        Typeface.createFromAsset(
            this.assets,
            "fonts/REDNumber-Medium.ttf"
        )
    }

    // A.H.C   ·韩国防晒霜新款小蓝瓶防晒隔离霜SPF50面部防水防汗清爽控油
    // 肆月·复古咖啡杯女生高颜值水杯高级感陶瓷马克杯日式ins高档精致

    private val textPaint by lazy {
        TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 11.dpF
            density = resources.displayMetrics.density
            typeface = numberFont
        }
    }

    private val originPaint by lazy {
        TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 9.dpF
            density = resources.displayMetrics.density
            typeface = numberFont
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_commodity_card)

        initText()
    }

    private fun initText() {
        val text = "¥8888"
//        val text = "¥5000"
        val originalPrice = "¥8888"
        val spannableString = SpannableStringBuilder()
        spannableString.append(text)
        spannableString.setSpan(AbsoluteSizeSpan(8.dp), 0, 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)

        logep("content width = ${textPaint.measureText(text) + originPaint.measureText(originalPrice) + 4.dp + 8.dp}, 74.dip = ${74.dip}")
        logep("text = ${textPaint.measureText(text)}, originalPrice = ${originPaint.measureText(originalPrice)}, 4.dp = ${4.dp}, 8.dp = ${8.dp}, 74.dp = ${74.dp}")

        if (originalPrice.isNotBlank() && (textPaint.measureText(text) + originPaint.measureText(originalPrice) + 4.dp + 8.dp <= 74.dp)) {
            spannableString.append(originalPrice)
            val verticalCenterSpan =
                CustomPriceSpan(9.dp, resources.getColor(R.color.xhsTheme_colorGray400))
            verticalCenterSpan.setMarginStart(4.dp)
            verticalCenterSpan.setStrikethroughSpan(true)
            verticalCenterSpan.setBottomInterval(2.dpF)
            spannableString.setSpan(
                verticalCenterSpan,
                text.length,
                text.length + originalPrice.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        top_text.text = spannableString
        top_text.typeface = numberFont
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, CommodityCardActivity::class.java)
            context.startActivity(intent)
        }
    }
}