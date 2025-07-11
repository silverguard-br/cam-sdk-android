package com.silverguard.cam.core.model

data class RequestUrlModel(
    val transaction: Transaction,
    val destination_bank: Bank,
    val origin_bank_customer: BankCustomer,
    val destination_bank_customer: BankCustomer
)

data class Transaction(
    val e2e: String,
    val amount: Int,
    val date: String
)

data class Bank(
    val name: String,
    val ispb: String,
    val compe: String
)

data class BankCustomer(
    val name: String,
    val document: String,
    val document_type: String
)