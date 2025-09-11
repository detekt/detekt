package dev.detekt.gradle

import dev.detekt.gradle.testkit.DslTestBuilder
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test

class DetektTaskGroovyDslSpec {

    @Test
    fun `detekt extension can be configured without errors`() {
        val config = """
            detekt {
                toolVersion = "1.0.0.RC8"
                ignoreFailures = true
                failOnSeverity = dev.detekt.gradle.extensions.FailOnSeverity.Info
                source.setFrom(file("src/main/kotlin"))
                baseline = file("path/to/baseline.xml")
                basePath = projectDir
                config.setFrom("config/detekt/detekt.yml")
                debug = true
                parallel = true
                allRules = true
                buildUponDefaultConfig = true
                disableDefaultRuleSets = true
                autoCorrect = true
                ignoredVariants = ["variantA", "variantB"]
                ignoredBuildTypes = ["buildTypeA", "buildTypeB"]
                ignoredFlavors = ["flavorA", "flavorB"]
            }
        """.trimIndent()
        val groovyBuilder = DslTestBuilder.groovy()
        val gradleRunner = groovyBuilder.withDetektConfig(config).build()
        val result = gradleRunner.runTasks(":help")
        assertThat(result.task(":help")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
    }

    @Test
    fun `detekt task can be fully configured without errors`() {
        val config = """
            tasks.create("customDetektTask", dev.detekt.gradle.Detekt) {
                source = files("${"$"}projectDir")
                includes = ["**/*.kt", "**/*.kts"]
                excludes = ["build/"]
                ignoreFailures = false
                failOnSeverity = dev.detekt.gradle.extensions.FailOnSeverity.Info
                detektClasspath.setFrom(files("config.yml"))
                pluginClasspath.setFrom(files("config.yml"))
                baseline = file("config.yml")
                config.setFrom(files("config.yml"))
                classpath.setFrom(files("config.yml"))
                apiVersion = "1.5"
                languageVersion = "1.6"
                jvmTarget = "1.8"
                jdkHome = file("path/to/jdk")
                debug = true
                parallel = true
                disableDefaultRuleSets = true
                buildUponDefaultConfig = true
                allRules = false
                autoCorrect = false
                basePath = projectDir.absolutePath
                reports {
                    checkstyle {
                        required.set(true)
                        outputLocation.set(file("build/reports/mydetekt.xml"))
                    }
                    html.required.set(true)
                    html.outputLocation.set(file("build/reports/mydetekt.html"))
                    sarif {
                        required.set(true)
                        outputLocation.set(file("build/reports/mydetekt.sarif"))
                    }
                }
            }
        """.trimIndent()
        val groovyBuilder = DslTestBuilder.groovy()
        val gradleRunner = groovyBuilder.withDetektConfig(config).build()
        val result = gradleRunner.runTasks(":help")
        assertThat(result.task(":help")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
    }

    @Test
    fun `detekt create baseline task can be configured without errors`() {
        val config = """
            tasks.create("customDetektCreateBaselineTask", dev.detekt.gradle.DetektCreateBaselineTask) {
                source = files("${"$"}projectDir")
                includes = ["**/*.kt", "**/*.kts"]
                excludes = ["build/"]
                ignoreFailures = false
                detektClasspath.setFrom(files("config.yml"))
                pluginClasspath.setFrom(files("config.yml"))
                baseline = file("config.yml")
                config.setFrom(files("config.yml"))
                classpath.setFrom(files("config.yml"))
                jvmTarget = "1.8"
                jdkHome = file("path/to/jdk")
                debug = true
                parallel = true
                disableDefaultRuleSets = true
                buildUponDefaultConfig = true
                allRules = false
                autoCorrect = false
                basePath = projectDir.absolutePath
            }
        """.trimIndent()
        val groovyBuilder = DslTestBuilder.groovy()
        val gradleRunner = groovyBuilder.withDetektConfig(config).build()
        val result = gradleRunner.runTasks(":help")
        assertThat(result.task(":help")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
    }

    @Test
    fun `detekt generate config task can be configured without errors`() {
        val config = """
            tasks.create("customDetektGenerateConfigTask", dev.detekt.gradle.DetektGenerateConfigTask) {
                detektClasspath.setFrom(files("config.yml"))
                configFile.set(file("config.yml"))
            }
        """.trimIndent()
        val groovyBuilder = DslTestBuilder.groovy()
        val gradleRunner = groovyBuilder.withDetektConfig(config).build()
        val result = gradleRunner.runTasks(":help")
        assertThat(result.task(":help")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
    }
}
