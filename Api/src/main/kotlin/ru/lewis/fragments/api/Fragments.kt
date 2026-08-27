package ru.lewis.fragments.api

/**
 * Global constants exposed by the Fragments API.
 *
 * Integrations can use these constants to report the Fragments version or to derive the
 * artifact coordinate of the API.
 */
object Fragments {

    /**
     * The name of the Fragments plugin.
     */
    const val PLUGIN_NAME: String = "Fragments"

    /**
     * The version of the public API contract.
     *
     * This follows [Semantic Versioning](https://semver.org). Breaking changes to the
     * public API should bump the major version so that integrations can guard against
     * incompatibilities.
     */
    const val API_VERSION: String = "1.0.0"
}
