package com.example.starkfuturetest.domain.logic

import com.example.starkfuturetest.domain.model.BatteryStatus
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class BatteryStatusResolverTest {

    private lateinit var resolver: BatteryStatusResolverImpl

    @Before
    fun setUp() {
        resolver = BatteryStatusResolverImpl()
    }

    @Test
    fun `0 percent returns Critical`() = assertEquals(BatteryStatus.Critical, resolver.resolve(0))

    @Test
    fun `15 percent returns Critical`() = assertEquals(BatteryStatus.Critical, resolver.resolve(15))

    @Test
    fun `16 percent returns Medium`() = assertEquals(BatteryStatus.Medium, resolver.resolve(16))

    @Test
    fun `49 percent returns Medium`() = assertEquals(BatteryStatus.Medium, resolver.resolve(49))

    @Test
    fun `50 percent returns Healthy`() = assertEquals(BatteryStatus.Healthy, resolver.resolve(50))

    @Test
    fun `73 percent returns Healthy`() = assertEquals(BatteryStatus.Healthy, resolver.resolve(73))

    @Test
    fun `100 percent returns Healthy`() = assertEquals(BatteryStatus.Healthy, resolver.resolve(100))

    @Test
    fun `negative value returns Unknown`() = assertEquals(BatteryStatus.Unknown, resolver.resolve(-1))

    @Test
    fun `value above 100 returns Unknown`() = assertEquals(BatteryStatus.Unknown, resolver.resolve(101))
}