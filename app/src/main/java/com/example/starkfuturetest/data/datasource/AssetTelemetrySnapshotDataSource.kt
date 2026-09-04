package com.example.starkfuturetest.data.datasource

import android.content.Context
import com.example.starkfuturetest.core.dispatchers.DispatcherProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AssetTelemetrySnapshotDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val dispatcherProvider: DispatcherProvider,
) : TelemetrySnapshotDataSource {

    override suspend fun getTelemetrySnapshotJson(fileName: String): String =
        withContext(dispatcherProvider.io) {
            context.assets.open(fileName).bufferedReader().use { it.readText() }
        }
}