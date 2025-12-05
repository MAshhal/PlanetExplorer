package com.mystic.planetexplorer.di

import com.mystic.planetexplorer.BuildConfig
import com.mystic.planetexplorer.data.api.PlanetService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import javax.inject.Singleton

/**
 * Created: Fri 05 Dec 2025
 * Author: Muhammad Ashhal
 */

/**
 * Dagger Hilt module providing network dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Provides JSON parser with lenient settings to handle API inconsistencies.
     * - ignoreUnknownKeys: Prevents crashes when API returns extra fields
     * - isLenient: Allows malformed JSON to be parsed when possible
     */
    @Provides
    @Singleton
    fun provideJsonParser(): Json {
        return Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        json: Json
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.SWAPI_BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun providePlanetService(retrofit: Retrofit): PlanetService {
        return retrofit.create<PlanetService>()
    }

}