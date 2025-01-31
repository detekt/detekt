package dev.detekt.gradle.plugin

import org.gradle.api.Incubating
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional

/**
 * Mashup of base/common compilation parameters from:
 * * [org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerOptions]
 * * [org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerToolOptions]
 * * [org.jetbrains.kotlin.gradle.tasks.KotlinCompileTool]
 * * [org.jetbrains.kotlin.gradle.tasks.BaseKotlinCompile]
 */
interface CommonCompilationArgs {
    // org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerOptions
    @get:Optional
    @get:Input
    val apiVersion: Property<String>

    // org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerOptions
    @get:Optional
    @get:Input
    val languageVersion: Property<String>

    // org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerOptions
    @get:Input
    val optIn: ListProperty<String>

    // org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerToolOptions
    @get:Input
    @get:Incubating
    val freeCompilerArgs: ListProperty<String>

    // org.jetbrains.kotlin.gradle.tasks.KotlinCompileTool (as 'libraries')
    @get:Classpath
    @get:Optional
    val classpath: ConfigurableFileCollection

    // org.jetbrains.kotlin.gradle.tasks.BaseKotlinCompile
    @get:Internal
    val friendPaths: ConfigurableFileCollection

    // org.jetbrains.kotlin.gradle.tasks.BaseKotlinCompile
    @get:Input
    val multiPlatformEnabled: Property<Boolean>
}
