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
		val baselineFileOrNone: String? = null
) {

	private val rootDir: File = createTempDir(prefix = "applyPlugin")
	private val cacheDir = createTempDir(prefix = "cache")
	private val randomString = UUID.randomUUID().toString()

	private val settingsContent = """
    	|rootProject.name = "rootDir-project"
		|include(${projectLayout.submodules.map { "\"${it.name}\"" }.joinToString(",")})
		|
		|// Include original detekt dependencies as composite build
		|includeBuild("${System.getProperty("user.dir")}/../") {
		|    dependencySubstitution {
		|    	 // Use local detekt-cli to be able to use local changes to the CLI in Gradle Plugin
		|    	 // tests immediately.
		|        substitute module("io.gitlab.arturbosch.detekt:detekt-cli") with project(":detekt-cli")
		|    }
		|}
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
	private fun ktFileContent(className: String) = """
	|internal class $className(val randomDefaultValue: String = "$randomString")
	|
	""".trimMargin()

	fun setupProject() {
		writeProjectFile(buildFileName, mainBuildFileContent)
		writeProjectFile(SETTINGS_FILENAME, settingsContent)
		configFileOrNone?.let { writeProjectFile(configFileOrNone, configFileContent) }
		baselineFileOrNone?.let { writeProjectFile(baselineFileOrNone, baselineContent) }
		projectLayout.srcDirs.forEach { sourceDir ->
			(1..projectLayout.numberOfSourceFilesInRootPerSourceDir).forEach {
				writeKtFile(File(rootDir, sourceDir), "MyRoot${it}Class.kt")
			}
		}

		projectLayout.submodules.forEach { submodule ->
			val moduleRoot = File(rootDir, submodule.name)
			moduleRoot.mkdirs()
			File(moduleRoot, buildFileName).writeText(submodule.detektConfig ?: "")
			submodule.srcDirs.forEach { moduleSourceDir ->
				(1..submodule.numberOfSourceFiles).forEach {
					writeKtFile(File(moduleRoot, moduleSourceDir), "My${submodule.name}${it}Class.kt")
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

	private fun writeKtFile(dir: File, className: String) {
		dir.mkdirs()
		File(dir, className).writeText(ktFileContent(className))
	}


	fun runTasksAndCheckResult(vararg tasks: String, doAssert: DslGradleRunner.(BuildResult) -> Unit) {

		// Using a custom "project-cache-dir" to avoid a Gradle error on Windows
		val cacheArgs = listOf("--project-cache-dir", cacheDir.absolutePath, "--build-cache")

		val args = listOf("--stacktrace", "--info") + cacheArgs + tasks
		val result = GradleRunner.create()
				.withProjectDir(rootDir)
				.withPluginClasspath()
				.withArguments(args)
				.build()
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
