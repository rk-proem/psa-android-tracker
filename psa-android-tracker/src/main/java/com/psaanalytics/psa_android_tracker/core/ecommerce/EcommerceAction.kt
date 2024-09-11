package com.psaanalytics.psa_android_tracker.core.ecommerce

/**
 * Available types of ecommerce action. Each one is a different event type.
 */
enum class EcommerceAction {
    // lowercase to match the schema
    add_to_cart,
    remove_from_cart,
    product_view,
    list_click,
    list_view,
    promo_click,
    promo_view,
    checkout_step,
    transaction,
    trns_error,
    refund
}
