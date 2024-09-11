package com.psaanalytics.psatracker

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.psaanalytics.psa_android_tracker.psa.event.ScreenView

class Activity1 : AppCompatActivity() {
    lateinit var nextBtn: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_1)
        // Log before tracking screen view
        Log.d("TrackerLog", "Attempting to track Screen One view...")

        // Track screen view event
//        TrackerManager.getTracker()?.track(ScreenView("Screen One"))

        // Track screen view event
        TrackerManager.getTracker()?.track(ScreenView("Screen One PSA"))?.let {
            // Log success if tracker is not null and event is tracked
            Log.d("TrackerLog", "Screen One view tracked successfully.")
        } ?: run {
            // Log failure if tracker is null or tracking failed
            Log.e("TrackerLog", "Failed to track Screen One view.")
        }


        nextBtn = findViewById(R.id.nextBtn)
        nextBtn.setOnClickListener {
            val intent = Intent(this, Activity2::class.java)
            Log.d("TrackerLog", "Next button clicked.")

            startActivity(intent)
        }

    }

}