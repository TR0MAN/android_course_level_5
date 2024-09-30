package com.example.androidcourselevel5.di

import android.content.Context
import com.example.androidcourselevel5.data.repository.RetrofitContactAPI
import com.example.androidcourselevel5.data.repository.RetrofitUserAPI
import com.example.androidcourselevel5.data.repository.SharedPrefStorage
import com.example.androidcourselevel5.domain.repository.DataStorage
import com.example.androidcourselevel5.domain.repository.ServerContactsActions
import com.example.androidcourselevel5.domain.repository.ServerUsersActions
import com.example.androidcourselevel5.presentation.ui.ActivityHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

private const val RETROFIT_BASE_URL = "http://178.63.9.114:7777/api/"

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

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val interceptor =
            HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        return Retrofit.Builder()
            .baseUrl(RETROFIT_BASE_URL)
            .client(OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideUserAPI(retrofit: Retrofit): ServerUsersActions {
        return retrofit.create(RetrofitUserAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideContactAPI(retrofit: Retrofit): ServerContactsActions {
        return retrofit.create(RetrofitContactAPI::class.java)
    }

}