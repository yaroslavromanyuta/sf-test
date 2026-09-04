package com.example.starkfuturetest.ui.dashboard

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.starkfuturetest.ui.theme.StarkTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TelemetryErrorStateTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val errorMessage = "Could not read telemetry data"

    @Test
    fun errorTitleIsDisplayed() {
        composeTestRule.setContent {
            StarkTheme {
                TelemetryErrorState(message = errorMessage, onRetry = {})
            }
        }
        composeTestRule.onNodeWithText("Failed to load telemetry").assertIsDisplayed()
    }

    @Test
    fun errorMessageIsDisplayed() {
        composeTestRule.setContent {
            StarkTheme {
                TelemetryErrorState(message = errorMessage, onRetry = {})
            }
        }
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }

    @Test
    fun retryButtonIsDisplayed() {
        composeTestRule.setContent {
            StarkTheme {
                TelemetryErrorState(message = errorMessage, onRetry = {})
            }
        }
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun retryButtonInvokesCallback() {
        var retried = false
        composeTestRule.setContent {
            StarkTheme {
                TelemetryErrorState(message = errorMessage, onRetry = { retried = true })
            }
        }
        composeTestRule.onNodeWithText("Retry").performClick()
        assertTrue(retried)
    }
}