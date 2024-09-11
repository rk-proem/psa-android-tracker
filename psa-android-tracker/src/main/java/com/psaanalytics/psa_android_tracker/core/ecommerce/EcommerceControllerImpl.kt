package com.psaanalytics.psa_android_tracker.core.ecommerce

import androidx.annotation.RestrictTo
import com.psaanalytics.psa_android_tracker.psa.ecommerce.EcommerceController
import com.psaanalytics.psa_android_tracker.psa.ecommerce.entities.EcommerceScreenEntity
import com.psaanalytics.psa_android_tracker.psa.ecommerce.entities.EcommerceUserEntity
import com.psaanalytics.psa_android_tracker.core.tracker.ServiceProviderInterface
import com.psaanalytics.psa_android_tracker.psa.configuration.PluginConfiguration

@RestrictTo(RestrictTo.Scope.LIBRARY)
class EcommerceControllerImpl(val serviceProvider: ServiceProviderInterface) : EcommerceController {

    override fun setEcommerceScreen(screen: EcommerceScreenEntity) {
        val plugin = PluginConfiguration("ecommercePageTypePluginInternal")
        plugin.entities { listOf(screen.entity) }
        serviceProvider.addPlugin(plugin)
    }

    override fun setEcommerceUser(user: EcommerceUserEntity) {
        val plugin = PluginConfiguration("ecommerceUserPluginInternal")
        plugin.entities { listOf(user.entity) }
        serviceProvider.addPlugin(plugin)
    }

    override fun removeEcommerceScreen() {
        serviceProvider.removePlugin("ecommercePageTypePluginInternal")
    }

    override fun removeEcommerceUser() {
        serviceProvider.removePlugin("ecommerceUserPluginInternal")
    }
}
