package dev.detekt.tooling.api.spec

import java.nio.file.Path

interface ExtensionsSpec {

    /**
     * Where to look for [dev.detekt.api.Extension]s?
     *
     * Defaults to looking at the running classpath.
     */
    val plugins: Plugins?

    /**
     * Identifiers for [dev.detekt.api.Extension] which should not be loaded during the analysis.
     */
    val disabledExtensions: Set<ExtensionId>

    interface Plugins {

        /**
         * Paths to jar files which declared extensions.
         */
        val paths: Collection<Path>?

        /**
         * Classloader to look for extensions.
         */
        val loader: ClassLoader?
    }
}

typealias ExtensionId = String
