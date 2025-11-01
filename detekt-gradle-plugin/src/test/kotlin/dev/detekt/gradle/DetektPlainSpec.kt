package dev.detekt.gradle

import dev.detekt.gradle.extensions.DetektExtension
import dev.detekt.gradle.plugin.DetektPlugin
import dev.detekt.gradle.plugin.getSupportedKotlinVersion
import dev.detekt.gradle.testkit.DslGradleRunner
import dev.detekt.gradle.testkit.ProjectLayout
import dev.detekt.gradle.testkit.dependenciesAsPaths
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

    @Nested
    inner class `When applying detekt in a project` {
        val gradleRunner = DslGradleRunner(
            projectLayout = ProjectLayout(numberOfSourceFilesInRootPerSourceDir = 1),
            buildFileName = "build.gradle.kts",
            baselineFiles = listOf("detekt-baseline.xml"),
            projectScript = {
                apply<KotlinPluginWrapper>() // org.jetbrains.kotlin.jvm
                apply<DetektPlugin>()

                repositories {
                    mavenCentral()
                }

                tasks.withType(Detekt::class.java).configureEach {
                    it.reports { reports ->
                        reports.sarif.required.set(true)
                        reports.markdown.required.set(false)
                    }
                }
            },
        ).also { it.setupProject() }

        @Test
        fun `configures detekt plain task`() {
            val project = gradleRunner.buildProject()

            val detektTask = project.tasks.getByPath("detekt") as Detekt
            val argumentString = detektTask.arguments.joinToString(" ")

            assertThat(argumentString).containsPattern("""--baseline \S*[/\\]detekt-baseline.xml """)
            assertThat(argumentString).contains("--report checkstyle ")
            assertThat(argumentString).contains("--report sarif ")
            assertThat(argumentString).doesNotContain("--report md ")
            assertThat(argumentString).doesNotContain("--classpath")
            assertThat(argumentString).contains("--analysis-mode light")
            assertThat(argumentString).contains("--fail-on-severity error")
        }
    }

    @Test
    fun `resolves kotlin version from manifest`() {
        val version = getSupportedKotlinVersion()

        assertThat(version).isNotBlank()
    }
}
