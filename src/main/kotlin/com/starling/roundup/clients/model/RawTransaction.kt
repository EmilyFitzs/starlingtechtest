package com.starling.roundup.clients.model

data class RawTransaction(
    val id: String,
    val amount: Double,
    val date: String,
    val category: String
)
