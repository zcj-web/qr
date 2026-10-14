package com.zcj.qr

import android.app.Application
import android.content.Context

class QrApp : Application() {
    companion object {
        @Volatile
        lateinit var context: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            android.util.Log.e("QrApp", "FATAL CRASH: $throwable", throwable)
            throwable.printStackTrace()
        }
    }
}
