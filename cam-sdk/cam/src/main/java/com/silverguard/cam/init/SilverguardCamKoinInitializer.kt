package com.silverguard.cam.init

import android.content.Context
import com.silverguard.cam.di.modules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

object SilverguardCamKoinInitializer {
    fun init(context: Context) {
        if (GlobalContext.getOrNull() == null) {
            startKoin {
                androidContext(context)
                modules(modules)
            }
        } else {
            GlobalContext.get().loadModules(
                modules
            )
        }
    }
}