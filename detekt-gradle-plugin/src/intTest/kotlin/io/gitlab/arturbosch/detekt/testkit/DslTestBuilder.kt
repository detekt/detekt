package io.gitlab.arturbosch.detekt.testkit

abstract class DslTestBuilder {

    abstract val gradleBuildConfig: String
    abstract val gradleBuildName: String
    abstract val gradlePluginsSection: String
    val gradleRepositoriesSection = """
    |repositories {
    |   mavenLocal()
    |   mavenCentral()
    |   jcenter()
    |}
    |"""
    abstract val gradleApplyPlugins: String

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
            baselineFile,
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
    override val gradlePluginsSection = """
        |plugins {
        |   id 'java-library'
        |   id "io.gitlab.arturbosch.detekt"
        |}
        |"""
    override val gradleApplyPlugins = """
        |apply plugin: "io.gitlab.arturbosch.detekt"
        |"""

    override val gradleBuildConfig: String = """
        |$gradlePluginsSection
        |
        |$gradleRepositoriesSection
        |
        |dependencies {
        |   implementation "org.jetbrains.kotlin:kotlin-stdlib"
        |}
        """.trimMargin()
}

private class KotlinBuilder : DslTestBuilder() {
    override val gradleBuildName: String = "build.gradle.kts"
    override val gradlePluginsSection = """
        |plugins {
        |   `java-library`
        |   id("io.gitlab.arturbosch.detekt")
        |}
        |"""
    override val gradleApplyPlugins = """
        |plugins.apply("io.gitlab.arturbosch.detekt")
        |"""

    override val gradleBuildConfig: String = """
        |$gradlePluginsSection
        |
        |$gradleRepositoriesSection
        |
        |dependencies {
        |   implementation(kotlin("stdlib-jdk8"))
        |}
        """.trimMargin()
}
