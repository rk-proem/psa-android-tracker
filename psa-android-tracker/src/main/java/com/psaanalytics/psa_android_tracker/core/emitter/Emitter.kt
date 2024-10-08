package com.psaanalytics.psa_android_tracker.core.emitter

import android.content.Context
import com.psaanalytics.psa_android_tracker.psa.network.OkHttpNetworkConnection.OkHttpNetworkConnectionBuilder
import com.psaanalytics.psa_android_tracker.psa.network.Request
import com.psaanalytics.psa_android_tracker.psa.network.RequestCallback
import com.psaanalytics.psa_android_tracker.core.constants.Parameters
import com.psaanalytics.psa_android_tracker.core.emitter.storage.SQLiteEventStore
import com.psaanalytics.psa_android_tracker.core.tracker.Logger
import com.psaanalytics.psa_android_tracker.core.utils.Util
import com.psaanalytics.psa_android_tracker.psa.emitter.BufferOption
import com.psaanalytics.psa_android_tracker.psa.emitter.EmitterEvent
import com.psaanalytics.psa_android_tracker.psa.emitter.EventStore
import com.psaanalytics.psa_android_tracker.psa.network.HttpMethod
import com.psaanalytics.psa_android_tracker.psa.network.NetworkConnection
import com.psaanalytics.psa_android_tracker.psa.network.Protocol
import com.psaanalytics.psa_android_tracker.psa.payload.Payload
import okhttp3.CookieJar
import okhttp3.OkHttpClient
import java.util.ArrayList
import java.util.EnumSet
import java.util.HashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.Duration



import com.psaanalytics.psa_android_tracker.psa.network.*


/**
 * Build an emitter object which controls the
 * sending of events to the Psa Collector.
 */
class Emitter(
    val namespace: String,
    eventStore: EventStore?,
    context: Context,
    collectorUri: String,
    builder: ((Emitter) -> Unit)? = null
) {
    private val TAG = Emitter::class.java.simpleName

    private var builderFinished = false
    private val isRunning = AtomicBoolean(false)
    private val isEmittingPaused = AtomicBoolean(false)
    private var isCustomNetworkConnection = false

    private val context: Context
    private lateinit var uri: String
    private var emptyCount = 0

    /**
     * This configuration option is not published in the EmitterConfiguration class.
     * Create an Emitter and Tracker directly, not via the Psa interface, to configure timeUnit.
     */
    var timeUnit: TimeUnit = EmitterDefaults.timeUnit
        set(unit) {
            if (!builderFinished) {
                field = unit
            }
        }

    var cookieJar: CookieJar? = null
        set(cookieJar) {
            if (!builderFinished) {
                field = cookieJar
            }
        }

    var threadPoolSize = EmitterDefaults.threadPoolSize
        set(poolSize) {
            if (!builderFinished) {
                field = poolSize
            }
        }

    var client: OkHttpClient? = null
        set(client) {
            if (!builderFinished) {
                field = client
            }
        }

    /**
     * The emitter event store object
     */
    val eventStore: EventStore = eventStore ?: SQLiteEventStore(context, namespace)

    /**
     * This configuration option is not published in the EmitterConfiguration class.
     * Create an Emitter and Tracker directly, not via the Psa interface, to configure tlsVersions.
     * @return the TLS versions accepted for the emitter
     */
    var tlsVersions: EnumSet<TLSVersion> = EmitterDefaults.tlsVersions

    /**
     * The emitter tick. This configuration option is not published in the EmitterConfiguration class.
     * Create an Emitter and Tracker directly, not via the Psa interface, to configure emitterTick.
     */
    var emitterTick: Int = EmitterDefaults.emitterTick

    /**
     * The amount of times the event store can be empty before it is shut down.
     * This configuration option is not published in the EmitterConfiguration class.
     * Create an Emitter and Tracker directly, not via the Psa interface, to configure emptyLimit.
     */
    var emptyLimit: Int = EmitterDefaults.emptyLimit

    /**
     * The maximum amount of events to grab for an emit attempt.
     */
    var emitRange: Int = EmitterDefaults.emitRange

    /**
     * The GET byte limit
     */
    var byteLimitGet: Long = EmitterDefaults.byteLimitGet

    /**
     * The POST byte limit
     */
    var byteLimitPost: Long = EmitterDefaults.byteLimitPost

    /**
     * @return the request callback method
     */
    var requestCallback: RequestCallback? = null

    /**
     * The emitter status
     */
    val emitterStatus: Boolean
        get() = isRunning.get()

    /**
     * The URI for the Emitter
     */
    var emitterUri: String
        get() = networkConnection?.uri.toString()
        set(uri) {
            this.uri = uri
            if (!isCustomNetworkConnection && builderFinished) {
                networkConnection =
                    emitTimeout?.let {
                        OkHttpNetworkConnection.OkHttpNetworkConnectionBuilder(uri, context)
                            .method(httpMethod)
                            .tls(tlsVersions)
                            .emitTimeout(it)
                            .customPostPath(customPostPath)
                            .client(client)
                            .cookieJar(cookieJar)
                            .serverAnonymisation(serverAnonymisation)
                            .requestHeaders(requestHeaders)
                            .build()
                    }
            }
        }

    /**
     * The Emitters request method
     */
    var httpMethod: HttpMethod = EmitterDefaults.httpMethod
        /**
         * Sets the HttpMethod for the Emitter
         * @param method the HttpMethod
         */
        set(method) {
            field = method
            if (!isCustomNetworkConnection && builderFinished) {
                networkConnection = emitTimeout?.let {
                    OkHttpNetworkConnection.OkHttpNetworkConnectionBuilder(uri, context)
                        .method(httpMethod)
                        .tls(tlsVersions)
                        .emitTimeout(it)
                        .customPostPath(customPostPath)
                        .client(client)
                        .cookieJar(cookieJar)
                        .serverAnonymisation(serverAnonymisation)
                        .requestHeaders(requestHeaders)
                        .build()
                }

            }
        }

    /**
     * The buffer option selected for the emitter
     */
    var bufferOption: BufferOption = EmitterDefaults.bufferOption
        /**
         * Whether the buffer should send events instantly or after the buffer has reached
         * its limit.
         */
        set(option) {
            if (!isRunning.get()) {
                field = option
            }
        }

    /**
     * The request security selected for the emitter
     */
    var requestSecurity: Protocol = EmitterDefaults.httpProtocol
        /**
         * Sets the Protocol for the Emitter
         * @param security the Protocol
         */
        set(security) {
            field = security
            if (!isCustomNetworkConnection && builderFinished) {
                networkConnection = emitTimeout?.let {
                    OkHttpNetworkConnection.OkHttpNetworkConnectionBuilder(uri, context)
                        .method(httpMethod)
                        .tls(tlsVersions)
                        .emitTimeout(it)
                        .customPostPath(customPostPath)
                        .client(client)
                        .cookieJar(cookieJar)
                        .serverAnonymisation(serverAnonymisation)
                        .requestHeaders(requestHeaders)
                        .build()
                }

            }
        }

    /**
     * The maximum timeout for emitting events. If emit time exceeds this value
     * TimeOutException will be thrown.
     *
     * This configuration option, used to create an OkHttpNetworkConnection, is not published
     * in the EmitterConfiguration class. However, it is published in the NetworkConfiguration class.
     * Configure emitTimeout by providing it via networkConfiguration to Psa.createTracker().
     */
    var emitTimeout: Int? = EmitterDefaults.emitTimeout
        set(emitTimeout) {
            emitTimeout?.let {
                field = emitTimeout
                if (!isCustomNetworkConnection && builderFinished) {
                    networkConnection = OkHttpNetworkConnection.OkHttpNetworkConnectionBuilder(
                        uri,
                        context
                    )
                        .method(httpMethod)
                        .tls(tlsVersions)
                        .emitTimeout(emitTimeout)
                        .customPostPath(customPostPath)
                        .client(client)
                        .cookieJar(cookieJar)
                        .serverAnonymisation(serverAnonymisation)
                        .requestHeaders(requestHeaders)
                        .build()
                }
            }
        }

    /**
     * The customPostPath for the Emitter
     */
    var customPostPath: String? = null
        set(customPostPath) {
            field = customPostPath
            if (!isCustomNetworkConnection && builderFinished) {
                networkConnection = emitTimeout?.let {
                    OkHttpNetworkConnection.OkHttpNetworkConnectionBuilder(uri, context)
                        .method(httpMethod)
                        .tls(tlsVersions)
                        .emitTimeout(it)
                        .customPostPath(customPostPath)
                        .client(client)
                        .cookieJar(cookieJar)
                        .serverAnonymisation(serverAnonymisation)
                        .requestHeaders(requestHeaders)
                        .build()
                }

            }
        }

    private val _networkConnection = AtomicReference<NetworkConnection>()
    /**
     * The NetworkConnection if it exists
     */
    var networkConnection: NetworkConnection?
        get() = _networkConnection.get()
        set(value) { _networkConnection.set(value) }

    /**
     * Whether to anonymise server-side user identifiers including the `network_userid` and `user_ipaddress`
     */
    var serverAnonymisation: Boolean = EmitterDefaults.serverAnonymisation
        /**
         * Updates the server anonymisation setting for the Emitter.
         * Ignored if using a custom network connection.
         * @param serverAnonymisation whether to anonymise server-side user identifiers including the `network_userid` and `user_ipaddress`
         */
        set(serverAnonymisation) {
            field = serverAnonymisation
            if (!isCustomNetworkConnection && builderFinished && networkConnection is OkHttpNetworkConnection) {
                (networkConnection as OkHttpNetworkConnection).serverAnonymisation = serverAnonymisation
            }
        }

    private val _customRetryForStatusCodes = AtomicReference<Map<Int, Boolean>>()
    var customRetryForStatusCodes: Map<Int, Boolean>?
        get() = _customRetryForStatusCodes.get()
        set(value) {
            _customRetryForStatusCodes.set(value ?: HashMap())
        }

    private val _retryFailedRequests = AtomicReference(EmitterDefaults.retryFailedRequests)
    /**
     * Whether retrying failed requests is allowed
     */
    var retryFailedRequests: Boolean
        get() = _retryFailedRequests.get()
        set(value) { _retryFailedRequests.set(value) }

    /**
     * The request headers for the emitter
     */
    var requestHeaders: Map<String, String>? = null
        /**
         * Updates the request headers for the emitter.
         * Ignored if using a custom network connection.
         */
        set(requestHeaders) {
            field = requestHeaders
            if (!isCustomNetworkConnection && builderFinished) {
                networkConnection = emitTimeout?.let {
                    OkHttpNetworkConnection.OkHttpNetworkConnectionBuilder(uri, context)
                        .method(httpMethod)
                        .tls(tlsVersions)
                        .emitTimeout(it)
                        .customPostPath(customPostPath)
                        .client(client)
                        .cookieJar(cookieJar)
                        .serverAnonymisation(serverAnonymisation)
                        .requestHeaders(requestHeaders)
                        .build()
                }
            }
        }

    /**
     * Limit for the maximum number of unsent events to keep in the event store.
     */
    var maxEventStoreSize: Long = EmitterDefaults.maxEventStoreSize

    /**
     * Limit for the maximum duration of how long events should be kept in the event store if they fail to be sent.
     */
    var maxEventStoreAge: Duration = EmitterDefaults.maxEventStoreAge

    /**
     * Creates an emitter object
     */
    init {
        this.context = context
        builder?.let { it(this) }

        if (networkConnection == null) {
            isCustomNetworkConnection = false
            var endpoint = collectorUri
            if (!endpoint.startsWith("http")) {
                val protocol =
                    if (requestSecurity === Protocol.HTTPS) "https://" else "http://"
                endpoint = protocol + endpoint
            }
            uri = endpoint
            networkConnection = emitTimeout?.let {
                OkHttpNetworkConnectionBuilder(endpoint, context)
                    .method(httpMethod)
                    .tls(tlsVersions)
                    .emitTimeout(it)
                    .customPostPath(customPostPath)
                    .client(client)
                    .cookieJar(cookieJar)
                    .serverAnonymisation(serverAnonymisation)
                    .requestHeaders(requestHeaders)
                    .build()
            }
        } else {
            isCustomNetworkConnection = true
        }

        if (threadPoolSize > 2) {
            Executor.threadCount = threadPoolSize
        }
        builderFinished = true
        Logger.v(TAG, "Emitter created successfully!")
    }

    // --- Controls

    /**
     * Adds a payload to the EventStore and
     * then attempts to start the emitter
     * if it is not currently running.
     *
     * @param payload the event payload
     * to be added.
     */
    fun add(payload: Payload) {
        Executor.execute(TAG) {
            eventStore.add(payload)
            if (eventStore.size() >= bufferOption.code && isRunning.compareAndSet(false, true)) {
                try {
                    removeOldEvents()
                    attemptEmit(networkConnection)
                } catch (t: Throwable) {
                    isRunning.set(false)
                    Logger.e(TAG, "Received error during emission process: %s", t)
                }
            }
        }
    }

    /**
     * Attempts to start the emitter if it
     * is not currently running.
     */
    fun flush() {
        Executor.execute(TAG) {
            if (isRunning.compareAndSet(false, true)) {
                try {
                    removeOldEvents()
                    attemptEmit(networkConnection)
                } catch (t: Throwable) {
                    isRunning.set(false)
                    Logger.e(TAG, "Received error during emission process: %s", t)
                }
            }
        }
    }

    /**
     * Pause emitting events.
     */
    fun pauseEmit() {
        isEmittingPaused.set(true)
    }

    /**
     * Resume emitting events and attempt to emit any queued events.
     */
    fun resumeEmit() {
        if (isEmittingPaused.compareAndSet(true, false)) {
            flush()
        }
    }

    /**
     * Resets the `isRunning` truth to false and shutdown.
     */
    fun shutdown() {
        shutdown(0)
    }

    /**
     * Resets the `isRunning` truth to false and shutdown.
     *
     * @param timeout the amount of seconds to wait for the termination of the running threads.
     */
    fun shutdown(timeout: Long): Boolean {
        Logger.d(TAG, "Shutting down emitter.")
        isRunning.compareAndSet(true, false)

        val es = Executor.shutdown()
        return if (es == null || timeout <= 0) {
            true
        } else try {
            val isTerminated = es.awaitTermination(timeout, TimeUnit.SECONDS)
            Logger.d(TAG, "Executor is terminated: $isTerminated")
            isTerminated
        } catch (e: InterruptedException) {
            Logger.e(TAG, "Executor termination is interrupted: " + e.message)
            false
        }
    }

    private fun removeOldEvents() {
        eventStore.removeOldEvents(maxEventStoreSize, maxEventStoreAge)
    }

    /**
     * Attempts to send events in the database to a collector.
     *
     * - If the emitter is paused, it will not send
     * - If the emitter is not online it will not send
     * - If the emitter is online but there are no events:
     * + Increment empty counter until emptyLimit reached
     * + Incurs a backoff period between empty counters
     * - If the emitter is online and we have events:
     * + Pulls allowed amount of events from database and
     * attempts to send.
     * + If there are failures resets running state
     * + Otherwise will attempt to emit again
     */
    private fun attemptEmit(networkConnection: NetworkConnection?) {
        if (isEmittingPaused.get()) {
            Logger.d(TAG, "Emitter paused.")
            isRunning.compareAndSet(true, false)
            return
        }

        if (!Util.isOnline(context)) {
            Logger.d(TAG, "Emitter loop stopping: emitter offline.")
            isRunning.compareAndSet(true, false)
            return
        }

        if (networkConnection == null) {
            Logger.d(TAG, "No networkConnection set.")
            isRunning.compareAndSet(true, false)
            return
        }

        if (eventStore.size() <= 0) {
            if (emptyCount >= emptyLimit) {
                Logger.d(TAG, "Emitter loop stopping: empty limit reached.")
                isRunning.compareAndSet(true, false)
                return
            }
            emptyCount++
            Logger.e(TAG, "Emitter database empty: $emptyCount")
            try {
                timeUnit.sleep(emitterTick.toLong())
            } catch (e: InterruptedException) {
                Logger.e(TAG, "Emitter thread sleep interrupted: $e")
            }
            attemptEmit(networkConnection) // at this point we update network connection since it might be outdated after sleep
            return
        }

        emptyCount = 0
        val events = eventStore.getEmittableEvents(emitRange)
        val requests = buildRequests(events, networkConnection.httpMethod)
        val results = networkConnection.sendRequests(requests)

        Logger.v(TAG, "Processing emitter results.")

        var successCount = 0
        var failedWillRetryCount = 0
        var failedWontRetryCount = 0
        val removableEvents: MutableList<Long> = ArrayList()

        for (res in results) {
            if (res.isSuccessful) {
                removableEvents.addAll(res.eventIds)
                successCount += res.eventIds.size
            } else if (res.shouldRetry(customRetryForStatusCodes, retryFailedRequests)) {
                failedWillRetryCount += res.eventIds.size
                Logger.e(TAG, "Request sending failed but we will retry later.")
            } else {
                failedWontRetryCount += res.eventIds.size
                removableEvents.addAll(res.eventIds)
                Logger.e(
                    TAG,
                    String.format(
                        "Sending events to Collector failed with status %d. Events will be dropped.",
                        res.statusCode
                    )
                )
            }
        }
        eventStore.removeEvents(removableEvents)

        val allFailureCount = failedWillRetryCount + failedWontRetryCount
        Logger.d(TAG, "Success Count: %s", successCount)
        Logger.d(TAG, "Failure Count: %s", allFailureCount)

        if (requestCallback != null) {
            if (allFailureCount != 0) {
                requestCallback?.onFailure(successCount, allFailureCount)
            } else {
                requestCallback?.onSuccess(successCount)
            }
        }
        if (failedWillRetryCount > 0 && successCount == 0) {
            if (Util.isOnline(context)) {
                Logger.e(TAG, "Ensure collector path is valid: %s", networkConnection.uri)
            }
            Logger.e(TAG, "Emitter loop stopping: failures.")
            isRunning.compareAndSet(true, false)
        } else {
            attemptEmit(networkConnection) // refresh network connection for next emit
        }
    }

    /**
     * Returns a list of ReadyRequests which can
     * all be sent regardless of if it is GET or POST.
     * - Checks if the event is over-sized.
     * - Stores all of the relevant event ids.
     *
     * @param events a list of EmittableEvents pulled
     * from the database.
     * @param httpMethod HTTP method to use (passed in order to ensure consistency within attemptEmit)
     * @return a list of ready to send requests
     */
    private fun buildRequests(
        events: List<EmitterEvent?>,
        httpMethod: HttpMethod
    ): List<Request> {
        val requests: MutableList<Request> = ArrayList()
        val sendingTime = Util.timestamp()

        if (httpMethod === HttpMethod.GET) {
            for (event in events) {
                val payload = event?.payload
                if (payload != null) {
                    addSendingTimeToPayload(payload, sendingTime)
                    val isOversize = isOversize(payload, httpMethod)
                    val request = Request(payload, event.eventId, isOversize)
                    requests.add(request)
                }
            }
        } else {
            var eventIds: MutableList<Long> = ArrayList()
            var eventPayloads: MutableList<Payload> = ArrayList()

            for (event in events) {
                if (event == null) { continue }
                val payload = event.payload
                val eventId = event.eventId
                addSendingTimeToPayload(payload, sendingTime)

                // Oversize event -> separate requests
                if (isOversize(payload, httpMethod)) {
                    val request = Request(payload, eventId, true)
                    requests.add(request)
                }
                // Events up to this one are oversize -> create request for them
                else if (isOversize(payload, eventPayloads, httpMethod)) {
                    val request = Request(eventPayloads, eventIds)
                    requests.add(request)

                    // Clear collections and build a new POST
                    eventPayloads = ArrayList()
                    eventIds = ArrayList()

                    // Build and store the request
                    eventPayloads.add(payload)
                    eventIds.add(eventId)
                }
                // Add to the list of events for the request
                else {
                    eventPayloads.add(payload)
                    eventIds.add(eventId)
                }
            }

            // Check if there are any remaining events not in a request
            if (eventPayloads.isNotEmpty()) {
                val request = Request(eventPayloads, eventIds)
                requests.add(request)
            }
        }
        return requests
    }

    /**
     * Calculate if the payload exceeds the maximum amount of bytes allowed on configuration.
     * @param payload to send.
     * @param httpMethod HTTP method to use (passed in order to ensure consistency within attemptEmit)
     * @return whether the payload exceeds the maximum size allowed.
     */
    private fun isOversize(payload: Payload, httpMethod: HttpMethod): Boolean {
        return isOversize(payload, ArrayList(), httpMethod)
    }

    /**
     * Calculate if the payload bundle exceeds the maximum amount of bytes allowed on configuration.
     * @param payload to add om the payload bundle.
     * @param previousPayloads already in the payload bundle.
     * @param httpMethod HTTP method to use (passed in order to ensure consistency within attemptEmit)
     * @return whether the payload bundle exceeds the maximum size allowed.
     */
    private fun isOversize(
        payload: Payload,
        previousPayloads: List<Payload>,
        httpMethod: HttpMethod
    ): Boolean {
        val byteLimit = if (httpMethod === HttpMethod.GET) byteLimitGet else byteLimitPost
        return isOversize(payload, byteLimit, previousPayloads)
    }

    /**
     * Calculate if the payload bundle exceeds the maximum amount of bytes allowed on configuration.
     * @param payload to add om the payload bundle.
     * @param byteLimit maximum amount of bytes allowed.
     * @param previousPayloads already in the payload bundle.
     * @return whether the payload bundle exceeds the maximum size allowed.
     */
    private fun isOversize(
        payload: Payload,
        byteLimit: Long,
        previousPayloads: List<Payload>
    ): Boolean {
        var totalByteSize = payload.byteSize
        for (previousPayload in previousPayloads) {
            totalByteSize += previousPayload.byteSize
        }
        val wrapperBytes =
            if (previousPayloads.isNotEmpty()) previousPayloads.size + POST_WRAPPER_BYTES else 0
        return totalByteSize + wrapperBytes > byteLimit
    }

    /**
     * Adds the Sending Time (stm) field
     * to each event payload.
     *
     * @param payload The payload to append the field to
     * @param timestamp An optional timestamp String
     */
    private fun addSendingTimeToPayload(payload: Payload, timestamp: String) {
        payload.add(Parameters.SENT_TIMESTAMP, timestamp)
    }

    companion object {
        private const val POST_WRAPPER_BYTES =
            88 // "schema":"iglu:com.snowplowanalytics.snowplow/payload_data/jsonschema/1-0-3","data":[]
    }
}
