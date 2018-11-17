package io.gitlab.arturbosch.detekt


/**
 * @author Markus Schwarz
 */
abstract class DslTestBuilder {

	abstract val gradleBuildConfig: String
	abstract val gradleBuildName: String

	private var detektConfig: String = ""
	private var projectLayout: ProjectLayout = ProjectLayout(1)
	private var baselineFile: String? = null
	private var configFile: String? = null
	var buildGradleExtension: String? = null
	var buildGradleKtsExtension: String? = null
	var baseGradlePlugin: GradlePlugin = GradlePlugin.JavaLibrary

	fun withDetektConfig(config: String): DslTestBuilder {
		detektConfig = config
		return this
	}

	fun withProjectLayout(layout: ProjectLayout): DslTestBuilder {
		projectLayout = layout
		return this
	}

	fun withBaseline(filename: String): DslTestBuilder {
		baselineFile = filename
		return this
	}

	fun withConfigFile(filename: String): DslTestBuilder {
		configFile = filename
		return this
	}

	fun withBuildGradleExtension(buildGradle: String, buildGradleKts: String): DslTestBuilder {
		buildGradleExtension = buildGradle
		buildGradleKtsExtension = buildGradleKts
		return this
	}

	fun withBaseGradlePlugin(plugin: GradlePlugin): DslTestBuilder {
		baseGradlePlugin = plugin
		return this
	}

	fun build(): DslGradleRunner {
		val mainBuildFileContent = """
			| $gradleBuildConfig
			| $detektConfig
		""".trimMargin()
		val runner = DslGradleRunner(
				projectLayout,
				gradleBuildName,
				mainBuildFileContent,
				configFile,
				baselineFile)
		runner.setupProject()
		return runner
	}

	private class GroovyBuilder : DslTestBuilder() {
		override val gradleBuildName: String = "build.gradle"
		override val gradleBuildConfig: String = """
				|import io.gitlab.arturbosch.detekt.DetektPlugin
				|plugins {
				|   ${baseGradlePlugin.groovy}
				|   id "io.gitlab.arturbosch.detekt"
				|}
				|
				|repositories {
				|	jcenter()
				|	mavenLocal()
				|}
				|$buildGradleExtension
			""".trimMargin()
	}

	private class KotlinBuilder : DslTestBuilder() {
		override val gradleBuildName: String = "build.gradle.kts"
		override val gradleBuildConfig: String = """
				|import io.gitlab.arturbosch.detekt.detekt
				|
				|plugins {
				|   ${baseGradlePlugin.kotlin}
				|	id("io.gitlab.arturbosch.detekt")
				|}
				|
				|repositories {
				|	jcenter()
				|	mavenLocal()
				|}
				|$buildGradleKtsExtension
				""".trimMargin()
	}

	companion object {
		fun kotlin(): DslTestBuilder = KotlinBuilder()
		fun groovy(): DslTestBuilder = GroovyBuilder()
	}
}

sealed class GradlePlugin {
	abstract val groovy: String
	abstract val kotlin: String

	object JavaLibrary : GradlePlugin() {
		override val groovy = "id \"java-library\""
		override val kotlin = "`java-library`"
	}

	object Kotlin : GradlePlugin() {
		override val groovy = "id \"org.jetbrains.kotlin.jvm\" version \"1.3.0\""
		override val kotlin = "kotlin(\"jvm\") version \"1.3.0\""
	}

	object KotlinMultiPlatform : GradlePlugin() {
		override val groovy = "id \"org.jetbrains.kotlin.multiplatform\" version \"1.3.0\""
		override val kotlin = "kotlin(\"multiplatform\") version \"1.3.0\""
	}

	object Kotlin2Js : GradlePlugin() {
		override val groovy = "id \"org.jetbrains.kotlin.kotlin2js\" version \"1.3.0\""
		override val kotlin = "kotlin(\"kotlin2js\") version \"1.3.0\""
	}

	object KotlinAndroid : GradlePlugin() {
		override val groovy = "id \"org.jetbrains.kotlin.android\" version \"1.3.0\""
		override val kotlin = "kotlin(\"android\") version \"1.3.0\""
	}
}
