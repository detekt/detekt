package io.gitlab.arturbosch.detekt

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Files

class JvmSpec {
    @Test
    fun `Type resolution on JVM`() {
        val resourceDir = File(javaClass.classLoader.getResource("jvm")?.file!!)
        val projectDir: File = Files.createTempDirectory("jvmWithTypeResolution").toFile()
        resourceDir.copyRecursively(projectDir)

        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .withArguments("detektMain")
            .buildAndFail()

        assertThat(result.output).contains("failed with 2 weighted issues.")
        assertThat(result.output).contains("Do not directly exit the process outside the `main` function. Throw an exception(…)")
        assertThat(result.output).contains("Errors.kt:7:9")
        assertThat(result.output).contains("Errors.kt:12:16")
    }
}
