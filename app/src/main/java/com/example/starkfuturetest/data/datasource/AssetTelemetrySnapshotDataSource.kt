package com.example.starkfuturetest.data.datasource

import android.content.Context
import com.example.starkfuturetest.core.dispatchers.DispatcherProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AssetTelemetrySnapshotDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dispatcherProvider: DispatcherProvider,
) : TelemetrySnapshotDataSource {
    override suspend fun getTelemetrySnapshotJson(): String =
        withContext(dispatcherProvider.io) {
            context.assets.open("telemetry_snapshot.json").bufferedReader().use { it.readText() }
        }
}