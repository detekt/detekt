package io.gitlab.arturbosch.detekt

import java.io.File

class DslBaseTest(private val buildGradleFileName: String, private val buildGradleFile: String) {

	private fun getBuildFileContent(customConfig: String): String {
		return """
    	|$buildGradleFile
		|
    	|$customConfig
		""".trimMargin()
	}

	private val ktFileContent = """
	|internal class MyClass
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

	fun writeFiles(root: File, detektConfiguration: String, srcDir: File = File(root, "src/main/java")) {
		File(root, buildGradleFileName).writeText(getBuildFileContent(detektConfiguration))
		File(root, "settings.gradle").writeText(settingsFileContent)
		srcDir.mkdirs()
		File(srcDir, "MyClass.kt").writeText(ktFileContent)
	}

	fun writeFiles(root: File, detektConfiguration: String, vararg srcDir: File) {
		File(root, buildGradleFileName).writeText(getBuildFileContent(detektConfiguration))
		File(root, "settings.gradle").writeText(settingsFileContent)
		srcDir.forEach {
			it.mkdirs()
			File(it, "MyClass.kt").writeText(ktFileContent)
		}
	}
}

val File.safeAbsolutePath
	get() = absolutePath.replace("\\", "\\\\")
