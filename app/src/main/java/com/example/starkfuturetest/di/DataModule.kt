package com.example.starkfuturetest.di

import com.example.starkfuturetest.data.datasource.AssetTelemetrySnapshotDataSource
import com.example.starkfuturetest.data.datasource.TelemetrySnapshotDataSource
import com.example.starkfuturetest.data.mapper.TelemetryDtoToDomainMapper
import com.example.starkfuturetest.data.mapper.TelemetryDtoToDomainMapperImpl
import com.example.starkfuturetest.data.parser.KotlinxTelemetryJsonParser
import com.example.starkfuturetest.data.parser.TelemetryJsonParser
import com.example.starkfuturetest.data.repository.TelemetryRepositoryImpl
import com.example.starkfuturetest.data.repository.TelemetryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds @Singleton
    abstract fun bindTelemetryRepository(impl: TelemetryRepositoryImpl): TelemetryRepository

    @Binds @Singleton
    abstract fun bindTelemetrySnapshotDataSource(impl: AssetTelemetrySnapshotDataSource): TelemetrySnapshotDataSource

    @Binds @Singleton
    abstract fun bindTelemetryJsonParser(impl: KotlinxTelemetryJsonParser): TelemetryJsonParser

    @Binds @Singleton
    abstract fun bindTelemetryDtoToDomainMapper(impl: TelemetryDtoToDomainMapperImpl): TelemetryDtoToDomainMapper
}