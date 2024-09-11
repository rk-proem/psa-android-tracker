package com.psaanalytics.psa_android_tracker.core.emitter

import com.psaanalytics.psa_android_tracker.psa.emitter.BufferOption
import com.psaanalytics.psa_android_tracker.psa.network.HttpMethod
import com.psaanalytics.psa_android_tracker.psa.network.Protocol
import java.util.EnumSet
import java.util.concurrent.TimeUnit
import kotlin.time.DurationUnit
import kotlin.time.toDuration

object EmitterDefaults {
    var httpMethod = HttpMethod.POST
    var bufferOption = BufferOption.Single
    var httpProtocol = Protocol.HTTPS
    var tlsVersions: EnumSet<TLSVersion> = EnumSet.of(TLSVersion.TLSv1_2)
    var emitRange: Int = BufferOption.LargeGroup.code
    var emitterTick = 5
    var emptyLimit = 5
    var byteLimitGet: Long = 40000
    var byteLimitPost: Long = 40000
    var emitTimeout = 30
    var threadPoolSize = 15
    var serverAnonymisation = false
    var retryFailedRequests = true
    var timeUnit = TimeUnit.SECONDS
    var maxEventStoreAge = 30.toDuration(DurationUnit.DAYS)
    var maxEventStoreSize: Long = 1000
}
