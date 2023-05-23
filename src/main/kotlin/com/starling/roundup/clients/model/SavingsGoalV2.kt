package com.starling.roundup.clients.model

data class SavingsGoalV2(
    var savingsGoalUid: String,
    var name: String,
    var target: CurrencyAndAmount?,
    val description: String,
    var totalSaved: CurrencyAndAmount?,
    var savedPercentage: Int?
)
