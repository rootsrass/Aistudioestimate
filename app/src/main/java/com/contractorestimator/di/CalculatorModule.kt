package com.contractorestimator.di

import com.contractorestimator.domain.calculator.ServiceCalculator
import com.contractorestimator.domain.calculator.SodCalculator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CalculatorModule {

    @Provides
    @Singleton
    fun provideSodCalculator(): SodCalculator {
        return SodCalculator()
    }

    @Provides
    @Singleton
    fun provideServiceRegistry(sodCalculator: SodCalculator): ServiceRegistry {
        return ServiceRegistry(sodCalculator)
    }
}
