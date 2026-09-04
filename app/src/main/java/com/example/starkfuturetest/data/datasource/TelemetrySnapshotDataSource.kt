package com.example.starkfuturetest.data.datasource

interface TelemetrySnapshotDataSource {
    suspend fun getTelemetrySnapshotJson(fileName: String): String
}