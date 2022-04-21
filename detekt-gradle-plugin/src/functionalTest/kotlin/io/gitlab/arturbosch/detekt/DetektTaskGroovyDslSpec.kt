package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.testkit.DslTestBuilder
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test

class DetektTaskGroovyDslSpec {

    @Test
    fun `detekt extension can be configured without errors`() {
        val config = """
            |detekt {
            |    toolVersion = "1.0.0.RC8"
            |    ignoreFailures = true
            |    source = files("src/main/kotlin")
            |    baseline = file("path/to/baseline.xml")
            |    basePath = projectDir
            |    config = files("config/detekt/detekt.yml")
            |    debug = true
            |    parallel = true
            |    allRules = true
            |    buildUponDefaultConfig = true
            |    disableDefaultRuleSets = true
            |    autoCorrect = true
            |    ignoredVariants = ["variantA", "variantB"]
            |    ignoredBuildTypes = ["buildTypeA", "buildTypeB"]
            |    ignoredFlavors = ["flavorA", "flavorB"]
            |}
        """
        val groovyBuilder = DslTestBuilder.groovy()
        val gradleRunner = groovyBuilder.withDetektConfig(config).build()
        val result = gradleRunner.runTasks(":help")
        assertThat(result.task(":help")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
    }

    @Test
    fun `detekt task can be fully configured without errors`() {
        val config = """
            |tasks.create("customDetektTask", io.gitlab.arturbosch.detekt.Detekt) {
            |    source = files("${"$"}projectDir")
            |    includes = ["**/*.kt", "**/*.kts"]
            |    excludes = ["build/"]
            |    ignoreFailures = false
            |    detektClasspath.setFrom(files("config.yml"))
            |    pluginClasspath.setFrom(files("config.yml"))
            |    baseline = file("config.yml")
            |    config.setFrom(files("config.yml"))
            |    classpath.setFrom(files("config.yml"))
            |    languageVersion = "1.6"
            |    jvmTarget = "1.8"
            |    debug = true
            |    parallel = true
            |    disableDefaultRuleSets = true
            |    buildUponDefaultConfig = true
            |    allRules = false
            |    autoCorrect = false
            |    basePath = projectDir
            |    reports {
            |        xml {
            |            enabled = true
            |            destination = file("build/reports/mydetekt.xml")
            |        }
            |        html.enabled = true
            |        html.destination = file("build/reports/mydetekt.html")
            |        txt.enabled = true
            |        txt.destination = file("build/reports/mydetekt.txt")
            |        sarif {
            |            enabled = true
            |            destination = file("build/reports/mydetekt.sarif")
            |        }
            |    }
            |    reportsDir = file("config.yml")
            |}
        """
        val groovyBuilder = DslTestBuilder.groovy()
        val gradleRunner = groovyBuilder.withDetektConfig(config).build()
        val result = gradleRunner.runTasks(":help")
        assertThat(result.task(":help")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
    }

    @Test
    fun `detekt create baseline task can be configured without errors`() {
        val config = """
            |tasks.create("customDetektCreateBaselineTask", io.gitlab.arturbosch.detekt.DetektCreateBaselineTask) {
            |    source = files("${"$"}projectDir")
            |    includes = ["**/*.kt", "**/*.kts"]
            |    excludes = ["build/"]
            |    ignoreFailures = false
            |    detektClasspath.setFrom(files("config.yml"))
            |    pluginClasspath.setFrom(files("config.yml"))
            |    baseline = file("config.yml")
            |    config.setFrom(files("config.yml"))
            |    classpath.setFrom(files("config.yml"))
            |    jvmTarget = "1.8"
            |    debug = true
            |    parallel = true
            |    disableDefaultRuleSets = true
            |    buildUponDefaultConfig = true
            |    allRules = false
            |    autoCorrect = false
            |    basePath = projectDir
            |}
        """
        val groovyBuilder = DslTestBuilder.groovy()
        val gradleRunner = groovyBuilder.withDetektConfig(config).build()
        val result = gradleRunner.runTasks(":help")
        assertThat(result.task(":help")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
    }

    @Test
    fun `detekt generate config task can be configured without errors`() {
        val config = """
            |tasks.create("customDetektGenerateConfigTask", io.gitlab.arturbosch.detekt.DetektGenerateConfigTask) {
            |    detektClasspath.setFrom(files("config.yml"))
            |    config.setFrom(files("config.yml"))
            |}
        """
        val groovyBuilder = DslTestBuilder.groovy()
        val gradleRunner = groovyBuilder.withDetektConfig(config).build()
        val result = gradleRunner.runTasks(":help")
        assertThat(result.task(":help")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
    }
}
