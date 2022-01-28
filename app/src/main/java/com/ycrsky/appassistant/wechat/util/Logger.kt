package com.ycrsky.appassistant.wechat.util

import android.util.Log


object Logger {

    private const val TAG = "AppAssitant"

    @JvmStatic
    fun d(message: String) {
        Log.d(TAG, message)
    }

    @JvmStatic
    fun e(message: String) {
        Log.d(TAG, message)
    }
}