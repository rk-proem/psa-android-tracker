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
package com.psaanalytics.psa_android_tracker.core.tracker


import com.psaanalytics.psa_android_tracker.core.Controller
import com.psaanalytics.psa_android_tracker.psa.controller.PluginsController
import com.psaanalytics.psa_android_tracker.core.tracker.ServiceProvider
import com.psaanalytics.psa_android_tracker.psa.configuration.PluginIdentifiable

class PluginsControllerImpl
    (serviceProvider: ServiceProvider) : Controller(serviceProvider), PluginsController {

    override val identifiers: List<String>
        get() {
            return serviceProvider.pluginConfigurations.map { it.identifier }
        }

    override fun addPlugin(plugin: PluginIdentifiable) {
        serviceProvider.addPlugin(plugin)
    }

    override fun removePlugin(identifier: String) {
        serviceProvider.removePlugin(identifier)
    }
}
