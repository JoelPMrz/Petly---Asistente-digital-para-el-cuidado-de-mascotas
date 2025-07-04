package com.jdev.petly.di

import android.content.Context
import android.content.SharedPreferences
import com.jdev.petly.utils.SharedPreferencesManager
import com.jdev.petly.data.repository.PreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideSharedPreferencesManager(sharedPreferences: SharedPreferences): SharedPreferencesManager {
        return SharedPreferencesManager(sharedPreferences)
    }

    @Provides
    @Singleton
    fun providePreferencesRepository(sharedPreferencesManager: SharedPreferencesManager): PreferencesRepository {
        return PreferencesRepository(sharedPreferencesManager)
    }
}
