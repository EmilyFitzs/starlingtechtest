package com.starling.roundup.api.model

data class SavingsGoalV2(
    var savingsGoalUid: String,
    var name: String,
    var target: CurrencyAndAmount?,
    var totalSaved: CurrencyAndAmount?,
    var savedPercentage: Int?
)
