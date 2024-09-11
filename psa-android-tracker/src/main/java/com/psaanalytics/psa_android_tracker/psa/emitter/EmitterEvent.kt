package com.psaanalytics.psa_android_tracker.psa.emitter

import com.psaanalytics.psa_android_tracker.psa.payload.Payload

/**
 * A wrapper for event data while it is being processed for sending.
 */
class EmitterEvent(val payload: Payload, val eventId: Long)
