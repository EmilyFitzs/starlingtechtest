package com.starling.roundup.web

import com.starling.roundup.clients.Account
import com.starling.roundup.clients.AccountsApiClient
import com.starling.roundup.service.RoundUpProcessor
import com.starling.roundup.web.model.RoundupResponse
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

class RoundupControllerTest {
    private lateinit var roundupProcessor: RoundUpProcessor
    private lateinit var accountsApiClient: AccountsApiClient
    private lateinit var roundupController: RoundupController

    @BeforeEach
    fun setup() {
        roundupProcessor = mockk(relaxed = true)
        accountsApiClient = mockk(relaxed = true)
        roundupController = RoundupController(roundupProcessor)
        roundupController.accountsApiClient = accountsApiClient
    }

    @AfterEach
    fun cleanup() {
        clearMocks(roundupProcessor, accountsApiClient)
    }

    @Test
    fun `roundUp should return successful response when round-up is processed successfully`() {
        // Arrange
        val accountUid = UUID.randomUUID()
        val savingsGoalUid = UUID.randomUUID()
        val minTransactionTimestamp = OffsetDateTime.now()
        val maxTransactionTimestamp = OffsetDateTime.now()

        every { accountsApiClient.getAccounts() } returns listOf(
            Account(
                accountUid.toString(),
                "type",
                "defaultCategory",
                "currency",
                "createdAt",
                "name"
            )
        )
        every {
            roundupProcessor.processRoundUpForWeek(
                accountUid.toString(),
                "defaultCategory",
                minTransactionTimestamp.atZoneSameInstant(ZoneOffset.UTC),
                maxTransactionTimestamp.atZoneSameInstant(ZoneOffset.UTC)
            )
        } returns 10.0

        // Act
        val response = roundupController.roundUp(
            accountUid,
            savingsGoalUid,
            minTransactionTimestamp,
            maxTransactionTimestamp
        )

        // Assert
        verify(exactly = 1) {
            accountsApiClient.getAccounts()
            roundupProcessor.processRoundUpForWeek(
                accountUid.toString(),
                "defaultCategory",
                minTransactionTimestamp.atZoneSameInstant(ZoneOffset.UTC),
                maxTransactionTimestamp.atZoneSameInstant(ZoneOffset.UTC)
            )
        }
        confirmVerified(accountsApiClient, roundupProcessor)

        val expectedResponse = RoundupResponse("Roundup completed successfully", 10.0)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.body == expectedResponse)
    }
    @Test
    fun `roundUp should return bad request response when an IllegalArgumentException is thrown`() {
        // Arrange
        val accountUid = UUID.randomUUID()
        val savingsGoalUid = UUID.randomUUID()
        val minTransactionTimestamp = OffsetDateTime.now()
        val maxTransactionTimestamp = OffsetDateTime.now()

        every { accountsApiClient.getAccounts() } returns listOf(
            Account(
                accountUid.toString(),
                "type",
                "defaultCategory",
                "currency",
                "createdAt",
                "name"
            )
        )
        every {
            roundupProcessor.processRoundUpForWeek(
                accountUid.toString(),
                "defaultCategory",
                minTransactionTimestamp.atZoneSameInstant(ZoneOffset.UTC),
                maxTransactionTimestamp.atZoneSameInstant(ZoneOffset.UTC)
            )
        } throws IllegalArgumentException("Invalid input")

        // Act
        val response = roundupController.roundUp(
            accountUid,
            savingsGoalUid,
            minTransactionTimestamp,
            maxTransactionTimestamp
        )

        // Assert
        verify(exactly = 1) {
            accountsApiClient.getAccounts()
            roundupProcessor.processRoundUpForWeek(
                accountUid.toString(),
                "defaultCategory",
                minTransactionTimestamp.atZoneSameInstant(ZoneOffset.UTC),
                maxTransactionTimestamp.atZoneSameInstant(ZoneOffset.UTC)
            )
        }
        confirmVerified(accountsApiClient, roundupProcessor)

        val expectedResponse = RoundupResponse("Invalid input: Invalid input", 0.0)
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
        assert(response.body == expectedResponse)
    }

    @Test
    fun `roundUp should return internal server error response when an exception is thrown`() {
        // Arrange
        val accountUid = UUID.randomUUID()
        val savingsGoalUid = UUID.randomUUID()
        val minTransactionTimestamp = OffsetDateTime.now()
        val maxTransactionTimestamp = OffsetDateTime.now()

        every { accountsApiClient.getAccounts() } throws Exception("An error occurred")

        // Act
        val response = roundupController.roundUp(
            accountUid,
            savingsGoalUid,
            minTransactionTimestamp,
            maxTransactionTimestamp
        )

        // Assert
        verify(exactly = 1) {
            accountsApiClient.getAccounts()
        }
        confirmVerified(accountsApiClient)

        val expectedResponse = RoundupResponse("An error occurred: An error occurred", 0.0)
        assert(response.statusCode == HttpStatus.INTERNAL_SERVER_ERROR)
        assert(response.body == expectedResponse)
    }
}
