package io.gitlab.arturbosch.detekt

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import java.io.File

class JvmSpec {
    @Test
    fun `Type resolution on JVM`() {
        val projectDir = checkNotNull(javaClass.classLoader.getResource("jvm")?.file)
        val result = GradleRunner.create()
            .withProjectDir(File(projectDir))
            .withPluginClasspath()
            .withArguments("detektMain")
            .buildAndFail()

        assertThat(result.output).contains("failed with 2 weighted issues.")
        assertThat(result.output).contains("Do not directly exit the process outside the `main` function. Throw an exception(…)")
        assertThat(result.output).contains("Errors.kt:7:9")
        assertThat(result.output).contains("Errors.kt:12:16")
    }
}
