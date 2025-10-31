package com.silverguard.cam.core.model

import com.google.gson.annotations.SerializedName

data class ResponseUrlModel(
    @SerializedName("data") val data: UrlData
)

data class UrlData(
    @SerializedName("url") val url: String
)