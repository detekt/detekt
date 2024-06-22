package io.github.detekt.tooling.api.spec

import java.nio.file.Path

interface ExtensionsSpec {

    /**
     * Where to look for [io.gitlab.arturbosch.detekt.api.Extension]s?
     *
     * Defaults to looking at the running classpath.
     */
    val plugins: Plugins?

    /**
     * Identifiers for [io.gitlab.arturbosch.detekt.api.Extension] which should not be loaded during the analysis.
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
