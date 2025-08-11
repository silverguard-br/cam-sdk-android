package com.silverguard.cam.core.config

import android.content.Context
import android.content.Intent
import com.silverguard.cam.CamMainActivity
import com.silverguard.cam.core.model.RequestListUrlModel
import com.silverguard.cam.core.model.RequestUrlModel
import com.silverguard.cam.core.styles.ColorsInterface
import com.silverguard.cam.core.styles.FontsInterface
import com.silverguard.cam.core.styles.Stylesheet
import com.silverguard.cam.init.SilverguardKoinInitializer

object SilverguardCAM {

    private var apiKey: String? = null
    private var isInitialized = false
    private var requestUrlModel: RequestUrlModel? = null
    private var requestListUrlModel: RequestListUrlModel? = null
    private var flow: FLOW = FLOW.CREATE_REQUEST

    fun configure(context: Context, apiKey: String) {
        if (!isInitialized) {
            this.apiKey = apiKey
            this.isInitialized = true
        }
    }

    fun setColors(colors: ColorsInterface) {
        Stylesheet.setColors(colors)
    }

    fun setFonts(fonts: FontsInterface) {
        Stylesheet.setFonts(fonts)
    }

    fun getFlow() = this.flow

    fun getApiKey(): String {
        check(isInitialized) { "SilverguardCAM is not configured. Call configure(context, apiKey) first." }
        return apiKey!!
    }

    fun getRequestUrlModel(): RequestUrlModel {
        check(isInitialized) { "SilverguardCAM is not configured. Call configure(context, apiKey) first." }
        return requestUrlModel ?: throw IllegalStateException("RequestUrlModel is not set. Call launch(context, model) first.")
    }

    fun getRequestListUrlModel(): RequestListUrlModel {
        check(isInitialized) { "SilverguardCAM is not configured. Call configure(context, apiKey) first." }
        return requestListUrlModel ?: throw IllegalStateException("RequestUrlModel is not set. Call launch(context, model) first.")
    }

    fun createRequest(context: Context, model: RequestUrlModel) {
        SilverguardKoinInitializer.init(context)
        this.requestUrlModel = model
        this.flow = FLOW.CREATE_REQUEST
        val intent = Intent(context, CamMainActivity::class.java)
        context.startActivity(intent)
    }

    fun getRequests(context: Context, model: RequestListUrlModel) {
        SilverguardKoinInitializer.init(context)
        this.requestListUrlModel = model
        this.flow = FLOW.GET_REQUESTS
        val intent = Intent(context, CamMainActivity::class.java)
        context.startActivity(intent)
    }
}

enum class FLOW {
    CREATE_REQUEST,
    GET_REQUESTS
}