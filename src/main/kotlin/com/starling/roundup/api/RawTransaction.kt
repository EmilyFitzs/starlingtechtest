package com.starling.roundup.api

data class RawTransaction(
    val id: String,
    val amount: Double,
    val date: String,
    val category: String
)
