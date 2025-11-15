package dev.detekt.gradle.testkit

import org.gradle.testkit.runner.GradleRunner
import java.io.File
import java.nio.file.Files

/**
 * Copy project files from `resources` to temporary directories for isolation.
 * This helps with the incremental build (up-to-date checks).
 */
fun GradleRunner.withResourceDir(resourcePath: String) =
    apply {
        val resourceDir = File(javaClass.classLoader.getResource(resourcePath).file)
        val projectDir = Files.createTempDirectory(resourcePath).toFile()
        resourceDir.copyRecursively(projectDir)
        withProjectDir(projectDir)
    }
