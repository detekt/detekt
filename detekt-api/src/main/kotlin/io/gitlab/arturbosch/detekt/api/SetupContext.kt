package io.gitlab.arturbosch.detekt.api

import java.net.URI
import java.nio.file.Path

/**
 * Context providing useful processing settings to initialize extensions.
 */
interface SetupContext : PropertiesAware {
    /**
     * All config locations which where used to create [config].
     */
    val configUris: Collection<URI>

    /**
     * Configuration which is used to set up detekt.
     */
    val config: Config

    val basePath: Path

    /**
     * The channel to log all the output.
     */
    val outputChannel: Appendable

    /**
     * The channel to log all the errors.
     */
    val errorChannel: Appendable
}
