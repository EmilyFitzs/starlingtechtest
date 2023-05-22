package com.starling.roundup.api.model

enum class TransactionStatus {
    UPCOMING,
    PENDING,
    REVERSED,
    SETTLED,
    DECLINED,
    REFUNDED,
    RETRYING,
    ACCOUNT_CHECK
}
