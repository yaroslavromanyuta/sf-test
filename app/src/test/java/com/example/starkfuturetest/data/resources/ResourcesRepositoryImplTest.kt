package com.example.starkfuturetest.data.resources

import android.content.Context
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test

class ResourcesRepositoryImplTest {

    private val context: Context = mockk()
    private val repository = ResourcesRepositoryImpl(context)

    @Test
    fun `getString delegates to context`() {
        every { context.getString(42) } returns "Asset read failed"

        assertEquals("Asset read failed", repository.getString(42))
        verify(exactly = 1) { context.getString(42) }
    }

    @Test
    fun `getString with arguments forwards every argument in order`() {
        every { context.getString(7, "Battery", 73) } returns "Battery at 73%"

        assertEquals("Battery at 73%", repository.getString(7, "Battery", 73))
        verify(exactly = 1) { context.getString(7, "Battery", 73) }
    }
}
