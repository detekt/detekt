package io.github.detekt.tooling.api.spec

import java.nio.file.Path

interface ExtensionsSpec {

    /**
     * Exclude all default rule sets from current analysis.
     */
    val disableDefaultRuleSets: Boolean // TODO move to RulesSpec

    /**
     * Source where to look for [io.gitlab.arturbosch.detekt.api.Extension]s.
     */
    val plugins: Plugins?

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
