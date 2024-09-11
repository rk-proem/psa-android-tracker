package com.psaanalytics.psatracker

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.psaanalytics.psa_android_tracker.psa.event.ScreenView

class Activity2 : AppCompatActivity() {
    lateinit var next2: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_2)

        next2 = findViewById(R.id.next2Btn)



        // Log before tracking screen view
        Log.d("TrackerLog", "Attempting to track Screen Two view...")

        // Track screen view event
        TrackerManager.getTracker()?.track(ScreenView("Screen Two PSA"))?.let {
            // Log success if tracker is not null and event is tracked
            Log.d("TrackerLog", "Screen Two view tracked successfully.")
        } ?: run {
            // Log failure if tracker is null or tracking failed
            Log.e("TrackerLog", "Failed to track Screen Two view.")
        }




        // Track screen view event
//        TrackerManager.getTracker()?.track(ScreenView("Screen Two"))




        next2.setOnClickListener {
            val intent = Intent(this, Activity3::class.java)
            startActivity(intent)

        }
    }
}