package com.psaanalytics.psa_android_tracker.psa.network

/**
 * Protocol is used to set the protocol used for sending Requests.
 * Either HTTP or HTTPS.
 */
enum class Protocol {
    /**
     * Events are sent without security.
     */
    HTTP,

    /**
     * Events are sent with added security.
     */
    HTTPS
}
