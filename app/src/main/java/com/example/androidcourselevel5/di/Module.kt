package com.example.androidcourselevel5.di

import android.content.Context
import com.example.androidcourselevel5.data.repository.SharedPrefStorage
import com.example.androidcourselevel5.domain.repository.DataStorage
import com.example.androidcourselevel5.presentation.ui.ActivityHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class Module {

    @Provides
    @Singleton
    fun provideActivityHelper(@ApplicationContext context: Context): ActivityHelper {
        return ActivityHelper(context)
    }

    @Provides
    @Singleton
    fun provideSharedStorage(@ApplicationContext context: Context): DataStorage {
        return SharedPrefStorage(context)
    }
}