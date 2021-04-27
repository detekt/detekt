package io.gitlab.arturbosch.detekt.testkit

abstract class DslTestBuilder {

    abstract val gradleBuildConfig: String
    abstract val gradleBuildName: String
    abstract val gradlePlugins: String
    abstract val gradleSubprojectsApplyPlugins: String
    val gradleRepositories = """
        repositories {
            mavenLocal()
            mavenCentral()
            jcenter()
        }
    """.trimIndent()

    private var detektConfig: String = ""
    private var projectLayout: ProjectLayout = ProjectLayout(1)
    private var baselineFile: String? = null
    private var configFile: String? = null
    private var gradleVersion: String? = null
    private var dryRun: Boolean = false

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

    fun dryRun(): DslTestBuilder {
        dryRun = true
        return this
    }

    fun build(): DslGradleRunner {
        val mainBuildFileContent = """
            |$gradleBuildConfig
            |$detektConfig
            """.trimMargin()
        val runner = DslGradleRunner(
            projectLayout,
            gradleBuildName,
            mainBuildFileContent,
            configFile,
            baselineFile?.let { listOf(it) } ?: emptyList(),
            gradleVersion,
            dryRun
        )
        runner.setupProject()
        return runner
    }

    companion object {
        fun kotlin(): DslTestBuilder = KotlinBuilder()
        fun groovy(): DslTestBuilder = GroovyBuilder()
    }
}

private class GroovyBuilder : DslTestBuilder() {
    override val gradleBuildName: String = "build.gradle"
    override val gradlePlugins = """
        |plugins {
        |  id 'java-library'
        |  id "io.gitlab.arturbosch.detekt"
        |}
        |"""

    override val gradleBuildConfig: String = """
        |$gradlePlugins
        |
        |$gradleRepositories
        |
        |dependencies {
        |   implementation "org.jetbrains.kotlin:kotlin-stdlib"
        |}
        """.trimMargin()

    override val gradleSubprojectsApplyPlugins = """
        |apply plugin: "io.gitlab.arturbosch.detekt"
        |"""
}

private class KotlinBuilder : DslTestBuilder() {
    override val gradleBuildName: String = "build.gradle.kts"

    override val gradlePlugins = """
        |plugins {
        |   `java-library`
        |   id("io.gitlab.arturbosch.detekt")
        |}
        |"""

    override val gradleBuildConfig: String = """
        |$gradlePlugins
        |
        |$gradleRepositories
        |
        |dependencies {
        |   implementation(kotlin("stdlib"))
        |}
        """.trimMargin()

    override val gradleSubprojectsApplyPlugins = """
        |plugins.apply("io.gitlab.arturbosch.detekt")
        |"""
}
