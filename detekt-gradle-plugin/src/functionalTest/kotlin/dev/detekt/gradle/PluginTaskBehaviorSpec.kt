package dev.detekt.gradle

import dev.detekt.gradle.testkit.DslGradleRunner
import dev.detekt.gradle.testkit.DslTestBuilder.Companion.kotlin
import dev.detekt.gradle.testkit.ProjectLayout
import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.Project
import org.gradle.testkit.runner.TaskOutcome
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Files

/**
 * Tests that run the Detekt Gradle Plugin's tasks multiple times to check for correct
 * UP-TO-DATE states and correct build caching.
 */
class PluginTaskBehaviorSpec {

    private val configFileName = "config.yml"
    private val baselineFileName = "baseline.xml"

    private val detektConfig = """
        detekt {
            config.setFrom("$configFileName")
            baseline = file("$baselineFileName")
        }
    """.trimIndent()

    lateinit var gradleRunner: DslGradleRunner

    @BeforeEach
    fun setupGradleRunner() {
        gradleRunner = kotlin()
            .withDetektConfig(detektConfig)
            .withBaseline(baselineFileName)
            .withConfigFile(configFileName)
            .build()
    }

    @Test
    fun `should be UP-TO-DATE the 2nd run without changes`() {
        gradleRunner.runDetektTaskAndCheckResult { result ->
            assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        }
        gradleRunner.runDetektTaskAndCheckResult { result ->
            assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.UP_TO_DATE)
        }
    }

    @Test
    fun `should pick up build artifacts from the build cache on a 2nd run after deleting the build dir`() {
        gradleRunner.runDetektTaskAndCheckResult { result ->
            assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        }

        gradleRunner.projectFile("build").deleteRecursively()

        // Running detekt again should pick up artifacts from Build Cache
        gradleRunner.runDetektTaskAndCheckResult { result ->
            assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.FROM_CACHE)
        }
    }

    @Test
    fun `should pick up build artifacts from the build cache on a 2nd run after running 'clean'`() {
        gradleRunner.runDetektTaskAndCheckResult { result ->
            assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        }
        gradleRunner.runTasksAndCheckResult("clean", "detekt") { result ->
            assertThat(result.task(":clean")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
            assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.FROM_CACHE)
        }
    }

    @Test
    fun `should pick up build artifacts from the build cache on a 2nd run even if it is in another directory`() {
        gradleRunner.runDetektTaskAndCheckResult { result ->
            assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        }

        val gradleRunner2 = gradleRunner.copy(
            rootDir = Files.createTempDirectory("applyPlugin").toFile().apply { deleteOnExit() }
        ).also { it.setupProject() }
        // Running detekt again should pick up artifacts from Build Cache
        gradleRunner2.runDetektTaskAndCheckResult { result ->
            assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.FROM_CACHE)
        }
    }

    @Test
    fun `should run again after changing config`() {
        @Language("yaml")
        val configFileWithCommentsDisabled = """
            comments:
              active: false
        """.trimIndent()

        gradleRunner.runDetektTaskAndCheckResult { result ->
            assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        }

        // update config file
        gradleRunner.writeProjectFile(configFileName, configFileWithCommentsDisabled)

        gradleRunner.runTasksAndCheckResult("detekt") { result ->
            assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        }
    }

    @Test
    fun `should run again after changing baseline`() {
        @Language("xml")
        val changedBaselineContent = """
            <some>
                <more/>
                <xml/>
            </some>
        """.trimIndent()

        gradleRunner.runDetektTaskAndCheckResult { result ->
            assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        }

        // update baseline file
        gradleRunner.writeProjectFile(baselineFileName, changedBaselineContent)

        gradleRunner.runTasksAndCheckResult("detekt") { result ->
            assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        }
    }

    @Test
    fun `should run again after changing inputs`() {
        gradleRunner.runDetektTaskAndCheckResult { result ->
            assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        }

        // add a new File
        gradleRunner.writeKtFile(gradleRunner.projectLayout.srcDirs.first(), "OtherKotlinClass")

        gradleRunner.runTasksAndCheckResult("detekt") { result ->
            assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        }
    }
}

@Suppress("LongParameterList")
fun DslGradleRunner.copy(
    projectLayout: ProjectLayout = this.projectLayout,
    buildFileName: String = this.buildFileName,
    mainBuildFileContent: String = this.mainBuildFileContent,
    settingsContent: String = this.settingsContent,
    configFileOrNone: String? = this.configFileOrNone,
    baselineFiles: List<String> = this.baselineFiles,
    gradleVersionOrNone: String? = this.gradleVersionOrNone,
    dryRun: Boolean = this.dryRun,
    disableIP: Boolean = this.disableIP,
    failOnGradleWarnings: Boolean = this.failOnGradleWarnings,
    jvmArgs: String = this.jvmArgs,
    gradleProperties: Map<String, String> = this.gradleProperties,
    customPluginClasspath: List<File> = this.customPluginClasspath,
    projectScript: Project.() -> Unit = this.projectScript,
    rootDir: File = this.rootDir,
    randomString: String = this.randomString,
): DslGradleRunner =
    DslGradleRunner(
        projectLayout = projectLayout,
        buildFileName = buildFileName,
        mainBuildFileContent = mainBuildFileContent,
        settingsContent = settingsContent,
        configFileOrNone = configFileOrNone,
        baselineFiles = baselineFiles,
        gradleVersionOrNone = gradleVersionOrNone,
        dryRun = dryRun,
        disableIP = disableIP,
        failOnGradleWarnings = failOnGradleWarnings,
        jvmArgs = jvmArgs,
        gradleProperties = gradleProperties,
        customPluginClasspath = customPluginClasspath,
        projectScript = projectScript,
        rootDir = rootDir,
        randomString = randomString,
    )
