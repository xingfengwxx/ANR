package com.wangxingxing.anr

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class MyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("wxx","onReceive")
        Thread.sleep(100000)
    }
}