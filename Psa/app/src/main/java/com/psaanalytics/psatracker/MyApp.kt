package com.psaanalytics.psatracker

import android.app.Application
import android.util.Log
import com.psaanalytics.psa_android_tracker.psa.Psa
import com.psaanalytics.psa_android_tracker.psa.configuration.NetworkConfiguration
import com.psaanalytics.psa_android_tracker.psa.configuration.SessionConfiguration
import com.psaanalytics.psa_android_tracker.psa.configuration.TrackerConfiguration
import com.psaanalytics.psa_android_tracker.psa.network.HttpMethod
import com.psaanalytics.psa_android_tracker.psa.util.TimeMeasure
import java.util.concurrent.TimeUnit

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Log initialization start
        Log.d("TrackerLog", "Initializing Snowplow tracker...")


        val networkConfig = NetworkConfiguration(
            "https://internal.proemsportsanalytics.com",
            HttpMethod.POST
        )
        // Configure tracker
        val trackerConfig = TrackerConfiguration("rt-android")
            .base64encoding(false)
            .sessionContext(true)
            .platformContext(true)
            .lifecycleAutotracking(false)
            .screenViewAutotracking(true)
            .screenContext(true)
            .applicationContext(true)
            .exceptionAutotracking(true)
            .installAutotracking(true)
            .userAnonymisation(false)

        // Configure session
        val sessionConfig = SessionConfiguration(
            TimeMeasure(30, TimeUnit.SECONDS),
            TimeMeasure(30, TimeUnit.SECONDS)
        )

        try {
            // Create and configure the tracker
            val tracker = Psa.createTracker(
                applicationContext,
                "kotlin",
                networkConfig,
                trackerConfig,
                sessionConfig
            )

            // Set the tracker instance to be accessible globally
            TrackerManager.setTracker(tracker)

            // Log tracker creation success
            Log.d("TrackerLog", "Snowplow tracker initialized successfully.")
        } catch (e: Exception) {
            // Log the exception with more details
            Log.e("TrackerLog", "Error initializing Snowplow tracker: ${e.message}", e)
        }




    }
}