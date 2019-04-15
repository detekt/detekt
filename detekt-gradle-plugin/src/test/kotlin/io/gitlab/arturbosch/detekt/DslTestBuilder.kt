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
    private var gradleVersion: String? = null

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

    fun withGradleVersion(version: String): DslTestBuilder {
        gradleVersion = version
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
            baselineFile,
            gradleVersion
        )
        runner.setupProject()
        return runner
    }

    private class GroovyBuilder : DslTestBuilder() {
        override val gradleBuildName: String = "build.gradle"
        override val gradleBuildConfig: String = """
				|import io.gitlab.arturbosch.detekt.DetektPlugin
				|
				|plugins {
				|   id "java-library"
				|   id "io.gitlab.arturbosch.detekt"
				|}
				|
				|repositories {
				|	jcenter()
				|	mavenLocal()
				|}
				""".trimMargin()
    }

    private class KotlinBuilder : DslTestBuilder() {
        override val gradleBuildName: String = "build.gradle.kts"
        override val gradleBuildConfig: String = """
				|import io.gitlab.arturbosch.detekt.detekt
				|
				|plugins {
				|   `java-library`
				|	id("io.gitlab.arturbosch.detekt")
				|}
				|
				|repositories {
				|	jcenter()
				|	mavenLocal()
				|}
				""".trimMargin()
    }

    companion object {
        fun kotlin(): DslTestBuilder = KotlinBuilder()
        fun groovy(): DslTestBuilder = GroovyBuilder()
    }
}
