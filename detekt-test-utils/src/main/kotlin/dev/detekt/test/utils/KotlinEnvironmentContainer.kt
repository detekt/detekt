package dev.detekt.test.utils

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.config.addJavaSourceRoots
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoots
import org.jetbrains.kotlin.cli.jvm.config.configureJdkClasspathRoots
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import java.io.File
import kotlin.script.experimental.jvm.util.classpathFromClassloader

class KotlinEnvironmentContainer(val configuration: CompilerConfiguration)

/**
 * Create a {@link KotlinEnvironmentContainer} used for test.
 *
 * @param additionalJavaSourceRootPaths the optional Java source roots list.
 */
fun createEnvironment(
    additionalJavaSourceRootPaths: List<File> = emptyList(),
): KotlinEnvironmentContainer {
    val configuration = CompilerConfiguration()
    configuration.put(CommonConfigurationKeys.MODULE_NAME, "test_module")
    configuration.put(CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)

    // Get the runtime locations of both the stdlib and kotlinx coroutines core jars and pass
    // to the compiler so it's available to generate the BindingContext for rules under test.
    configuration.apply {
        val classLoader = Thread.currentThread().contextClassLoader
        val classpath = checkNotNull(classpathFromClassloader(classLoader)) { "We should always have a classpath" }
        addJvmClasspathRoots(classpath)
        addJavaSourceRoots(additionalJavaSourceRootPaths)
        put(JVMConfigurationKeys.JDK_HOME, File(System.getProperty("java.home")))
        configureJdkClasspathRoots()
    }

    return KotlinEnvironmentContainer(configuration)
}
