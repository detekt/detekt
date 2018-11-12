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
		|autoCorrect: true
		|failFast: false
		""".trimMargin()

	/**
	 * Each generated file is different so the artifacts are not cached in between test runs
	 */
	private fun ktFileContent(className: String, withCodeSmell: Boolean = false) = """
	|internal class $className(
	|	val randomDefaultValue: String = "$randomString",
	|	val smellyConstant: Int = ${if (withCodeSmell) "42" else "0"}
	|)
	|
	""".trimMargin()

	fun setupProject() {
		writeProjectFile(buildFileName, mainBuildFileContent)
		writeProjectFile(SETTINGS_FILENAME, settingsContent)
		configFileOrNone?.let { writeProjectFile(configFileOrNone, configFileContent) }
		baselineFileOrNone?.let { writeProjectFile(baselineFileOrNone, baselineContent) }
		projectLayout.srcDirs.forEach { sourceDir ->
			(1..projectLayout.numberOfSourceFilesInRootPerSourceDir).forEach {
				writeKtFile(File(rootDir, sourceDir), "MyRoot${it}Class", it == 1)
			}
		}

		projectLayout.submodules.forEach { submodule ->
			val moduleRoot = File(rootDir, submodule.name)
			moduleRoot.mkdirs()
			File(moduleRoot, buildFileName).writeText(submodule.detektConfig ?: "")
			submodule.srcDirs.forEach { moduleSourceDir ->
				(1..submodule.numberOfSourceFiles).forEach {
					writeKtFile(File(moduleRoot, moduleSourceDir), "My${submodule.name}${it}Class", it == 1)
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

    fun runTasksAndCheckResult(vararg tasks: String, doAssert: DslGradleRunner.(BuildResult) -> Unit) {

        val args = listOf("--stacktrace", "--info", "--build-cache") + tasks
        val result = GradleRunner.create().apply {
            withProjectDir(rootDir)
            withPluginClasspath()
            withArguments(args)
            gradleVersionOrNone?.let { withGradleVersion(gradleVersionOrNone) }
        }.build()
        this.doAssert(result)
    }

    fun runDetektTaskAndCheckResult(doAssert: DslGradleRunner.(BuildResult) -> Unit) {
        runTasksAndCheckResult("detekt") { doAssert(it) }
    }

    fun projectFile(path: String): File = File(rootDir, path)

    companion object {
        const val SETTINGS_FILENAME = "settings.gradle"
    }
}
