package com.example.starkfuturetest.ui.dashboard

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.starkfuturetest.ui.theme.StarkTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TelemetryEmptyStateTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun emptyTitleIsDisplayed() {
        composeTestRule.setContent {
            StarkTheme { TelemetryEmptyState() }
        }
        composeTestRule.onNodeWithText("No telemetry data").assertIsDisplayed()
    }

    @Test
    fun emptyBodyIsDisplayed() {
        composeTestRule.setContent {
            StarkTheme { TelemetryEmptyState() }
        }
        composeTestRule.onNodeWithText("The snapshot contains no readable telemetry.").assertIsDisplayed()
    }
}