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
package com.psaanalytics.psa_android_tracker.core.globalcontexts

import com.psaanalytics.psa_android_tracker.psa.configuration.PluginEntitiesCallable
import com.psaanalytics.psa_android_tracker.psa.configuration.PluginEntitiesConfiguration
import com.psaanalytics.psa_android_tracker.psa.configuration.PluginIdentifiable
import com.psaanalytics.psa_android_tracker.psa.globalcontexts.GlobalContext


class GlobalContextPluginConfiguration(
    override val identifier: String,
    val globalContext: GlobalContext
) : PluginIdentifiable, PluginEntitiesCallable {

    override val entitiesConfiguration: PluginEntitiesConfiguration
        get() = PluginEntitiesConfiguration(closure = globalContext::generateContexts)
}
