package io.gitlab.arturbosch.detekt.api.internal

import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.config.addKotlinSourceRoots
import org.jetbrains.kotlin.cli.common.messages.MessageRenderer
import org.jetbrains.kotlin.cli.common.messages.PrintingMessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.config.addJavaSourceRoots
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoots
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.pom.PomModel
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import org.jetbrains.kotlin.config.JvmTarget
import java.io.File
import java.nio.file.Path

fun createKotlinCoreEnvironment(configuration: CompilerConfiguration = CompilerConfiguration()): KotlinCoreEnvironment {
    System.setProperty("idea.io.use.fallback", "true")
    configuration.put(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY,
        PrintingMessageCollector(System.err, MessageRenderer.PLAIN_FULL_PATHS, false))
    configuration.put(CommonConfigurationKeys.MODULE_NAME, "detekt")

    val environment = KotlinCoreEnvironment.createForProduction(
        Disposer.newDisposable(),
        configuration,
        EnvironmentConfigFiles.JVM_CONFIG_FILES
    )

    val project = environment.project as? MockProject ?: error("MockProject type expected")
    project.registerService(PomModel::class.java, DetektPomModel(project))

    return environment
}

fun createCompilerConfiguration(
    pathsToAnalyze: List<Path>,
    classpath: List<String>,
    jvmTarget: JvmTarget
): CompilerConfiguration {

    val javaFiles = pathsToAnalyze.flatMap { path ->
        path.toFile().walk()
            .filter { it.isFile && it.extension.equals("java", true) }
            .toList()
    }
    val kotlinFiles = pathsToAnalyze.flatMap { path ->
        path.toFile().walk()
            .filter { it.isFile }
            .filter { it.extension.equals("kt", true) || it.extension.equals("kts", true) }
            .map { it.absolutePath }
            .toList()
    }

    return CompilerConfiguration().apply {
        put(JVMConfigurationKeys.JVM_TARGET, jvmTarget)
        addJavaSourceRoots(javaFiles)
        addKotlinSourceRoots(kotlinFiles)
        addJvmClasspathRoots(classpath.map { File(it) })
    }
}
