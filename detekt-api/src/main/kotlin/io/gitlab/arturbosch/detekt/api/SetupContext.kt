package io.gitlab.arturbosch.detekt.api

import java.net.URI

/**
 * Context providing useful processing settings to initialize extensions.
 */
@UnstableApi
interface SetupContext : PropertiesAware {
    /**
     * All config locations which where used to create [config].
     */
    val configUris: Collection<URI>

    /**
     * Configuration which is used to setup detekt.
     */
    val config: Config

    /**
     * The channel to log all the output.
     */
    val outputChannel: Appendable

    /**
     * The channel to log all the errors.
     */
    val errorChannel: Appendable
}
