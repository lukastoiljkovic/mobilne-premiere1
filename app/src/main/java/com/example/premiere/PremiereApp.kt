package com.example.premiere

import android.app.Application
import com.example.premiere.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PremiereApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PremiereApp)
            modules(appModule)
        }
    }
}