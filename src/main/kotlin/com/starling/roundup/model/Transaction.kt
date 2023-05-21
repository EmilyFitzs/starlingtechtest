package com.starling.roundup.model

import java.time.LocalDate

data class Transaction(
    val transactionId: String,
    val amount: Double,
    val date: LocalDate,
    val category: String
)