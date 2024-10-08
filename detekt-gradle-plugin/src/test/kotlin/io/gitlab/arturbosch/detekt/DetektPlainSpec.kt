package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.testkit.DslGradleRunner
import io.gitlab.arturbosch.detekt.testkit.ProjectLayout
import io.gitlab.arturbosch.detekt.testkit.dependenciesAsPaths
import org.assertj.core.api.Assertions.assertThat
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.repositories
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class DetektPlainSpec {
    @Nested
    inner class `When detekt is applied before JVM plugin` {
        val gradleRunner = DslGradleRunner(
            projectLayout = ProjectLayout(numberOfSourceFilesInRootPerSourceDir = 1),
            buildFileName = "build.gradle.kts",
            projectScript = {
                apply<DetektPlugin>()
                apply<KotlinPluginWrapper>() // org.jetbrains.kotlin.jvm

                repositories {
                    mavenCentral()
                }

                configure<DetektExtension> {
                }
            },
        ).also { it.setupProject() }

        @Test
        fun `lazily adds detekt as a dependency of the 'check' task`() {
            val project = gradleRunner.buildProject()

            assertThat(project.tasks["check"].dependenciesAsPaths()).contains(":detekt")
        }
    }

    @Test
    fun `resolves kotlin version from manifest`() {
        val version = getSupportedKotlinVersion()

        assertThat(version).isNotBlank()
    }
}
