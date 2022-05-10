package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.activity.CommodityCardActivity
import com.example.myapplication.activity.StaticTextActivity
import com.example.myapplication.activity.TabBarTestActivity
import com.facebook.drawee.backends.pipeline.Fresco
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fresco.initialize(this)
        setContentView(R.layout.activity_main)

        initClicks()
    }

    private fun initClicks() {
        text_tabbar_btn.setOnClickListener {
            TabBarTestActivity.start(this)
        }

        text_commodity_btn.setOnClickListener {
            CommodityCardActivity.start(this)
        }

        text_static_text_btn.setOnClickListener {
            StaticTextActivity.start(this)
        }
    }
}