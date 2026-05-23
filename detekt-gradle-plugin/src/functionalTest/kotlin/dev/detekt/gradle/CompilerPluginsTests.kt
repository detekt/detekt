package dev.detekt.gradle

import dev.detekt.gradle.testkit.withResourceDir
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test

class CompilerPluginsTests {
    @Test
    fun `detekt works with @Parzelize`() {
        val result = GradleRunner.create()
            .withResourceDir("android-parcelize")
            .withPluginClasspath()
            .withArguments("detektMain")
            .build()

        assertThat(result.output)
            .doesNotContainPattern("There were [0-9]+ compiler errors found during analysis".toRegex().toPattern())
    }
    @Test
    fun `detekt works with BuildConfig`() {
        val result = GradleRunner.create()
            .withResourceDir("android-buildconfig")
            .withPluginClasspath()
            .withArguments("detektMain")
            .build()

        assertThat(result.output)
            .doesNotContainPattern("There were [0-9]+ compiler errors found during analysis".toRegex().toPattern())
    }
}
