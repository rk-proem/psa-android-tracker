package com.psaanalytics.psa_android_tracker.core.statemachine

import com.psaanalytics.psa_android_tracker.psa.tracker.InspectableEvent


/**
 * The inspectable properties of the event used to generate context entities.
 */
interface StateMachineEvent : InspectableEvent {
    /**
     * The tracker state at the time the event was sent.
     */
    val state: TrackerStateSnapshot

    /**
     * Add payload values to the event.
     * @param payload Map of values to add to the event payload
     * @return Whether or not the values have been successfully added to the event payload
     */
    fun addPayloadValues(payload: Map<String, Any>): Boolean
}
