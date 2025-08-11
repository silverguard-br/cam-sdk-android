package com.silverguard.cam.core.model

data class RequestUrlModel(
    val transaction_id: String,
    val transaction_amount: Double? = null,
    val transaction_time: String? = null,
    val transaction_description: String? = null,
    val reporter_client_name: String? = null,
    val reporter_client_id: Long? = null,
    val contested_participant_id: String? = null,
    val counterparty_client_name: String? = null,
    val counterparty_client_id: Long? = null,
    val counterparty_client_key: String? = null,
    val protocol_id: String? = null,
    val pix_auto: Boolean? = null,
    val client_id: String? = null,
    val client_since: String? = null,
    val client_birth: String? = null,
    val autofraud_risk: Boolean? = null,
)