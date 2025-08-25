package dev.detekt.test.junit

import com.intellij.openapi.Disposable
import com.intellij.openapi.util.Disposer
import dev.detekt.test.utils.KotlinEnvironmentContainer
import kotlinx.coroutines.CoroutineScope
import org.jetbrains.kotlin.analysis.api.standalone.StandaloneAnalysisAPISession
import org.jetbrains.kotlin.analysis.api.standalone.buildStandaloneAnalysisAPISession
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.CliTraceHolder
import org.jetbrains.kotlin.cli.jvm.config.addJavaSourceRoots
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoot
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoots
import org.jetbrains.kotlin.cli.jvm.config.configureJdkClasspathRoots
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import org.jetbrains.kotlin.resolve.CodeAnalyzerInitializer
import org.junit.jupiter.api.extension.ExtensionContext
import java.io.File

/**
 * Make sure to always call [close] or use a [use] block when working with [StandaloneAnalysisAPISession]s.
 */
internal class KotlinCoreEnvironmentWrapper(
    val env: KotlinEnvironmentContainer,
    private val disposable: Disposable,
) :
    @Suppress("DEPRECATION")
    ExtensionContext.Store.CloseableResource,
    AutoCloseable {
    override fun close() {
        Disposer.dispose(disposable)
    }
}

/**
 * Create a {@link KotlinEnvironmentContainer} used for test.
 *
 * @param disposable a disposable that should be called once the returned [KotlinEnvironmentContainer] is not used anymore
 * @param additionalRootPaths the optional JVM classpath roots list.
 * @param additionalRootPaths the optional Java classpath roots list.
 */
internal fun createEnvironment(
    disposable: Disposable,
    additionalRootPaths: List<File> = emptyList(),
    additionalJavaSourceRootPaths: List<File> = emptyList(),
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
        addJavaSourceRoots(additionalJavaSourceRootPaths)
        put(JVMConfigurationKeys.JDK_HOME, File(System.getProperty("java.home")))
        configureJdkClasspathRoots()
    }

    val analysisSession = buildStandaloneAnalysisAPISession(disposable) {
        @Suppress("DEPRECATION") // Required until fully transitioned to setting up Kotlin Analysis API session
        buildKtModuleProviderByCompilerConfiguration(configuration)

        // Required to set up BindingContext with TopDownAnalyzerFacadeForJVM.analyzeFilesWithJavaIntegration
        registerProjectService(CodeAnalyzerInitializer::class.java, CliTraceHolder(project))
    }

    return KotlinEnvironmentContainer(analysisSession.project, configuration)
}

private fun kotlinStdLibPath(): File = File(CharRange::class.java.protectionDomain.codeSource.location.path)

private fun kotlinxCoroutinesCorePath(): File =
    File(CoroutineScope::class.java.protectionDomain.codeSource.location.path)
