/*
 * Copyright (c) 2015-present PSA Analytics Ltd. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package com.psaanalytics.psa_android_tracker.psa.network

import java.util.*

/**
 * Stores the result of a Request attempt.
 * @param statusCode HTTP status code from Collector response
 * @param oversize was the request oversize
 * @param eventIds a list of event ids involved in the sending
 */
class RequestResult(
    val statusCode: Int,
    val oversize: Boolean,
    val eventIds: List<Long>
) {
    /**
     * @return the requests success status
     */
    val isSuccessful: Boolean
        get() = statusCode in 200..299

    /**
     * Checks if the request should be retried.
     * Requests will not be retried if:
     * - the request was successful (status code 2xx)
     * - the request is larger than the configured maximum byte size
     * - the status code is in the list of configured no-retry codes
     * - the status code is in the list of default no-retry codes - 400, 401, 403, 410, or 422 
     * 
     * @see com.psaanalytics.psa.configuration.EmitterConfiguration.customRetryForStatusCodes
     */
    fun shouldRetry(customRetryForStatusCodes: Map<Int, Boolean>?, retryAllowed: Boolean): Boolean {
        // don't retry if successful
        if (isSuccessful) {
            return false
        }

        // don't retry if retries are not allowed
        if (!retryAllowed) {
            return false
        }

        // don't retry if request is larger than max byte limit
        if (oversize) {
            return false
        }

        // status code has a custom retry rule
        if (customRetryForStatusCodes?.containsKey(statusCode) == true) {
            return customRetryForStatusCodes[statusCode]!!
        }

        // retry if status code is not in the list of no-retry status codes
        val dontRetryStatusCodes: Set<Int> = HashSet(listOf(400, 401, 403, 410, 422))
        return !dontRetryStatusCodes.contains(statusCode)
    }
}
