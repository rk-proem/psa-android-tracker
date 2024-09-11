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

package com.psaanalytics.psa_android_tracker.psa.entity

import com.psaanalytics.psa_android_tracker.core.constants.Parameters
import com.psaanalytics.psa_android_tracker.core.constants.TrackerConstants
import com.psaanalytics.psa_android_tracker.psa.payload.SelfDescribingJson

/**
 * Used to represent session information.
 */
class ClientSessionEntity(private val values: Map<String, Any?>) :
    SelfDescribingJson(TrackerConstants.SESSION_SCHEMA, values) {

    val userId: String?
        get() = values[Parameters.SESSION_USER_ID] as String?
}
