package io.gitlab.arturbosch.detekt

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import java.io.File
import java.util.UUID

class DslGradleRunner @Suppress("LongParameterList") constructor(
    val projectLayout: ProjectLayout,
    val buildFileName: String,
    val mainBuildFileContent: String,
    val configFileOrNone: String? = null,
    val baselineFileOrNone: String? = null,
    val gradleVersionOrNone: String? = null,
    val dryRun: Boolean = false
) {

    private val rootDir: File = createTempDir(prefix = "applyPlugin")
    private val randomString = UUID.randomUUID().toString()

    private val settingsContent = """
        |rootProject.name = "rootDir-project"
        |include(${projectLayout.submodules.map { "\"${it.name}\"" }.joinToString(",")})
        |
        """.trimMargin()

    private val baselineContent = """
        |<some>
        |   <xml/>
        |</some>
        """.trimMargin()

    private val configFileContent = """
        |build:
        |  maxIssues: 5
        |style:
        |  MagicNumber:
        |    active: true
        """.trimMargin()

    /**
     * Each generated file is different so the artifacts are not cached in between test runs
     */
    private fun ktFileContent(className: String, withCodeSmell: Boolean = false) = """
    |internal class $className(
    |   val randomDefaultValue: String = "$randomString"
    |) {
    |   val smellyConstant: Int = ${if (withCodeSmell) "11" else "0"}
    |}
    |
    """.trimMargin()

    fun setupProject() {
        writeProjectFile(buildFileName, mainBuildFileContent)
        writeProjectFile(SETTINGS_FILENAME, settingsContent)
        configFileOrNone?.let { writeProjectFile(configFileOrNone, configFileContent) }
        baselineFileOrNone?.let { writeProjectFile(baselineFileOrNone, baselineContent) }
        projectLayout.srcDirs.forEachIndexed { srcDirIdx, sourceDir ->
            repeat(projectLayout.numberOfSourceFilesInRootPerSourceDir) {
                val withCodeSmell =
                    srcDirIdx * projectLayout.numberOfSourceFilesInRootPerSourceDir + it < projectLayout.numberOfCodeSmellsInRootPerSourceDir
                writeKtFile(File(rootDir, sourceDir), "MyRoot${it}Class", withCodeSmell)
            }
        }

        projectLayout.submodules.forEach { submodule ->
            val moduleRoot = File(rootDir, submodule.name)
            moduleRoot.mkdirs()
            File(moduleRoot, buildFileName).writeText(submodule.detektConfig ?: "")
            submodule.srcDirs.forEachIndexed { srcDirIdx, moduleSourceDir ->
                repeat(submodule.numberOfSourceFilesPerSourceDir) {
                    val withCodeSmell =
                        srcDirIdx * submodule.numberOfSourceFilesPerSourceDir + it < submodule.numberOfCodeSmells
                    writeKtFile(File(moduleRoot, moduleSourceDir), "My${submodule.name}${it}Class", withCodeSmell)
                }
            }
        }
    }

    fun writeProjectFile(filename: String, content: String) {
        File(rootDir, filename).writeText(content)
    }

    fun writeKtFile(srcDir: String, className: String) {
        writeKtFile(File(rootDir, srcDir), className)
    }

    private fun writeKtFile(dir: File, className: String, withCodeSmell: Boolean = false) {
        dir.mkdirs()
        File(dir, "$className.kt").writeText(ktFileContent(className, withCodeSmell))
    }

    private fun buildGradleRunner(tasks: List<String>): GradleRunner {
        val args = mutableListOf("--stacktrace", "--info", "--build-cache")
        if (dryRun) {
            args.add("-Pdetekt-dry-run=true")
        }
        args.addAll(tasks.toList())

        return GradleRunner.create().apply {
            withProjectDir(rootDir)
            withPluginClasspath()
            withArguments(args)
            gradleVersionOrNone?.let { withGradleVersion(gradleVersionOrNone) }
        }
    }

    fun runTasksAndCheckResult(vararg tasks: String, doAssert: DslGradleRunner.(BuildResult) -> Unit) {
        val result: BuildResult = runTasks(*tasks)
        this.doAssert(result)
    }

    fun runTasks(vararg tasks: String): BuildResult = buildGradleRunner(tasks.toList()).build()

    fun runDetektTaskAndCheckResult(doAssert: DslGradleRunner.(BuildResult) -> Unit) {
        runTasksAndCheckResult(DETEKT_TASK) { this.doAssert(it) }
    }

    fun runDetektTask(): BuildResult = runTasks(DETEKT_TASK)

    fun runDetektTaskAndExpectFailure(doAssert: DslGradleRunner.(BuildResult) -> Unit = {}) {
        val result = buildGradleRunner(listOf(DETEKT_TASK)).buildAndFail()
        this.doAssert(result)
    }

    fun projectFile(path: String): File = File(rootDir, path).canonicalFile

    companion object {
        const val SETTINGS_FILENAME = "settings.gradle"
        private const val DETEKT_TASK = "detekt"
    }
}
