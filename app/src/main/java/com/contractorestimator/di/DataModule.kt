package com.contractorestimator.di

import android.content.Context
import androidx.room.Room
import com.contractorestimator.data.local.AppDatabase
import com.contractorestimator.data.local.dao.EstimateDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "contractor_db"
        ).build()
    }

    @Provides
    fun provideEstimateDao(database: AppDatabase): EstimateDao {
        return database.estimateDao()
    }
}
