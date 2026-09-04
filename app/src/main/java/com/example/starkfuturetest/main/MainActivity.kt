package com.example.starkfuturetest.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.starkfuturetest.presentation.dashboard.TelemetryDashboardViewModel
import com.example.starkfuturetest.ui.dashboard.TelemetryDashboardRoute
import com.example.starkfuturetest.ui.theme.StarkTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: TelemetryDashboardViewModel = hiltViewModel()
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            StarkTheme(themeMode = themeMode) {
                TelemetryDashboardRoute(viewModel = viewModel)
            }
        }
    }
}