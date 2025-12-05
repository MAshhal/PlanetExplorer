package com.mystic.planetexplorer.di

import com.mystic.planetexplorer.data.repository.PlanetRepositoryImpl
import com.mystic.planetexplorer.domain.repository.PlanetRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created: Fri 05 Dec 2025
 * Author: Muhammad Ashhal
 */

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindPlanetRepository(
        impl: PlanetRepositoryImpl
    ): PlanetRepository
}