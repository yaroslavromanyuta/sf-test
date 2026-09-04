package com.example.starkfuturetest.di

import com.example.starkfuturetest.domain.logic.AverageSpeedCalculator
import com.example.starkfuturetest.domain.logic.AverageSpeedCalculatorImpl
import com.example.starkfuturetest.domain.logic.BatteryStatusResolver
import com.example.starkfuturetest.domain.logic.BatteryStatusResolverImpl
import com.example.starkfuturetest.domain.usecase.GetTelemetrySnapshotUseCase
import com.example.starkfuturetest.domain.usecase.GetTelemetrySnapshotUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DomainModule {

    @Binds @Singleton
    abstract fun bindGetTelemetrySnapshotUseCase(impl: GetTelemetrySnapshotUseCaseImpl): GetTelemetrySnapshotUseCase

    @Binds @Singleton
    abstract fun bindBatteryStatusResolver(impl: BatteryStatusResolverImpl): BatteryStatusResolver

    @Binds @Singleton
    abstract fun bindAverageSpeedCalculator(impl: AverageSpeedCalculatorImpl): AverageSpeedCalculator
}