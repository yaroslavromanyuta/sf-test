package com.example.starkfuturetest.di

import android.content.Context
import com.example.starkfuturetest.core.dispatchers.DefaultDispatcherProvider
import com.example.starkfuturetest.core.dispatchers.DispatcherProvider
import com.example.starkfuturetest.core.resources.ResourcesRepository
import com.example.starkfuturetest.data.resources.ResourcesRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    @Provides
    @Singleton
    fun provideDispatcherProvider(): DispatcherProvider = DefaultDispatcherProvider()

    @Provides
    @Singleton
    fun provideResourcesRepository(
        @ApplicationContext context: Context,
    ): ResourcesRepository = ResourcesRepositoryImpl(context)
}