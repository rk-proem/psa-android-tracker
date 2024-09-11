package com.psaanalytics.psatracker

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.psaanalytics.psa_android_tracker.psa.event.ScreenView

class Activity3 : AppCompatActivity() {
    lateinit var backBtn: Button



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_3)

        backBtn = findViewById(R.id.backBtn)




        // Track screen view event
        TrackerManager.getTracker()?.track(ScreenView("Screen Three"))

        backBtn.setOnClickListener {
//            val intent = Intent(this, Activity1::class.java)
//            startActivity(intent)
            finish()
        }


    }
}