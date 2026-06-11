package dev.detekt.core.parser

import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.arguments.parseCommandLineArguments
import org.jetbrains.kotlin.cli.common.arguments.validateArguments
import org.jetbrains.kotlin.cli.common.config.addKotlinSourceRoots
import org.jetbrains.kotlin.cli.common.messages.MessageRenderer
import org.jetbrains.kotlin.cli.common.messages.PrintingMessageCollector
import org.jetbrains.kotlin.cli.common.setupLanguageVersionSettings
import org.jetbrains.kotlin.cli.create
import org.jetbrains.kotlin.cli.jvm.config.addJavaSourceRoots
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoots
import org.jetbrains.kotlin.cli.jvm.config.configureJdkClasspathRoots
import org.jetbrains.kotlin.cli.jvm.configureAdvancedJvmOptions
import org.jetbrains.kotlin.cli.jvm.setupJvmSpecificArguments
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import java.io.File
import java.io.PrintStream
import java.nio.file.Path

/**
 * Creates a compiler configuration for the kotlin compiler with all known sources and classpath jars.
 * Be aware that if any path of [pathsToAnalyze] is a directory it is scanned for java and kotlin files.
 */
@Suppress("LongParameterList")
fun createCompilerConfiguration(
    pathsToAnalyze: List<Path>,
    classpath: List<Path>,
    apiVersion: String?,
    languageVersion: String?,
    jvmTarget: String,
    jdkHome: Path?,
    freeCompilerArgs: List<String>,
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

    val classpathFiles = classpath.map(Path::toFile)

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
        addAll(freeCompilerArgs)
    }

    parseCommandLineArguments(args, jvmCompilerArguments)

    validateArguments(jvmCompilerArguments.errors)?.let { throw IllegalStateException(it) }

    return CompilerConfiguration.create().apply {
        addJavaSourceRoots(javaFiles)
        addKotlinSourceRoots(kotlinFiles)
        addJvmClasspathRoots(classpathFiles)
        put(
            CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY,
            PrintingMessageCollector(printStream, MessageRenderer.PLAIN_FULL_PATHS, false)
        )
        setupLanguageVersionSettings(jvmCompilerArguments)
        setupJvmSpecificArguments(jvmCompilerArguments)
        configureAdvancedJvmOptions(jvmCompilerArguments)

        if (jdkHome != null) {
            put(JVMConfigurationKeys.JDK_HOME, jdkHome.toFile())
        } else {
            put(JVMConfigurationKeys.JDK_HOME, File(System.getProperty("java.home")))
        }

        configureJdkClasspathRoots()
    }
}

// https://github.com/JetBrains/kotlin/blob/v2.4.0/compiler/cli/src/org/jetbrains/kotlin/cli/common/CLICompiler.kt#L207-L300
protected fun loadPlugins(
    paths: KotlinPaths?,
    arguments: A,
    configuration: CompilerConfiguration,
    parentDisposable: Disposable,
): ExitCode {
    val pluginClasspaths = arguments.pluginClasspaths.orEmpty().toMutableList()
    val pluginOptions = arguments.pluginOptions.orEmpty().toMutableList()
    val pluginConfigurations = arguments.pluginConfigurations?.asList().orEmpty()
    val pluginOrderConstraints = arguments.pluginOrderConstraints?.asList().orEmpty()

    val useK2 = configuration.get(CommonConfigurationKeys.USE_FIR) == true

    if (!checkPluginsArguments(configuration, useK2, pluginClasspaths, pluginOptions, pluginConfigurations)) {
        return INTERNAL_ERROR
    }

    val scriptingPluginClasspath = mutableListOf<String>()
    val scriptingPluginOptions = mutableListOf<String>()

    if (!arguments.disableDefaultScriptingPlugin) {
        scriptingPluginOptions.addPlatformOptions(arguments)
        val explicitScriptingPlugin =
            extractPluginClasspathAndOptions(pluginConfigurations).any { (_, classpath, _) ->
                classpath.any { File(it).name.startsWith(PathUtil.KOTLIN_SCRIPTING_COMPILER_PLUGIN_NAME) }
            } || pluginClasspaths.any { File(it).name.startsWith(PathUtil.KOTLIN_SCRIPTING_COMPILER_PLUGIN_NAME) }
        val explicitOrLoadedScriptingPlugin = explicitScriptingPlugin ||
            tryLoadScriptingPluginFromCurrentClassLoader(configuration, pluginOptions, useK2)
        if (!explicitOrLoadedScriptingPlugin) {
            val kotlinPaths = paths ?: PathUtil.kotlinPathsForCompiler
            val libPath = kotlinPaths.libPath.takeIf { it.exists() && it.isDirectory } ?: File(".")
            val (jars, missingJars) =
                PathUtil.KOTLIN_SCRIPTING_PLUGIN_CLASSPATH_JARS.map { File(libPath, it) }.partition { it.exists() }
            if (missingJars.isEmpty()) {
                scriptingPluginClasspath.addAll(0, jars.map { it.canonicalPath })
            } else {
                configuration.messageCollector.report(
                    LOGGING,
                    "Scripting plugin will not be loaded: not all required jars are present in the classpath (missing files: $missingJars)"
                )
            }
        }
    } else {
        scriptingPluginOptions.add("plugin:kotlin.scripting:disable=true")
    }

    pluginClasspaths.addAll(scriptingPluginClasspath)
    pluginOptions.addAll(scriptingPluginOptions)

    return PluginCliParser.loadPluginsSafe(
        pluginClasspaths,
        pluginOptions,
        pluginConfigurations,
        pluginOrderConstraints,
        configuration,
        parentDisposable
    )
}

private fun tryLoadScriptingPluginFromCurrentClassLoader(
    configuration: CompilerConfiguration,
    pluginOptions: List<String>,
    useK2: Boolean
): Boolean =
    try {
        val pluginRegistrarClass = PluginCliParser::class.java.classLoader.loadClass(SCRIPT_PLUGIN_REGISTRAR_NAME)
        val pluginRegistrar = (pluginRegistrarClass.getDeclaredConstructor().newInstance() as? ComponentRegistrar)?.also {
            configuration.add(ComponentRegistrar.PLUGIN_COMPONENT_REGISTRARS, it)
        }
        val pluginK2Registrar = if (useK2) {
            val pluginK2RegistrarClass = PluginCliParser::class.java.classLoader.loadClass(SCRIPT_PLUGIN_K2_REGISTRAR_NAME)
            (pluginK2RegistrarClass.getDeclaredConstructor().newInstance() as? CompilerPluginRegistrar)?.also {
                configuration.add(CompilerPluginRegistrar.COMPILER_PLUGIN_REGISTRARS, it)
            }
        } else null
        if (pluginRegistrar != null || pluginK2Registrar != null) {
            processScriptPluginCliOptions(pluginOptions, configuration)
            true
        } else false
    } catch (e: Throwable) {
        val messageCollector = configuration.getNotNull(CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY)
        messageCollector.report(LOGGING, "Exception on loading scripting plugin: $e")
        false
    }

private fun processScriptPluginCliOptions(pluginOptions: List<String>, configuration: CompilerConfiguration) {
    val cmdlineProcessorClass =
        if (pluginOptions.isEmpty()) null
        else PluginCliParser::class.java.classLoader.loadClass(SCRIPT_PLUGIN_COMMANDLINE_PROCESSOR_NAME)!!
    val cmdlineProcessor = cmdlineProcessorClass?.getDeclaredConstructor()?.newInstance() as? CommandLineProcessor
    if (cmdlineProcessor != null) {
        processCompilerPluginsOptions(configuration, pluginOptions, listOf(cmdlineProcessor))
    }
}

// https://github.com/JetBrains/kotlin/blob/v2.4.0/compiler/cli/cli-jvm/src/org/jetbrains/kotlin/cli/jvm/K2JVMCompiler.kt#L182-L193
override fun MutableList<String>.addPlatformOptions(arguments: K2JVMCompilerArguments) {
    if (arguments.scriptTemplates?.isNotEmpty() == true) {
        add("plugin:kotlin.scripting:script-templates=${arguments.scriptTemplates!!.joinToString(",")}")
    }
    if (arguments.scriptResolverEnvironment?.isNotEmpty() == true) {
        add(
            "plugin:kotlin.scripting:script-resolver-environment=${arguments.scriptResolverEnvironment!!.joinToString(
                ","
            )}"
        )
    }
}
