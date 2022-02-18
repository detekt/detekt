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

        assertThat(result.output).contains("Build failed with 2 weighted issues.")
        assertThat(result.output).contains("ExitOutsideMain - [kotlinExit]")
        assertThat(result.output).contains("ExitOutsideMain - [javaExit]")
    }
}
