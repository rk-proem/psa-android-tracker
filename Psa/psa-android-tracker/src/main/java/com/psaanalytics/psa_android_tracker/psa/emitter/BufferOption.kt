package com.psaanalytics.psa_android_tracker.psa.emitter

/**
 * BufferOption is used to set how many events will be in one request to the collector.
 */
enum class BufferOption(val code: Int) {
    /**
     * Sends both GET and POST requests with only a single event.
     * This is the default setting.
     * Can cause a spike in network traffic if used in correlation with a large amount of events.
     */
    Single(1),

    /**
     * Sends POST requests in groups of 10 events.
     * All GET requests will still emit one at a time.
     */
    SmallGroup(10),

    /**
     * Sends POST requests in groups of 25 events.
     * Useful for situations where many events need to be sent.
     * All GET requests will still emit one at a time.
     */
    LargeGroup(25);

    companion object {
        fun fromString(string: String): BufferOption? {
            return when (string) {
                "Single" -> Single
                "SmallGroup" -> SmallGroup
                "DefaultGroup" -> SmallGroup
                "LargeGroup" -> LargeGroup
                "HeavyGroup" -> LargeGroup
                else -> null
            }
        }
    }
}