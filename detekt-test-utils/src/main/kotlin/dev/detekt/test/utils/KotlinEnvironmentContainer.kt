package dev.detekt.test.utils

import kotlinx.coroutines.CoroutineScope
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.config.addJavaSourceRoots
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoot
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoots
import org.jetbrains.kotlin.cli.jvm.config.configureJdkClasspathRoots
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import java.io.File
import kotlin.reflect.KClass

class KotlinEnvironmentContainer(val configuration: CompilerConfiguration)

/**
 * Create a {@link KotlinEnvironmentContainer} used for test.
 *
 * @param additionalRootPaths the optional JVM classpath roots list.
 * @param additionalJavaSourceRootPaths the optional Java source roots list.
 * @param additionalLibraryTypes the optional list of types from which to load the associated library artifact JAR.
 */
fun createEnvironment(
    additionalRootPaths: List<File> = emptyList(),
    additionalJavaSourceRootPaths: List<File> = emptyList(),
    additionalLibraryTypes: List<KClass<*>> = emptyList(),
): KotlinEnvironmentContainer {
    val configuration = CompilerConfiguration()
    configuration.put(CommonConfigurationKeys.MODULE_NAME, "test_module")
    configuration.put(CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)

    // Get the runtime locations of both the stdlib and kotlinx coroutines core jars and pass
    // to the compiler so it's available to generate the BindingContext for rules under test.
    configuration.apply {
        addJvmClasspathRoot(kotlinStdLibPath())
        addJvmClasspathRoot(kotlinxCoroutinesCorePath())
        addJvmClasspathRoots(additionalRootPaths)
        addJvmClasspathRoots(additionalLibraryTypes.map { it.toJar() })
        addJavaSourceRoots(additionalJavaSourceRootPaths)
        put(JVMConfigurationKeys.JDK_HOME, File(System.getProperty("java.home")))
        configureJdkClasspathRoots()
    }

    return KotlinEnvironmentContainer(configuration)
}

private fun kotlinStdLibPath(): File = CharRange::class.toJar()

private fun kotlinxCoroutinesCorePath(): File = CoroutineScope::class.toJar()

private fun KClass<*>.toJar() = File(this.java.protectionDomain.codeSource.location.path)
