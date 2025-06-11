package dev.detekt.gradle.plugin

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Console
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.options.Option

interface DetektBase : JvmCompilationArgs {

    @get:Console
    val debug: Property<Boolean>

    @get:Internal
    val parallel: Property<Boolean>

    val baseline: RegularFileProperty

    @get:InputFiles
    @get:Optional
    @get:PathSensitive(PathSensitivity.RELATIVE)
    val config: ConfigurableFileCollection

    @get:Input
    @get:Optional
    val disableDefaultRuleSets: Property<Boolean>

    @get:Input
    @get:Optional
    val buildUponDefaultConfig: Property<Boolean>

    @get:Input
    @get:Optional
    val ignoreFailures: Property<Boolean>

    @get:Input
    @get:Optional
    val allRules: Property<Boolean>

    /**
     * Respect only the file path for incremental build. Using @InputFile respects both file path and content.
     */
    @get:Input
    @get:Optional
    val basePath: Property<String>

    @get:Input
    @get:Optional
    @get:Option(option = "auto-correct", description = "Allow rules to auto correct code if they support it")
    val autoCorrect: Property<Boolean>
}
