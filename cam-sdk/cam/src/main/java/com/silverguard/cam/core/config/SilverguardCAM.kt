package com.silverguard.cam.core.config

import android.content.Context
import android.content.Intent
import com.silverguard.cam.CamMainActivity
import com.silverguard.cam.core.model.RequestUrlModel
import com.silverguard.cam.init.SilverguardKoinInitializer

object SilverguardCAM {

    private var apiKey: String? = null
    private var isInitialized = false
    private var model: RequestUrlModel? = null

    fun configure(context: Context, apiKey: String) {
        if (!isInitialized) {
            this.apiKey = apiKey
            this.isInitialized = true
        }
    }

    fun getApiKey(): String {
        check(isInitialized) { "SilverguardCAM is not configured. Call configure(context, apiKey) first." }
        return apiKey!!
    }

    fun getRequestUrlModel(): RequestUrlModel {
        check(isInitialized) { "SilverguardCAM is not configured. Call configure(context, apiKey) first." }
        return model ?: throw IllegalStateException("RequestUrlModel is not set. Call launch(context, model) first.")
    }

    fun launch(context: Context, model: RequestUrlModel) {
        SilverguardKoinInitializer.init(context)
        this.model = model
        val intent = Intent(context, CamMainActivity::class.java)
        context.startActivity(intent)
    }
}