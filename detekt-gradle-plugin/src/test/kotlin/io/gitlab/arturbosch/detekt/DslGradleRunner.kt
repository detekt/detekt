package io.gitlab.arturbosch.detekt

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import java.io.File
import java.util.UUID

class DslGradleRunner(
    val projectLayout: ProjectLayout,
    val buildFileName: String,
    val mainBuildFileContent: String,
    val configFileOrNone: String? = null,
    val baselineFileOrNone: String? = null,
    val gradleVersionOrNone: String? = null
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
		|	<xml/>
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
	|	val randomDefaultValue: String = "$randomString"
	|) {
	|	val smellyConstant: Int = ${if (withCodeSmell) "11" else "0"}
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

    fun writeProjectFile(filename: String, content: String) =
        File(rootDir, filename).apply { writeText(content) }

    fun writeKtFile(srcDir: String, className: String) {
        writeKtFile(File(rootDir, srcDir), className)
    }

    private fun writeKtFile(dir: File, className: String, withCodeSmell: Boolean = false) {
        dir.mkdirs()
        File(dir, "$className.kt").writeText(ktFileContent(className, withCodeSmell))
    }

    private fun buildGradleRunner(tasks: List<String>): GradleRunner {
        val args = listOf("--stacktrace", "--info", "--build-cache") + tasks.toList()
        val runner = GradleRunner.create().apply {
            withProjectDir(rootDir)
            withPluginClasspath()
            withArguments(args)
            gradleVersionOrNone?.let { withGradleVersion(gradleVersionOrNone) }
        }
        return runner
    }

    fun runTasksAndCheckResult(vararg tasks: String, doAssert: DslGradleRunner.(BuildResult) -> Unit) {
        val result: BuildResult = buildGradleRunner(tasks.toList()).build()
        this.doAssert(result)
    }

    fun runDetektTaskAndCheckResult(doAssert: DslGradleRunner.(BuildResult) -> Unit) {
        runTasksAndCheckResult(DETEKT_TASK) { this.doAssert(it) }
    }

    fun runDetektTaskAndExpectFailure(doAssert: DslGradleRunner.(BuildResult) -> Unit = {}) {
        val result = buildGradleRunner(listOf(DETEKT_TASK)).buildAndFail()
        this.doAssert(result)
    }

    fun projectFile(path: String): File = File(rootDir, path)

    companion object {
        const val SETTINGS_FILENAME = "settings.gradle"
        private const val DETEKT_TASK = "detekt"
    }
}
