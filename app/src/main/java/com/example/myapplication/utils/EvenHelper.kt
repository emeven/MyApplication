package com.example.myapplication.utils

import android.util.Log

/**
 * logep
 */
inline fun logep(msg: String) {
    Log.d("even_p_1", msg)
}

/**
 * cost
 */
inline fun costTiming(tag: String, callback: () -> Unit) {
    val start = System.currentTimeMillis()
    callback.invoke()
    logep("$tag time = ${System.currentTimeMillis() - start}")
}