package dev.detekt.tooling.api.spec

import java.net.URL
import java.nio.file.Path

interface ConfigSpec {

    /**
     * If the configuration properties should be validated.
     *
     * Unknown properties to detekt will get reported as errors.
     */
    val shouldValidateBeforeAnalysis: Boolean?

    /**
     * Rely on detekt to configure meaningful defaults.
     *
     * Additional configuration overwrite single properties of the default file.
     */
    val useDefaultConfig: Boolean

    /**
     * Configuration resources on detekt's classpath.
     */
    val resources: Collection<URL>

    /**
     * Paths to detekt yaml configuration files.
     */
    val configPaths: Collection<Path>
}
