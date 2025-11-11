package com.silverguard.cam.core.model

data class CamRequestListUrlModel(
    val reporter_client_id: String,
    val reporter_branch_number: Int? = null,
    val reporter_account_number: Int? = null
)