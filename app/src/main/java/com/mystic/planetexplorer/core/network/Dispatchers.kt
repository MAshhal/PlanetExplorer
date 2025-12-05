package com.mystic.planetexplorer.core.network

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

/**
 * Created using Android Studio
 * User: ashal
 * Date: 4/22/2024
 * Time: 9:04 AM
 */

/**
 * Dagger qualifier annotation for injecting specific CoroutineDispatchers.
 */
@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val dispatcher: DispatcherType)

enum class DispatcherType {
    DEFAULT, // For CPU-intensive work
    IO       // For I/O operations (network, database)
}