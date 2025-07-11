package com.silverguard.cam.init

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

object SilverguardKoinInitializer {
    fun init(context: Context) {
        if (org.koin.core.context.GlobalContext.getOrNull() == null) {
            startKoin {
                androidContext(context)
                modules(com.silverguard.cam.di.modules)
            }
        }
    }
}