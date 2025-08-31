package dev.detekt.gradle.testkit

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.intellij.lang.annotations.Language
import java.io.File
import java.nio.file.Files
import java.util.UUID

@Suppress("TooManyFunctions", "ClassOrdering")
class DslGradleRunner
@Suppress("LongParameterList")
constructor(
    val projectLayout: ProjectLayout,
    val buildFileName: String,
    @Language("gradle.kts")
    val mainBuildFileContent: String = "",
    val settingsContent: String = "",
    val configFileOrNone: String? = null,
    val baselineFiles: List<String> = emptyList(),
    val gradleVersionOrNone: String? = null,
    val dryRun: Boolean = false,
    var disableIP: Boolean = false,
    val jvmArgs: String = "-Xmx2g -XX:MaxMetaspaceSize=1g",
    val gradleProperties: Map<String, String> = emptyMap(),
    val customPluginClasspath: List<File> = emptyList(),
    val projectScript: Project.() -> Unit = {},
) {

    private val rootDir: File = Files.createTempDirectory("applyPlugin").toFile().apply { deleteOnExit() }
    private val randomString = UUID.randomUUID().toString()

    @Language("xml")
    private val baselineContent = """
        <some>
            <xml/>
        </some>
    """.trimIndent()

    @Language("yaml")
    private val configFileContent = """
        style:
          MagicNumber:
            active: true
    """.trimIndent()

    /**
     * Each generated file is different so the artifacts are not cached in between test runs
     */
    @Language("kotlin")
    private fun ktFileContent(className: String, withFinding: Boolean = false): String =
        """
            internal class $className(
                val randomDefaultValue: String = "$randomString"
            ) {
                val smellyConstant: Int = ${if (withFinding) "11" else "0"}
            }
            
        """.trimIndent() // Last line empty to prevent NewLineAtEndOfFile.

    fun setupProject() {
        writeProjectFile(buildFileName, mainBuildFileContent)
        val finalSettingsContent =
            """
                $settingsContent
                rootProject.name = "rootDir-project"
                include(${projectLayout.submodules.joinToString { "\"${it.name}\"" }})
            """.trimIndent()
        writeProjectFile(SETTINGS_FILENAME, finalSettingsContent)
        configFileOrNone?.let { writeProjectFile(it, configFileContent) }
        baselineFiles.forEach { file -> writeProjectFile(file, baselineContent) }
        projectLayout.srcDirs.forEachIndexed { srcDirIdx, sourceDir ->
            repeat(projectLayout.numberOfSourceFilesInRootPerSourceDir) { srcFileIndex ->
                val withFinding =
                    srcDirIdx * projectLayout.numberOfSourceFilesInRootPerSourceDir +
                        srcFileIndex < projectLayout.numberOfFindingsInRootPerSourceDir
                writeKtFile(
                    dir = File(rootDir, sourceDir),
                    className = "My${srcDirIdx}Root${srcFileIndex}Class",
                    withFinding = withFinding
                )
            }
        }

        projectLayout.submodules.forEach { submodule ->
            submodule.writeModuleFile(buildFileName, submodule.buildFileContent.orEmpty())
            submodule.baselineFiles.forEach { file -> submodule.writeModuleFile(file, baselineContent) }
            submodule.srcDirs.forEachIndexed { srcDirIdx, moduleSourceDir ->
                repeat(submodule.numberOfSourceFilesPerSourceDir) {
                    val withFinding =
                        srcDirIdx * submodule.numberOfSourceFilesPerSourceDir + it < submodule.numberOfFindings
                    writeKtFile(
                        dir = File(submodule.moduleRoot, moduleSourceDir),
                        className = "My$srcDirIdx${submodule.name}${it}Class",
                        withFinding = withFinding
                    )
                }
            }
        }
    }

    fun projectFile(path: String): File = File(rootDir, path).canonicalFile

    fun writeProjectFile(filename: String, content: String) {
        File(rootDir, filename)
            .also { it.parentFile.mkdirs() }
            .writeText(content)
    }

    fun writeKtFile(srcDir: String, className: String) {
        writeKtFile(File(rootDir, srcDir), className)
    }

    private fun writeKtFile(dir: File, className: String, withFinding: Boolean = false) {
        dir.mkdirs()
        File(dir, "$className.kt").writeText(ktFileContent(className, withFinding))
    }

    private fun Submodule.writeModuleFile(filename: String, content: String) {
        File(moduleRoot, filename).writeText(content)
    }

    private val Submodule.moduleRoot: File
        get() = File(rootDir, name).apply { mkdirs() }

    fun buildProject(): Project = ProjectBuilder.builder()
        .withProjectDir(rootDir)
        .build()
        .apply(projectScript)

    private fun buildGradleRunner(tasks: List<String>): GradleRunner {
        val args = buildList {
            add("--stacktrace")
            add("--info")
            add("--build-cache")
            add("-Dorg.gradle.jvmargs=$jvmArgs")
            if (dryRun) {
                add("-Pdetekt-dry-run=true")
            }
            if (!disableIP) {
                add("-Dorg.gradle.unsafe.isolated-projects=true")
            }
            addAll(gradleProperties.toList().map { (key, value) -> "-P$key=$value" })
            addAll(tasks)
        }

        return GradleRunner.create().apply {
            withProjectDir(rootDir)
            if (customPluginClasspath.isNotEmpty()) {
                withPluginClasspath(customPluginClasspath)
            } else {
                withPluginClasspath()
            }
            withArguments(args)
            gradleVersionOrNone?.let(::withGradleVersion)
        }
    }

    fun runTasksAndCheckResult(vararg tasks: String, doAssert: DslGradleRunner.(BuildResult) -> Unit) {
        this.doAssert(runTasks(*tasks))
    }

    fun runTasks(vararg tasks: String): BuildResult = buildGradleRunner(tasks.toList()).build()

    fun runTasksAndExpectFailure(vararg tasks: String, doAssert: DslGradleRunner.(BuildResult) -> Unit) {
        val result: BuildResult = buildGradleRunner(tasks.toList()).buildAndFail()
        this.doAssert(result)
    }

    fun runDetektTaskAndCheckResult(doAssert: DslGradleRunner.(BuildResult) -> Unit) {
        runTasksAndCheckResult(DETEKT_TASK) { this.doAssert(it) }
    }

    fun runDetektTask(vararg args: String): BuildResult = runTasks(DETEKT_TASK, *args)

    fun runDetektTaskAndExpectFailure(doAssert: DslGradleRunner.(BuildResult) -> Unit = {}) {
        val result = buildGradleRunner(listOf(DETEKT_TASK)).buildAndFail()
        this.doAssert(result)
    }

    companion object {
        private const val SETTINGS_FILENAME = "settings.gradle"
        private const val DETEKT_TASK = "detekt"
    }
}
