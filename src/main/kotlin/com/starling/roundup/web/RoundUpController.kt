package com.starling.roundup.web

import com.starling.roundup.clients.AccountsApiClient
import com.starling.roundup.service.RoundUpProcessor
import com.starling.roundup.web.model.RoundupResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.OffsetDateTime
import java.util.*
import org.springframework.web.bind.annotation.*
import java.time.ZoneOffset

@RestController
class RoundupController(private val roundupProcessor: RoundUpProcessor) {
    @Autowired
    lateinit var accountsApiClient: AccountsApiClient

    @PutMapping("/api/v2/account/{accountUid}/savings-goals/{savingsGoalUid}/roundup/transactions-between")
    fun roundUp(
        @PathVariable("accountUid") accountUid: UUID?,
        @PathVariable("savingsGoalUid") savingsGoalUid: UUID?,
        @RequestParam("minTransactionTimestamp") @DateTimeFormat(iso = DATE_TIME) minTransactionTimestamp: OffsetDateTime?,
        @RequestParam("maxTransactionTimestamp") @DateTimeFormat(iso = DATE_TIME) maxTransactionTimestamp: OffsetDateTime?,
    ): ResponseEntity<RoundupResponse> {
        return try {
            val accountUidStr = accountUid?.toString()
            var accounts = accountsApiClient.getAccounts()
            accounts = accounts.filter { it.accountUid == accountUidStr }
            if (accounts.isEmpty()) {
                val response = RoundupResponse("Account Uid $accountUid does not exist", 0.0)
                ResponseEntity(response, HttpStatus.BAD_REQUEST)
            }
            val account = accounts[0]
            val categoryUid = account.defaultCategory
            val savingsGoalUidStr = savingsGoalUid?.toString()

            // Call the RoundUpProcessor to process the round-up
            val roundUpAmount = roundupProcessor.processRoundUpForWeek(accountUidStr!!, categoryUid, minTransactionTimestamp?.atZoneSameInstant(
                ZoneOffset.UTC), maxTransactionTimestamp?.atZoneSameInstant(ZoneOffset.UTC))

            val response = RoundupResponse("Roundup completed successfully", roundUpAmount)
            ResponseEntity(response, HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            val response = RoundupResponse("Invalid input: ${e.message}", 0.0)
            ResponseEntity(response, HttpStatus.BAD_REQUEST)
        } catch (e: Exception) {
            val response = RoundupResponse("An error occurred: ${e.message}", 0.0)
            ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}