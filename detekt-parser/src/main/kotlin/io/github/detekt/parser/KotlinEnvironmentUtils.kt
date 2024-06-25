package io.github.detekt.parser

import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.arguments.parseCommandLineArguments
import org.jetbrains.kotlin.cli.common.arguments.validateArguments
import org.jetbrains.kotlin.cli.common.config.addKotlinSourceRoots
import org.jetbrains.kotlin.cli.common.messages.MessageRenderer
import org.jetbrains.kotlin.cli.common.messages.PrintingMessageCollector
import org.jetbrains.kotlin.cli.common.setupLanguageVersionSettings
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.config.addJavaSourceRoots
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoots
import org.jetbrains.kotlin.cli.jvm.config.configureJdkClasspathRoots
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.com.intellij.openapi.Disposable
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.pom.PomModel
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import java.io.File
import java.io.PrintStream
import java.nio.file.Path

/**
 * Creates an environment instance which can be used to compile source code to KtFile's.
 * This environment also allows to modify the resulting AST files.
 */
fun createKotlinCoreEnvironment(
    configuration: CompilerConfiguration = CompilerConfiguration(),
    disposable: Disposable = Disposer.newDisposable(),
    printStream: PrintStream,
): KotlinCoreEnvironment {
    configuration.put(
        CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY,
        PrintingMessageCollector(printStream, MessageRenderer.PLAIN_FULL_PATHS, false)
    )
    configuration.put(CommonConfigurationKeys.MODULE_NAME, "detekt")

    val environment = KotlinCoreEnvironment.createForProduction(
        disposable,
        configuration,
        EnvironmentConfigFiles.JVM_CONFIG_FILES
    )

    val projectCandidate = environment.project

    val project = requireNotNull(projectCandidate as? MockProject) {
        "MockProject type expected, actual - ${projectCandidate.javaClass.simpleName}"
    }

    project.registerService(PomModel::class.java, DetektPomModel)

    return environment
}

/**
 * Creates a compiler configuration for the kotlin compiler with all known sources and classpath jars.
 * Be aware that if any path of [pathsToAnalyze] is a directory it is scanned for java and kotlin files.
 */
@Suppress("LongParameterList")
fun createCompilerConfiguration(
    pathsToAnalyze: List<Path>,
    classpath: List<String>,
    apiVersion: String?,
    languageVersion: String?,
    jvmTarget: String,
    jdkHome: Path?,
    printStream: PrintStream,
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

    val classpathFiles = classpath.map { File(it) }

    val jvmCompilerArguments = K2JVMCompilerArguments()

    val args = buildList {
        if (apiVersion != null) {
            add("-api-version")
            add(apiVersion)
        }
        if (languageVersion != null) {
            add("-language-version")
            add(languageVersion)
        }
        add("-jvm-target")
        add(jvmTarget)
    }

    parseCommandLineArguments(args, jvmCompilerArguments)

    validateArguments(jvmCompilerArguments.errors)?.let { throw IllegalStateException(it) }

    return CompilerConfiguration().apply {
        addJavaSourceRoots(javaFiles)
        addKotlinSourceRoots(kotlinFiles)
        addJvmClasspathRoots(classpathFiles)
        put(
            CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY,
            PrintingMessageCollector(printStream, MessageRenderer.PLAIN_FULL_PATHS, false)
        )
        setupLanguageVersionSettings(jvmCompilerArguments)

        if (jdkHome != null) {
            put(JVMConfigurationKeys.JDK_HOME, jdkHome.toFile())
        } else {
            put(JVMConfigurationKeys.JDK_HOME, File(System.getProperty("java.home")))
        }

        configureJdkClasspathRoots()
    }
}
