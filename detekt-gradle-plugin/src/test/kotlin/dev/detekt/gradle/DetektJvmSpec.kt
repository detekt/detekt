package dev.detekt.gradle

import dev.detekt.gradle.extensions.DetektExtension
import dev.detekt.gradle.plugin.DetektKotlinCompilerPlugin
import dev.detekt.gradle.plugin.DetektPlugin
import dev.detekt.gradle.testkit.DslGradleRunner
import dev.detekt.gradle.testkit.ProjectLayout
import org.assertj.core.api.Assertions.assertThat
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.repositories
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmExtension
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
                    reports.markdown.required.set(false)
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
        assertThat(argumentString).contains("--report checkstyle:")
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
        assertThat(argumentString).contains("--report checkstyle:")
        assertThat(argumentString).contains("--report sarif:")
        assertThat(argumentString).doesNotContain("--report md:")
        assertThat(argumentString).contains("--classpath")
        assertThat(argumentString).contains("--analysis-mode full")
        assertThat(argumentString).contains("--jvm-target 1.8")
        assertThat(argumentString).contains("--api-version 1.5")
        assertThat(argumentString).contains("--language-version 1.6")
        assertThat(argumentString).contains("--fail-on-severity error")
    }

    @Test
    fun `uses separate fragment directories for main and test baseline tasks`() {
        val project = gradleRunner.buildProject()
        val baseDirectory = project.layout.projectDirectory.dir("detekt-baseline.d")
        project.extensions.getByType<DetektExtension>().baselineFragments.set(baseDirectory)

        val main = project.tasks.getByPath("detektBaselineMain") as DetektCreateBaselineTask
        val test = project.tasks.getByPath("detektBaselineTest") as DetektCreateBaselineTask

        assertThat(main.baselineFragments.get().asFile.name).isEqualTo("detekt-baseline-main.d")
        assertThat(test.baselineFragments.get().asFile.name).isEqualTo("detekt-baseline-test.d")
        assertThat(main.baselineFragments.get().asFile).isNotEqualTo(test.baselineFragments.get().asFile)
    }

    @Test
    fun `analysis fragment directory prefers variant and falls back to base`() {
        val project = gradleRunner.buildProject()
        val baseDirectory = project.layout.projectDirectory.dir("detekt-baseline.d")
        baseDirectory.asFile.mkdirs()
        project.layout.projectDirectory.dir("detekt-baseline-main.d").asFile.mkdirs()
        project.extensions.getByType<DetektExtension>().baselineFragments.set(baseDirectory)

        val main = project.tasks.getByPath("detektMain") as Detekt
        val test = project.tasks.getByPath("detektTest") as Detekt

        assertThat(main.baselineFragments.get().asFile.name).isEqualTo("detekt-baseline-main.d")
        assertThat(test.baselineFragments.get().asFile.name).isEqualTo("detekt-baseline.d")
    }

    @Test
    fun `compiler task tracks base and variant fragment directories as inputs`() {
        val compilerRunner = DslGradleRunner(
            projectLayout = ProjectLayout(numberOfSourceFilesInRootPerSourceDir = 1),
            buildFileName = "build.gradle.kts",
            projectScript = {
                apply<KotlinPluginWrapper>()
                apply<DetektKotlinCompilerPlugin>()
                repositories {
                    mavenCentral()
                }
                extensions.getByType<DetektExtension>().baselineFragments.set(
                    layout.projectDirectory.dir("detekt-baseline.d")
                )
            },
        ).also(DslGradleRunner::setupProject)
        val project = compilerRunner.buildProject()
        val compilerPlugin = project.plugins.getPlugin(DetektKotlinCompilerPlugin::class.java)
        val mainCompilation = project.extensions.getByType<KotlinJvmExtension>().target.compilations.getByName("main")
        compilerPlugin.applyToCompilation(mainCompilation).get()
        val compileKotlin = project.tasks.getByPath("compileKotlin")

        assertThat(compileKotlin.inputs.files.files.map { it.name })
            .contains("detekt-baseline.d", "detekt-baseline-main.d")
    }
}
