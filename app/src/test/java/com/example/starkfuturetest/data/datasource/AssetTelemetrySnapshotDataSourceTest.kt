package com.example.starkfuturetest.data.datasource

import android.content.Context
import android.content.res.AssetManager
import com.example.starkfuturetest.core.dispatchers.DispatcherProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.IOException
import kotlin.coroutines.CoroutineContext

@OptIn(ExperimentalCoroutinesApi::class)
class AssetTelemetrySnapshotDataSourceTest {

    private val testDispatcher = StandardTestDispatcher()
    private val assets: AssetManager = mockk()
    private val context: Context = mockk()
    private val dispatchers = object : DispatcherProvider {
        override val main: CoroutineDispatcher = testDispatcher
        override val io: CoroutineDispatcher = testDispatcher
        override val default: CoroutineDispatcher = testDispatcher
    }
    private val dataSource = AssetTelemetrySnapshotDataSource(context, dispatchers)

    @Before
    fun setUp() {
        every { context.assets } returns assets
    }

    @Test
    fun `reads the requested asset as text`() = runTest(testDispatcher) {
        val json = """{"bike":{"model":"Stark VARG MX 1.2"}}"""
        every { assets.open("telemetry_snapshot.json") } returns ByteArrayInputStream(json.toByteArray())

        assertEquals(json, dataSource.getTelemetrySnapshotJson("telemetry_snapshot.json"))
        verify(exactly = 1) { assets.open("telemetry_snapshot.json") }
    }

    @Test
    fun `preserves multi-line content exactly`() = runTest(testDispatcher) {
        val json = "{\n  \"timestamp\": \"2025-05-19T10:32:00Z\"\n}"
        every { assets.open("snapshot_2.json") } returns ByteArrayInputStream(json.toByteArray())

        assertEquals(json, dataSource.getTelemetrySnapshotJson("snapshot_2.json"))
    }

    @Test(expected = IOException::class)
    fun `propagates IOException for a missing asset`() = runTest(testDispatcher) {
        every { assets.open("missing.json") } throws IOException("missing.json")

        dataSource.getTelemetrySnapshotJson("missing.json")
    }

    @Test
    fun `reads the asset on the injected io dispatcher`() = runTest(testDispatcher) {
        val counting = CountingDispatcher(testDispatcher)
        val source = AssetTelemetrySnapshotDataSource(
            context,
            object : DispatcherProvider {
                override val main: CoroutineDispatcher = testDispatcher
                override val io: CoroutineDispatcher = counting
                override val default: CoroutineDispatcher = testDispatcher
            },
        )
        every { assets.open(any()) } returns ByteArrayInputStream("{}".toByteArray())

        source.getTelemetrySnapshotJson("telemetry_snapshot.json")

        assertEquals(true, counting.dispatches > 0)
    }

    private class CountingDispatcher(private val delegate: CoroutineDispatcher) : CoroutineDispatcher() {
        var dispatches = 0
            private set

        override fun dispatch(context: CoroutineContext, block: Runnable) {
            dispatches++
            delegate.dispatch(context, block)
        }
    }
}
