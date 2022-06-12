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
                    mavenLocal()
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
                    mavenLocal()
                }

                tasks.withType(Detekt::class.java).configureEach {
                    it.reports { reports ->
                        reports.sarif.required.set(true)
                        reports.txt.required.set(false)
                    }
                }
            },
        ).also { it.setupProject() }

        @Test
        fun `configures detekt plain task`() {
            val project = gradleRunner.buildProject()

            val detektTask = project.tasks.getByPath("detekt") as Detekt
            val argumentString = detektTask.arguments.get().joinToString(" ")

            assertThat(argumentString).containsPattern("""--baseline \S*[/\\]detekt-baseline.xml """)
            assertThat(argumentString).contains("--report xml:")
            assertThat(argumentString).contains("--report sarif:")
            assertThat(argumentString).doesNotContain("--report txt:")
            assertThat(argumentString).doesNotContain("--classpath")
        }
    }
}
