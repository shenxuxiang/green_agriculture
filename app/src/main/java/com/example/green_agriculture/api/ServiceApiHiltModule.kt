package com.example.green_agriculture.api

import com.example.green_agriculture.http.HttpRequest
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceApiHiltModule {
    @Provides
    @Singleton
    fun provideHomeServiceApi() = HttpRequest.create<HomeServiceApi>()

    @Provides
    @Singleton
    fun provideMineServiceApi() = HttpRequest.create<MineServiceApi>()
}