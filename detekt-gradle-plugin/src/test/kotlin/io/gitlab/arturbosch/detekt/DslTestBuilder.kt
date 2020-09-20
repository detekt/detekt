package io.gitlab.arturbosch.detekt

abstract class DslTestBuilder {

    abstract val gradleBuildConfig: String
    abstract val gradleBuildName: String
    abstract val gradlePluginsSection: String
    val gradleRepositoriesSection = REPOSITORIES_SECTION
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

    private class GroovyBuilder : DslTestBuilder() {
        override val gradleBuildName: String = "build.gradle"
        override val gradlePluginsSection = GROOVY_PLUGINS_SECTION
        override val gradleApplyPlugins = GROOVY_APPLY_PLUGINS
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
        override val gradlePluginsSection = KOTLIN_PLUGINS_SECTION
        override val gradleApplyPlugins = KOTLIN_APPLY_PLUGINS
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

    companion object {
        private const val GROOVY_PLUGINS_SECTION = """
            |plugins {
            |   id 'java-library'
            |   id "io.gitlab.arturbosch.detekt"
            |}
            |"""

        private const val KOTLIN_PLUGINS_SECTION = """
            |plugins {
            |   `java-library`
            |   id("io.gitlab.arturbosch.detekt")
            |}
            |"""

        private const val REPOSITORIES_SECTION = """
            |repositories {
            |   mavenLocal()
            |   mavenCentral()
            |   jcenter()
            |}
            |"""

        private const val GROOVY_APPLY_PLUGINS = """
            |apply plugin: "io.gitlab.arturbosch.detekt"
            |"""

        private const val KOTLIN_APPLY_PLUGINS = """
            |plugins.apply("io.gitlab.arturbosch.detekt")
            |"""

        fun kotlin(): DslTestBuilder = KotlinBuilder()
        fun groovy(): DslTestBuilder = GroovyBuilder()
    }
}
