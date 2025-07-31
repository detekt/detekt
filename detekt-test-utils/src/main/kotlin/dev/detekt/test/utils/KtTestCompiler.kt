package dev.detekt.test.utils

import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.text.StringUtilRt
import dev.detekt.parser.KtCompiler
import kotlinx.coroutines.CoroutineScope
import org.intellij.lang.annotations.Language
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
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.resolve.CodeAnalyzerInitializer
import java.io.File
import java.nio.file.Path
import kotlin.io.path.name

/**
 * Test compiler extends kt compiler and adds ability to compile from text content.
 */
internal object KtTestCompiler : KtCompiler() {

    private val psiFileFactory = KtPsiFactory(project, markGenerated = false)

    /**
     * Not sure why but this function only works from this context.
     * Somehow the Kotlin language was not yet initialized.
     */
    fun createEnvironment(
        additionalRootPaths: List<File> = emptyList(),
        additionalJavaSourceRootPaths: List<File> = emptyList(),
    ): KotlinCoreEnvironmentWrapper {
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

        val parentDisposable = Disposer.newDisposable()
        val analysisSession = buildStandaloneAnalysisAPISession(parentDisposable) {
            @Suppress("DEPRECATION") // Required until fully transitioned to setting up Kotlin Analysis API session
            buildKtModuleProviderByCompilerConfiguration(configuration)

            // Required to set up BindingContext with TopDownAnalyzerFacadeForJVM.analyzeFilesWithJavaIntegration
            registerProjectService(CodeAnalyzerInitializer::class.java, CliTraceHolder(project))
        }

        return KotlinCoreEnvironmentWrapper(analysisSession.project, configuration, parentDisposable)
    }

    fun createKtFile(@Language("kotlin") content: String, path: Path): KtFile =
        psiFileFactory.createPhysicalFile(path.name, StringUtilRt.convertLineSeparators(content))

    private fun kotlinStdLibPath(): File = File(CharRange::class.java.protectionDomain.codeSource.location.path)

    private fun kotlinxCoroutinesCorePath(): File =
        File(CoroutineScope::class.java.protectionDomain.codeSource.location.path)
}
