package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.testkit.DslGradleRunner
import io.gitlab.arturbosch.detekt.testkit.ProjectLayout
import org.assertj.core.api.Assertions.assertThat
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.repositories
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.junit.jupiter.api.Test

class DetektJvmSpec {

    val gradleRunner = DslGradleRunner(
        projectLayout = ProjectLayout(numberOfSourceFilesInRootPerSourceDir = 1),
        buildFileName = "build.gradle.kts",
        baselineFiles = listOf("detekt-baseline.xml", "detekt-baseline-main.xml", "detekt-baseline-test.xml"),
        projectScript = {
            apply<KotlinPluginWrapper>()
            apply<DetektPlugin>()
            repositories {
                mavenCentral()
                mavenLocal()
            }
            tasks.withType(Detekt::class.java).configureEach {
                it.reports { reports ->
                    reports.txt.required.set(false)
                }
            }
        },
    ).also(DslGradleRunner::setupProject)

    @Test
    fun `configures detekt type resolution task main`() {
        val project = gradleRunner.buildProject()

        val detektTask = project.tasks.getByPath("detektMain") as Detekt
        val argumentString = detektTask.arguments.get().joinToString(" ")

        assertThat(argumentString).containsPattern("""--baseline \S*[/\\]detekt-baseline-main.xml """)
        assertThat(argumentString).contains("--report xml:")
        assertThat(argumentString).contains("--report sarif:")
        assertThat(argumentString).doesNotContain("--report txt:")
        assertThat(argumentString).contains("--classpath")
    }

    @Test
    fun `configures detekt type resolution task test`() {
        val project = gradleRunner.buildProject()

        val detektTask = project.tasks.getByPath("detektTest") as Detekt
        val argumentString = detektTask.arguments.get().joinToString(" ")

        assertThat(argumentString).containsPattern("""--baseline \S*[/\\]detekt-baseline-test.xml """)
        assertThat(argumentString).contains("--report xml:")
        assertThat(argumentString).contains("--report sarif:")
        assertThat(argumentString).doesNotContain("--report txt:")
        assertThat(argumentString).contains("--classpath")
    }
}
