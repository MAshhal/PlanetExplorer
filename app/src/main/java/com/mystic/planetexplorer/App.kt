package com.mystic.planetexplorer

import android.app.Application
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.HiltAndroidApp

/**
 * Created: Fri 05 Dec 2025
 * Author: Muhammad Ashhal
 */

@HiltAndroidApp
class App: Application() {
    override fun onCreate() {
        super.onCreate()
        // For enabling Material3 Theme in non-compose views
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}