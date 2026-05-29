package com.example.starkfuturetest.di

import com.example.starkfuturetest.presentation.dashboard.formatter.DurationFormatter
import com.example.starkfuturetest.presentation.dashboard.formatter.DurationFormatterImpl
import com.example.starkfuturetest.presentation.dashboard.formatter.TelemetryValueFormatter
import com.example.starkfuturetest.presentation.dashboard.formatter.TelemetryValueFormatterImpl
import com.example.starkfuturetest.presentation.dashboard.mapper.TelemetryUiMapper
import com.example.starkfuturetest.presentation.dashboard.mapper.TelemetryUiMapperImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PresentationModule {

    @Binds @Singleton
    abstract fun bindTelemetryUiMapper(impl: TelemetryUiMapperImpl): TelemetryUiMapper

    @Binds @Singleton
    abstract fun bindDurationFormatter(impl: DurationFormatterImpl): DurationFormatter

    @Binds @Singleton
    abstract fun bindTelemetryValueFormatter(impl: TelemetryValueFormatterImpl): TelemetryValueFormatter
}