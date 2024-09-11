package com.psaanalytics.psa_android_tracker.psa.network

/**
 * HttpMethod is used to set the request method for your Emitter (i.e. GET or POST requests).
 */
enum class HttpMethod {
    /**
     * Each event is sent individually in separate GET requests.
     */
    GET,

    /**
     * Events can be grouped together and sent in one request if desired.
     */
    POST
}
