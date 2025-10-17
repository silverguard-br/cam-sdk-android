package com.silverguard.sample

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

object SampleKoinInitializer {
    fun init(context: Context) {
        if (org.koin.core.context.GlobalContext.getOrNull() == null) {
            startKoin {
                androidContext(context)
                modules()
            }
        }
    }
}