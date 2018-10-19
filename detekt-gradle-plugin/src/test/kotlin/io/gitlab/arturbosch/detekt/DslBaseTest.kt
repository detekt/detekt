package io.gitlab.arturbosch.detekt

import java.io.File
import java.util.Random
import kotlin.streams.asSequence

class DslBaseTest(
		private val buildGradleFileName: String,
		private val buildGradleFile: String,
		private val createUniqueSourceFiles: Boolean = false) {

	private fun uniqueStringValue(): String =
			Random().ints(VARIABLE_NAME_LENGTH, 0, VARIABLE_NAME_SOURCE.length)
					.asSequence()
					.map(VARIABLE_NAME_SOURCE::get)
					.joinToString("")


	private fun getBuildFileContent(customConfig: String): String {
		return """
    	|$buildGradleFile
    	|
    	|tasks.all {}
		|
    	|$customConfig
		""".trimMargin()
	}

	private fun ktFileContent(className: String) = """
	|internal class ${className} {
	|
	|  val aString = "${if (createUniqueSourceFiles) uniqueStringValue() else ""}"
	|
	""".trimMargin()

	// settings.gradle
	private val settingsFileContent = """include ":custom""""

	fun writeConfig(root: File, failfast: Boolean = false) {
		File(root, "config.yml").writeText("""
		|autoCorrect: true
		|failFast: $failfast
		""".trimMargin())
	}

	fun writeBaseline(root: File) {
		File(root, "baseline.xml").writeText("""
		|<some>
		|	<xml/>
		|</some>
		""".trimMargin())
	}

	fun writeFiles(root: File, detektConfiguration: String = "", srcDir: File = File(root, "src/main/java")) {
		File(root, buildGradleFileName).writeText(getBuildFileContent(detektConfiguration))
		File(root, "settings.gradle").writeText(settingsFileContent)
		writeSourceFile(root, srcDir)
	}

	fun writeFiles(root: File, detektConfiguration: String, vararg srcDir: File) {
		File(root, buildGradleFileName).writeText(getBuildFileContent(detektConfiguration))
		File(root, "settings.gradle").writeText(settingsFileContent)
		srcDir.forEach {
			writeSourceFile(root, it)
		}
	}

	fun writeSourceFile(root: File, srcDir: File = File(root, "src/main/java"), className: String = "MyClass") {
		srcDir.mkdirs()
		File(srcDir, "$className.kt").writeText(ktFileContent(className))
	}

	companion object {
		const val VARIABLE_NAME_SOURCE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
		const val VARIABLE_NAME_LENGTH: Long = 10
	}
}

val File.safeAbsolutePath
	get() = absolutePath.replace("\\", "\\\\")
