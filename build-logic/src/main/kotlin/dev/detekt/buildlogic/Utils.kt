package dev.detekt.buildlogic

import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import org.gradle.language.base.plugins.LifecycleBasePlugin

private val operativeSystem: String = when {
    Os.isFamily(Os.FAMILY_WINDOWS) -> Os.FAMILY_WINDOWS
    Os.isFamily(Os.FAMILY_MAC) -> Os.FAMILY_MAC
    Os.isFamily(Os.FAMILY_UNIX) -> Os.FAMILY_UNIX
    else -> System.getProperty("os.name").lowercase()
}

fun Task.osDependent() {
    inputs.property("os.name", operativeSystem)
}

fun Project.testInMultipleJvms(testName: String, @Suppress("MagicNumber") jvms: List<Int> = listOf(17, 21, 25)) {
    if (jvms.isEmpty()) return

    val testSourceSet = extensions.getByType<SourceSetContainer>().getByName(testName)
    val isCiBuild = providers.environmentVariable("CI")

    jvms.dropLast(1).forEach { jvm ->
        val testTask = tasks.register<Test>("${testName}Jvm$jvm") {
            description = "Runs the test suite (JVM $jvm)."
            group = LifecycleBasePlugin.VERIFICATION_GROUP

            testClassesDirs = testSourceSet.output.classesDirs
            classpath = testSourceSet.runtimeClasspath

            useJUnitPlatform()

            configureJavaLauncher(jvm)
        }

        if (isCiBuild.isPresent) {
            tasks.named("check") { dependsOn(testTask) }
        }
    }

    val jvm = jvms.last()

    val defaultTestTask = tasks.named(testName, Test::class.java) {
        configureJavaLauncher(jvm)
    }
    tasks.register("${testName}Jvm$jvm") {
        description = "Runs the test suite (JVM $jvm) — alias of $testName."
        group = LifecycleBasePlugin.VERIFICATION_GROUP

        dependsOn(defaultTestTask)
    }
}

private fun Test.configureJavaLauncher(jvmVersion: Int) {
    javaLauncher.set(
        project.extensions.getByType<JavaToolchainService>()
            .launcherFor { languageVersion.set(JavaLanguageVersion.of(jvmVersion)) }
    )
}
