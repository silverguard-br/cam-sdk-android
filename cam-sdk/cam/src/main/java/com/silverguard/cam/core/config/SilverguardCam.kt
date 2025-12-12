package com.silverguard.cam.core.config

import android.content.Context
import android.content.Intent
import com.silverguard.cam.CamMainActivity
import com.silverguard.cam.core.model.CamRequestListUrlModel
import com.silverguard.cam.core.model.CamRequestUrlModel
import com.silverguard.cam.core.styles.CamColorsInterface
import com.silverguard.cam.core.styles.CamFontsInterface
import com.silverguard.cam.core.styles.Stylesheet
import com.silverguard.cam.init.SilverguardCamKoinInitializer

object SilverguardCam {

    private var apiKey: String? = null
    private var isInitialized = false
    private var CamRequestUrlModel: CamRequestUrlModel? = null
    private var CamRequestListUrlModel: CamRequestListUrlModel? = null
    private var flow: FLOW = FLOW.CREATE_REQUEST
    private var env: String = ENVIRONMENT.DEBUG.name
    private var baseUrl: String = ""

    fun configure(context: Context, apiKey: String, environment: String) {
        if (!isInitialized) {
            SilverguardCamKoinInitializer.init(context)
            this.apiKey = apiKey
            this.isInitialized = true
            setEnvironment(env = environment)
        }
    }

    fun setColors(colors: CamColorsInterface) {
        Stylesheet.setCamColors(colors)
    }

    fun setFonts(fonts: CamFontsInterface) {
        Stylesheet.setCamFonts(fonts)
    }

    fun getFlow(): FLOW = this.flow

    fun getApiKey(): String {
        check(isInitialized) { "SilverguardCAM is not configured. Call configure(context, apiKey) first." }
        return apiKey!!
    }

    fun getRequestUrlModel(): CamRequestUrlModel {
        check(isInitialized) { "SilverguardCAM is not configured. Call configure(context, apiKey) first." }
        return CamRequestUrlModel ?: throw IllegalStateException("RequestUrlModel is not set. Call launch(context, model) first.")
    }

    fun getRequestListUrlModel(): CamRequestListUrlModel {
        check(isInitialized) { "SilverguardCAM is not configured. Call configure(context, apiKey) first." }
        return CamRequestListUrlModel ?: throw IllegalStateException("RequestUrlModel is not set. Call launch(context, model) first.")
    }

    fun createRequest(context: Context, model: CamRequestUrlModel) {
        this.CamRequestUrlModel = model
        this.flow = FLOW.CREATE_REQUEST
        val intent = Intent(context, CamMainActivity::class.java)
        context.startActivity(intent)
    }

    fun getRequests(context: Context, model: CamRequestListUrlModel) {
        this.CamRequestListUrlModel = model
        this.flow = FLOW.GET_REQUESTS
        val intent = Intent(context, CamMainActivity::class.java)
        context.startActivity(intent)
    }

    fun setEnvironment(env: String) {
        this.env = env.uppercase()
        this.baseUrl = when (this.env) {
            ENVIRONMENT.DEBUG.name -> "https://test.camapi.sosgolpe.com.br/"
            ENVIRONMENT.STAGING.name -> "https://test.camapi.sosgolpe.com.br/"
            ENVIRONMENT.PRODUCTION.name -> "https://api.cam.silverguard.com.br/"
            else -> ""
        }
    }

    fun getBaseUrl(): String {
        check(isInitialized) { "SilverguardCAM is not configured. Call configure(context, apiKey) first." }
        return baseUrl
    }
}

enum class FLOW {
    CREATE_REQUEST,
    GET_REQUESTS
}

enum class ENVIRONMENT {
    DEBUG,
    STAGING,
    PRODUCTION
}