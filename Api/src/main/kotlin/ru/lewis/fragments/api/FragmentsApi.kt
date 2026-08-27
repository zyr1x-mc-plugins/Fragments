package ru.lewis.fragments.api

/**
 * Main entry point of the Fragments public API.
 *
 * This API is intended for integrations written by third-party plugins. It provides a
 * stable, versioned contract over the internal Fragments implementation, so that
 * integrations do not depend on internal classes which may change at any time.
 *
 * To obtain the API instance, use the static entry point:
 *
 * ```
 * val api = FragmentsApi.get()
 * if (api != null) {
 *     // work with the API
 * }
 * ```
 *
 * `FragmentsApi.get()` internally resolves the instance through the Bukkit
 * [org.bukkit.plugin.ServicesManager], so it is equivalent to
 * `Bukkit.getServicesManager().load(FragmentsApi::class.java)`.
 *
 * The API is registered once the plugin has fully initialised and is unregistered on
 * plugin shutdown. If the Fragments plugin is not installed or is disabled, [FragmentsApi.get]
 * returns `null`.
 *
 * All methods of this API and of the sub-APIs it exposes must be invoked on the main
 * server thread unless explicitly documented otherwise.
 */
interface FragmentsApi {

    /**
     * Provides access to the Fragments economy (the players' fragment balance).
     *
     * Must be called on the main thread only.
     */
    val economy: FragmentsEconomy

    /**
     * Provides access to the in-world Fragments event controller.
     *
     * Must be called on the main thread only.
     */
    val event: FragmentEventController

    /**
     * Provides access to the purchase state of the event shop.
     *
     * Must be called on the main thread only.
     */
    val purchases: FragmentPurchaseController

    companion object {

        /**
         * Returns the currently registered [FragmentsApi] instance, or `null` if the
         * Fragments plugin is not installed, not enabled, or has not finished
         * initialising.
         *
         * This is a convenient static entry point that internally resolves the instance
         * through the Bukkit [org.bukkit.plugin.ServicesManager], so it is equivalent to:
         *
         * ```
         * Bukkit.getServicesManager().load(FragmentsApi::class.java)
         * ```
         *
         * Can be called from any thread; the returned API must still be used on the main
         * thread only.
         *
         * @return the current API instance, or `null` if the plugin is unavailable
         */
        @JvmStatic
        fun get(): FragmentsApi? =
            org.bukkit.Bukkit.getServicesManager().load(FragmentsApi::class.java)
    }
}
