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

class DetektSpec {

    @Nested
    inner class `Console Report` {
        @Test
        fun `default behavior`() {
            val detektTask = detektTask()

            val arguments = detektTask.arguments
            val argumentString = arguments.joinToString(" ")

            assertThat(argumentString).contains("--console-report LiteIssuesReport ")
            assertThat(arguments.count { it == "--console-report" }).isEqualTo(1)
        }

        @Test
        fun `no value`() {
            val detektTask = detektTask {
                consoleReports.empty()
            }

            val arguments = detektTask.arguments
            val argumentString = arguments.joinToString(" ")

            assertThat(argumentString).contains("--console-report  ")
            assertThat(arguments.count { it == "--console-report" }).isEqualTo(1)
        }

        @Test
        fun `single value`() {
            val detektTask = detektTask {
                consoleReports.add("foo")
            }

            val arguments = detektTask.arguments
            val argumentString = arguments.joinToString(" ")

            assertThat(argumentString).contains("--console-report foo ")
            assertThat(arguments.count { it == "--console-report" }).isEqualTo(1)
        }

        @Test
        fun `two value`() {
            val detektTask = detektTask {
                consoleReports.add("foo")
                consoleReports.add("bar")
            }

            val arguments = detektTask.arguments
            val argumentString = arguments.joinToString(" ")

            assertThat(argumentString).contains("--console-report foo ")
            assertThat(argumentString).contains("--console-report bar ")
            assertThat(arguments.count { it == "--console-report" }).isEqualTo(2)
        }
    }
}

private fun detektTask(configureExtension: DetektExtension.() -> Unit = {}) =
    DslGradleRunner(
        projectLayout = ProjectLayout(numberOfSourceFilesInRootPerSourceDir = 1),
        buildFileName = "build.gradle.kts",
        projectScript = {
            apply<DetektPlugin>()
            apply<KotlinPluginWrapper>()

            repositories {
                mavenCentral()
            }

            configure<DetektExtension>(configureExtension)
        },
    )
        .also(DslGradleRunner::setupProject)
        .buildProject()
        .tasks
        .getByPath("detekt") as Detekt
