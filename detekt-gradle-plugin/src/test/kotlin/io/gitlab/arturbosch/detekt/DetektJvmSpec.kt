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
            }
            tasks.withType(Detekt::class.java).configureEach {
                it.reports { reports ->
                    reports.md.required.set(false)
                }
            }
            tasks.named("detektTest", Detekt::class.java) {
                it.jvmTarget.set("1.8")
                it.languageVersion.set("1.6")
                it.apiVersion.set("1.5")
            }
        },
    ).also(DslGradleRunner::setupProject)

    @Test
    fun `configures detekt type resolution task main`() {
        val project = gradleRunner.buildProject()

        val detektTask = project.tasks.getByPath("detektMain") as Detekt
        val argumentString = detektTask.arguments.joinToString(" ")

        assertThat(argumentString).containsPattern("""--baseline \S*[/\\]detekt-baseline-main.xml """)
        assertThat(argumentString).contains("--report xml:")
        assertThat(argumentString).contains("--report sarif:")
        assertThat(argumentString).doesNotContain("--report md:")
        assertThat(argumentString).contains("--classpath")
        assertThat(argumentString).contains("--analysis-mode full")
        assertThat(argumentString).doesNotContain("--api-version")
        assertThat(argumentString).doesNotContain("--language-version")
        assertThat(argumentString).contains("--fail-on-severity error")
    }

    @Test
    fun `configures detekt type resolution task test`() {
        val project = gradleRunner.buildProject()

        val detektTask = project.tasks.getByPath("detektTest") as Detekt
        val argumentString = detektTask.arguments.joinToString(" ")

        assertThat(argumentString).containsPattern("""--baseline \S*[/\\]detekt-baseline-test.xml """)
        assertThat(argumentString).contains("--report xml:")
        assertThat(argumentString).contains("--report sarif:")
        assertThat(argumentString).doesNotContain("--report md:")
        assertThat(argumentString).contains("--classpath")
        assertThat(argumentString).contains("--analysis-mode full")
        assertThat(argumentString).contains("--jvm-target 1.8")
        assertThat(argumentString).contains("--api-version 1.5")
        assertThat(argumentString).contains("--language-version 1.6")
        assertThat(argumentString).contains("--fail-on-severity error")
    }
}
