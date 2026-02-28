package dev.detekt.gradle.extensions

import dev.detekt.gradle.plugin.DetektBasePlugin.Companion.DEFAULT_TOP_RULES_TO_SHOW
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

@Suppress("ComplexInterface")
interface DetektExtension {

    val toolVersion: Property<String>

    val ignoreFailures: Property<Boolean>

    val failOnSeverity: Property<FailOnSeverity>

    val reportsDir: DirectoryProperty

    val source: ConfigurableFileCollection

    val baseline: RegularFileProperty

    val basePath: DirectoryProperty

    val enableCompilerPlugin: Property<Boolean>

    val config: ConfigurableFileCollection

    val debug: Property<Boolean>

    val parallel: Property<Boolean>

    val allRules: Property<Boolean>

    val buildUponDefaultConfig: Property<Boolean>

    val disableDefaultRuleSets: Property<Boolean>

    val autoCorrect: Property<Boolean>

    /**
     * Number of slowest rules to display in profiling task console output.
     * Defaults to [DEFAULT_TOP_RULES_TO_SHOW]. Set to [Int.MAX_VALUE] to see all rules.
     */
    val topRulesToShow: Property<Int>

    /**
     * List of Android build variants for which no detekt task should be created.
     *
     * This is a combination of build types and flavors, such as fooDebug or barRelease.
     */
    val ignoredVariants: ListProperty<String>

    /**
     * List of Android build types for which no detekt task should be created.
     */
    val ignoredBuildTypes: ListProperty<String>

    /**
     * List of Android build flavors for which no detekt task should be created
     */
    val ignoredFlavors: ListProperty<String>
}
