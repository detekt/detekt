package dev.detekt.gradle

import dev.detekt.gradle.extensions.DetektExtension
import dev.detekt.gradle.plugin.DetektPlugin
import dev.detekt.gradle.testkit.DslGradleRunner
import dev.detekt.gradle.testkit.ProjectLayout
import org.assertj.core.api.Assertions.assertThat
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.repositories
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class DetektProfilingPropertiesSpec {

    @Nested
    inner class `Detekt task profiling properties` {

        private val gradleRunner = DslGradleRunner(
            projectLayout = ProjectLayout(numberOfSourceFilesInRootPerSourceDir = 1),
            buildFileName = "build.gradle.kts",
            projectScript = {
                apply<KotlinPluginWrapper>()
                apply<DetektPlugin>()

                repositories {
                    mavenCentral()
                }
            },
        ).also { it.setupProject() }

        @Test
        fun `enableProfiling is set to false when profiling task is not in the graph`() {
            // Note: DetektPlugin registers a DetektProfilingTask which configures the detekt task
            // with enableProfiling = true when the task is in the graph
            val project = gradleRunner.buildProject()

            val detektTask = project.tasks.getByPath("detekt") as Detekt

            assertThat(detektTask.profile.get()).isFalse()
        }

        @Test
        fun `parallel defaults to false from extension`() {
            // The default parallel value comes from DetektExtension which defaults to false
            val project = gradleRunner.buildProject()

            val detektTask = project.tasks.getByPath("detekt") as Detekt

            assertThat(detektTask.parallel.get()).isFalse()
        }

        @Test
        fun `arguments include profiling when enabled`() {
            val project = gradleRunner.buildProject()

            val detektTask = project.tasks.getByPath("detekt") as Detekt
            detektTask.profile.set(true)

            val argumentString = detektTask.arguments.joinToString(" ")

            assertThat(argumentString).contains("--profiling")
        }

        @Test
        fun `arguments do not include profiling when disabled`() {
            val project = gradleRunner.buildProject()

            val detektTask = project.tasks.getByPath("detekt") as Detekt
            detektTask.profile.set(false)

            val argumentString = detektTask.arguments.joinToString(" ")

            assertThat(argumentString).doesNotContain("--profiling")
        }

        @Test
        fun `disables parallel when profiling is enabled`() {
            val project = gradleRunner.buildProject()

            val detektTask = project.tasks.getByPath("detekt") as Detekt
            detektTask.profile.set(true)
            detektTask.parallel.set(true)

            val argumentString = detektTask.arguments.joinToString(" ")

            // --parallel should not be present when profiling is enabled
            assertThat(argumentString).doesNotContain("--parallel")
        }

        @Test
        fun `enables parallel when profiling is disabled`() {
            val project = gradleRunner.buildProject()

            val detektTask = project.tasks.getByPath("detekt") as Detekt
            detektTask.profile.set(false)
            detektTask.parallel.set(true)

            val argumentString = detektTask.arguments.joinToString(" ")

            assertThat(argumentString).contains("--parallel")
        }

        @Test
        fun `does not enable parallel when profiling is disabled if parallel is disabled`() {
            val project = gradleRunner.buildProject()

            val detektTask = project.tasks.getByPath("detekt") as Detekt
            detektTask.parallel.set(false)
            detektTask.profile.set(false)

            val argumentString = detektTask.arguments.joinToString(" ")

            assertThat(argumentString).doesNotContain("--parallel")
        }
    }

    @Nested
    inner class `DetektExtension topRulesToShow property` {

        private val gradleRunner = DslGradleRunner(
            projectLayout = ProjectLayout(numberOfSourceFilesInRootPerSourceDir = 1),
            buildFileName = "build.gradle.kts",
            projectScript = {
                apply<KotlinPluginWrapper>()
                apply<DetektPlugin>()

                repositories {
                    mavenCentral()
                }

                configure<DetektExtension> {
                    topRulesToShow.set(20)
                }
            },
        ).also { it.setupProject() }

        @Test
        fun `can configure topRulesToShow`() {
            val project = gradleRunner.buildProject()

            val extension = project.extensions.getByType(DetektExtension::class.java)

            assertThat(extension.topRulesToShow.get()).isEqualTo(20)
        }
    }

    @Nested
    inner class `Detekt task profiling report` {

        private val gradleRunner = DslGradleRunner(
            projectLayout = ProjectLayout(numberOfSourceFilesInRootPerSourceDir = 1),
            buildFileName = "build.gradle.kts",
            projectScript = {
                apply<KotlinPluginWrapper>()
                apply<DetektPlugin>()

                repositories {
                    mavenCentral()
                }
            },
        ).also { it.setupProject() }

        @Test
        fun `includes profiling report argument when profiling enabled with output`() {
            val project = gradleRunner.buildProject()

            val detektTask = project.tasks.getByPath("detekt") as Detekt
            detektTask.profile.set(true)
            detektTask.profileOutput.set(project.layout.buildDirectory.file("profiling.csv"))

            val argumentString = detektTask.arguments.joinToString(" ")

            assertThat(argumentString).contains("--report")
            assertThat(argumentString).contains("profiling:")
        }

        @Test
        fun `does not include profiling report when disabled`() {
            val project = gradleRunner.buildProject()

            val detektTask = project.tasks.getByPath("detekt") as Detekt
            detektTask.profile.set(false)
            detektTask.profileOutput.set(project.layout.buildDirectory.file("profiling.csv"))

            val argumentString = detektTask.arguments.joinToString(" ")

            assertThat(argumentString).doesNotContain("profiling:")
        }
    }
}
