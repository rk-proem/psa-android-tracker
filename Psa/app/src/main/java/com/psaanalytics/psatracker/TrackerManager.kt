package com.psaanalytics.psatracker

import com.psaanalytics.psa_android_tracker.psa.controller.TrackerController

object TrackerManager {
    private var tracker: TrackerController? = null

    fun setTracker(tracker: TrackerController) {
        this.tracker = tracker
    }

    fun getTracker(): TrackerController? {
        return tracker
    }




}