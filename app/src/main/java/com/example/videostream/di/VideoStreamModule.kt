package com.example.videostream.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class VideoStreamModule {

    @Provides
    @Singleton
    fun providesSharedPreferences(application: Application): SharedPreferences{
        return application.getSharedPreferences("VideoStreamPreferences", Context.MODE_PRIVATE)
    }
}