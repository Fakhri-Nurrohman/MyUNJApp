package com.fakhrinurrohman.myunjapp

import android.app.Application
import com.fakhrinurrohman.myunjapp.data.appContext
import com.fakhrinurrohman.myunjapp.di.initKoin
import org.koin.android.ext.koin.androidContext

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext // Initialize context for Room
        
        initKoin {
            androidContext(this@MyApplication)
        }
    }
}
