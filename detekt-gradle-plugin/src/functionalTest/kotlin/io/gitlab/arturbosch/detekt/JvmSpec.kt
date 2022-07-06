package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.testkit.withResourceDir
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test

class JvmSpec {
    @Test
    fun `Type resolution on JVM`() {
        val result = GradleRunner.create()
            .withResourceDir("jvm")
            .withPluginClasspath()
            .withArguments("detektMain")
            .buildAndFail()

        assertThat(result.output).contains("failed with 2 weighted issues.")
        assertThat(result.output).contains(
            "Do not directly exit the process outside the `main` function. Throw an exception(...)"
        )
        assertThat(result.output).contains("Errors.kt:7:9")
        assertThat(result.output).contains("Errors.kt:12:16")
    }
}
