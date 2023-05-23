package com.starling.roundup.clients.model

import java.time.LocalTime

data class FeedItem(
    val feedItemUid: String,
    val categoryUid: String,
    val amount: CurrencyAndAmount,
    val sourceAmount: CurrencyAndAmount,
    val direction: Direction,
    val updatedAt: String,
    val transactionTime: String,
    val settlementTime: String,
    val source: TransactionSource,
    val status: TransactionStatus,
    val transactingApplicationUserUid: String?,
    val counterPartyType: String,
    val counterPartyUid: String?,
    val counterPartyName: String,
    val counterPartySubEntityUid: String?,
    val counterPartySubEntityName: String,
    val counterPartySubEntityIdentifier: String,
    val counterPartySubEntitySubIdentifier: String,
    val reference: String,
    val country: String,
    val spendingCategory: String,
    val hasAttachment: Boolean,
    val hasReceipt: Boolean,
    val batchPaymentDetails: Any? // Change to appropriate type if needed
)


data class MasterCardFeedItemData(
    val description: String,
    val merchantIdentifier: String,
    val mcc: Int,
    val posTimestamp: LocalTime,
    val authorisationCode: String,
    val cardLast4: String
)
