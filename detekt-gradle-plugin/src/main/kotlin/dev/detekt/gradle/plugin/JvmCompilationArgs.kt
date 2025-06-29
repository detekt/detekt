package dev.detekt.gradle.plugin

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional

/**
 * Selection of parameters relevant for compilation analysis on JVM platforms. See
 * [org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions].
 */
interface JvmCompilationArgs : CommonCompilationArgs {
    @get:Internal
    val jdkHome: DirectoryProperty

    @get:Input
    @get:Optional
    val jvmTarget: Property<String>

    @get:Input
    val noJdk: Property<Boolean>
}
