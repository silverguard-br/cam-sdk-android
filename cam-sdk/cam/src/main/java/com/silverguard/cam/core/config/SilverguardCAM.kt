package com.silverguard.cam.core.config

import android.content.Context
import android.content.Intent
import com.silverguard.cam.CamMainActivity
import com.silverguard.cam.core.model.CAMRequestListUrlModel
import com.silverguard.cam.core.model.CAMRequestUrlModel
import com.silverguard.cam.core.styles.CAMColorsInterface
import com.silverguard.cam.core.styles.CAMFontsInterface
import com.silverguard.cam.core.styles.Stylesheet
import com.silverguard.cam.init.SilverguardCamKoinInitializer

object SilverguardCAM {

    private var apiKey: String? = null
    private var isInitialized = false
    private var CAMRequestUrlModel: CAMRequestUrlModel? = null
    private var CAMRequestListUrlModel: CAMRequestListUrlModel? = null
    private var flow: FLOW = FLOW.CREATE_REQUEST

    fun configure(context: Context, apiKey: String) {
        if (!isInitialized) {
            this.apiKey = apiKey
            this.isInitialized = true
        }
    }

    fun setColors(colors: CAMColorsInterface) {
        Stylesheet.setCAMColors(colors)
    }

    fun setFonts(fonts: CAMFontsInterface) {
        Stylesheet.setCAMFonts(fonts)
    }

    fun getFlow() = this.flow

    fun getApiKey(): String {
        check(isInitialized) { "SilverguardCAM is not configured. Call configure(context, apiKey) first." }
        return apiKey!!
    }

    fun getRequestUrlModel(): CAMRequestUrlModel {
        check(isInitialized) { "SilverguardCAM is not configured. Call configure(context, apiKey) first." }
        return CAMRequestUrlModel ?: throw IllegalStateException("RequestUrlModel is not set. Call launch(context, model) first.")
    }

    fun getRequestListUrlModel(): CAMRequestListUrlModel {
        check(isInitialized) { "SilverguardCAM is not configured. Call configure(context, apiKey) first." }
        return CAMRequestListUrlModel ?: throw IllegalStateException("RequestUrlModel is not set. Call launch(context, model) first.")
    }

    fun createRequest(context: Context, model: CAMRequestUrlModel) {
        SilverguardCamKoinInitializer.init(context)
        this.CAMRequestUrlModel = model
        this.flow = FLOW.CREATE_REQUEST
        val intent = Intent(context, CamMainActivity::class.java)
        context.startActivity(intent)
    }

    fun getRequests(context: Context, model: CAMRequestListUrlModel) {
        SilverguardCamKoinInitializer.init(context)
        this.CAMRequestListUrlModel = model
        this.flow = FLOW.GET_REQUESTS
        val intent = Intent(context, CamMainActivity::class.java)
        context.startActivity(intent)
    }
}

enum class FLOW {
    CREATE_REQUEST,
    GET_REQUESTS
}