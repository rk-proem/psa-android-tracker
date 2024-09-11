package com.psaanalytics.psa_android_tracker.core.tracker

import com.psaanalytics.psa_android_tracker.psa.controller.GlobalContextsController
import com.psaanalytics.psa_android_tracker.core.emitter.NetworkControllerImpl
import com.psaanalytics.psa_android_tracker.psa.media.controller.MediaController
import com.psaanalytics.psa_android_tracker.core.ecommerce.EcommerceControllerImpl
import com.psaanalytics.psa_android_tracker.core.emitter.Emitter
import com.psaanalytics.psa_android_tracker.core.emitter.EmitterControllerImpl
import com.psaanalytics.psa_android_tracker.core.gdpr.GdprControllerImpl
import com.psaanalytics.psa_android_tracker.core.session.SessionControllerImpl
import com.psaanalytics.psa_android_tracker.psa.configuration.EmitterConfiguration
import com.psaanalytics.psa_android_tracker.psa.configuration.GdprConfiguration
import com.psaanalytics.psa_android_tracker.psa.configuration.NetworkConfiguration
import com.psaanalytics.psa_android_tracker.psa.configuration.PluginIdentifiable
import com.psaanalytics.psa_android_tracker.psa.configuration.SessionConfiguration
import com.psaanalytics.psa_android_tracker.psa.configuration.SubjectConfiguration
import com.psaanalytics.psa_android_tracker.psa.configuration.TrackerConfiguration

interface ServiceProviderInterface {
    val namespace: String

    // Internal services
    val isTrackerInitialized: Boolean
    fun getOrMakeTracker(): Tracker
    fun getOrMakeEmitter(): Emitter
    fun getOrMakeSubject(): Subject

    // Controllers
    fun getOrMakeTrackerController(): TrackerControllerImpl
    fun getOrMakeEmitterController(): EmitterControllerImpl
    fun getOrMakeNetworkController(): NetworkControllerImpl
    fun getOrMakeGdprController(): GdprControllerImpl
    fun getOrMakeGlobalContextsController(): GlobalContextsController
    fun getOrMakeSubjectController(): SubjectControllerImpl
    fun getOrMakeSessionController(): SessionControllerImpl
    val pluginsController: PluginsControllerImpl
    val mediaController: MediaController
    val ecommerceController: EcommerceControllerImpl

    // Configuration Updates
    val trackerConfiguration: TrackerConfiguration
    val networkConfiguration: NetworkConfiguration
    val subjectConfiguration: SubjectConfiguration
    val emitterConfiguration: EmitterConfiguration
    val sessionConfiguration: SessionConfiguration
    val gdprConfiguration: GdprConfiguration

    // Plugins
    val pluginConfigurations: List<PluginIdentifiable>
    fun addPlugin(plugin: PluginIdentifiable)
    fun removePlugin(identifier: String)
}
