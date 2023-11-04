package com.example.videostream.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.videostream.perferences.IVideoStreamPreferences
import com.example.videostream.perferences.VideoStreamPreferences
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
    fun providesSharedPreferences(application: Application): SharedPreferences {
        return application.getSharedPreferences("VideoStreamPreferences", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun providesIVideoStreamPreferences(sharedPreferences: SharedPreferences): IVideoStreamPreferences {
        return VideoStreamPreferences(sharedPreferences)
    }

}