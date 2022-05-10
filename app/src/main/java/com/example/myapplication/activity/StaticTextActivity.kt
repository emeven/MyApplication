package com.example.myapplication.activity

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import android.os.Bundle
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.LeadingMarginSpan
import com.example.myapplication.extension.dp
import com.example.myapplication.extension.dpF
import com.example.myapplication.utils.logep
import com.example.myapplication.widget.StaticLayoutTextFactory
import kotlinx.android.synthetic.main.activity_static_text.*

class StaticTextActivity : AppCompatActivity() {

    private val textPaint by lazy {
        TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 10.dpF
            density = resources.displayMetrics.density
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_static_text)

        val title = "A.H.C   ·韩国防晒霜新款小蓝瓶防晒隔离霜SPF50面部防水防汗清爽控油"

        val spannableString = SpannableString(title)
        spannableString.setSpan(
            LeadingMarginSpan.Standard(20.dp, 0),
            0,
            spannableString.length,
            SpannableString.SPAN_INCLUSIVE_INCLUSIVE
        )

        val layout = StaticLayoutTextFactory.initLayout(
            title,
            resources.getColor(R.color.xhsTheme_colorGrayLevel1),
            textSize = 14.dpF,
            context = this
        )
        static_title.setLayout(layout)
        static_title.setLayoutWidth(180.dp)

        text_title.text = spannableString

        lottie.setAnimation("anim/data.json")
//        lottie.setAnimation("anim/live_a.json")
        lottie.addAnimatorUpdateListener {
            logep("lottie.progress = ${lottie.progress}")
        }
//        lottie.playAnimation()
        lottie.progress = 0.99f
//        lottie.progress = 1f


        val string = "dfnienv出现的那件事jdkenvcds你才能几点上课jkfdjfnv"
        tv1.text = string

        val string2 = string
        val s2 = string.dropLastWhile {
            logep(
                string2.subSequence(0, string2.lastIndexOf(it)).toString() + " ${
                    textPaint.measureText(
                        string2.subSequence(0, string2.lastIndexOf(it)).toString()
                    )
                }"
            )
//            string2.dropWhile {  }
            textPaint.measureText(string2.subSequence(0, string2.lastIndexOf(it)).toString()) <= 100.dp
        }
        tv2.text = s2
        logep(s2)

    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, StaticTextActivity::class.java)
            context.startActivity(intent)
        }
    }
}