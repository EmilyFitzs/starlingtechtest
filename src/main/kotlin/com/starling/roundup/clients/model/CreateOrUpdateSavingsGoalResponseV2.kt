package com.starling.roundup.clients.model

data class CreateOrUpdateSavingsGoalResponseV2(
    val description: String,
    val savingsGoalUid: String,
    val success: Boolean
)
