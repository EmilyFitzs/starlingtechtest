package com.starling.roundup.service

import com.starling.roundup.clients.SavingsGoalsApiClient
import com.starling.roundup.clients.TransactionFeedApiClientInterface
import com.starling.roundup.clients.model.FeedItem
import com.starling.roundup.clients.model.CurrencyAndAmount
import com.starling.roundup.clients.model.Direction
import com.starling.roundup.clients.model.TransactionSource
import com.starling.roundup.clients.model.TransactionStatus
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.ZonedDateTime

@Component
class RoundUpProcessor(
    private val transactionFeedApiClient: TransactionFeedApiClientInterface,
    private val savingsGoalsApiClient: SavingsGoalsApiClient
) {
    private val logger: Logger = LoggerFactory.getLogger(RoundUpProcessor::class.java)

    private val transactionSourceToRoundUp = listOf(
        TransactionSource.MASTER_CARD,
        TransactionSource.CHEQUE,
        TransactionSource.DIRECT_DEBIT,
        TransactionSource.STARLING_PAY_STRIPE
    )

    fun processRoundUpForWeek(
        accountUid: String,
        categoryUid: String,
        startDate: ZonedDateTime? = null,
        endDate: ZonedDateTime? = null,
        savingsGoalUid: String? = null)
    : Double {
        try {
            val transactions = transactionFeedApiClient.getTransactions(accountUid, categoryUid, startDate, endDate)
            val roundUpAmount = calculateRoundUpAmount(transactions)

            if (roundUpAmount > 0) {
                val savingsGoalUidParam = savingsGoalUid ?: savingsGoalsApiClient.getSavingsGoal(accountUid).savingsGoalUid
                val amountToTransfer = CurrencyAndAmount("GBP", roundUpAmount.times(100).toInt())
                savingsGoalsApiClient.addMoneyToSavingsGoal(accountUid, amountToTransfer, savingsGoalUidParam)
            }
            return roundUpAmount
        } catch (e: Exception) {
            logger.error("Error occurred during round-up processing.", e)
        }
        return 0.0
    }

    private fun calculateRoundUpAmount(transactions: List<FeedItem>): Double {
        var totalRoundUp = BigDecimal.ZERO

        for (transaction in transactions) {
            try {
                if (transaction.status == TransactionStatus.SETTLED &&
                    transaction.direction == Direction.OUT &&
                    transactionSourceToRoundUp.contains(transaction.source)
                ) {
                    val amount = BigDecimal(transaction.amount.minorUnits.toString()).movePointLeft(2)
                    val roundedAmount = amount.setScale(0, RoundingMode.UP)
                    val roundUp = roundedAmount - amount
                    totalRoundUp += roundUp
                }
            } catch (e: Exception) {
                logger.error("Error occurred during round-up calculation for transaction: $transaction", e)
            }
        }

        return totalRoundUp.setScale(2, RoundingMode.HALF_UP).toDouble()
    }
}
