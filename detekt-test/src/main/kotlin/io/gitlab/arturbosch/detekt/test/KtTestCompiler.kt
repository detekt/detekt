package io.gitlab.arturbosch.detekt.test

import io.gitlab.arturbosch.detekt.core.KtCompiler
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.compiler.NoScopeRecordCliBindingTrace
import org.jetbrains.kotlin.cli.jvm.compiler.TopDownAnalyzerFacadeForJVM
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoot
import org.jetbrains.kotlin.com.intellij.openapi.Disposable
import org.jetbrains.kotlin.com.intellij.openapi.util.text.StringUtilRt
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.lazy.declarations.FileBasedDeclarationProviderFactory
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Test compiler extends kt compiler and adds ability to compile from text content.
 *
 * @author Artur Bosch
 */
object KtTestCompiler : KtCompiler() {

    private val root = Paths.get(resource("/"))

    fun compile(path: Path) = compile(root, path)

    fun compileFromContent(content: String): KtFile {
        val psiFile = psiFileFactory.createFileFromText(
                TEST_FILENAME,
                KotlinLanguage.INSTANCE,
                StringUtilRt.convertLineSeparators(content))
        return psiFile as? KtFile ?: throw IllegalStateException("kotlin file expected")
    }

    fun getContextForPaths(environment: KotlinCoreEnvironment, paths: List<KtFile>) =
        TopDownAnalyzerFacadeForJVM.analyzeFilesWithJavaIntegration(
            environment.project, paths, NoScopeRecordCliBindingTrace(),
            environment.configuration, environment::createPackagePartProvider, ::FileBasedDeclarationProviderFactory
        ).bindingContext

    fun createEnvironment(): KotlinCoreEnvironment {
        val configuration = CompilerConfiguration()
        configuration.put(CommonConfigurationKeys.MODULE_NAME, "test_module")
        configuration.put(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)

        // Get the runtime location of stdlib jar and pass to the compiler so it's available to generate the
        // BindingContext for rules under test.
        val path = File(CharRange::class.java.protectionDomain.codeSource.location.path)
        configuration.addJvmClasspathRoot(path)

        return KotlinCoreEnvironment.createForTests(
            TestDisposable(),
            configuration,
            EnvironmentConfigFiles.JVM_CONFIG_FILES
        )
    }

    class TestDisposable : Disposable {
        override fun dispose() { } // Don't want to dispose the test KotlinCoreEnvironment
    }
}

const val TEST_FILENAME = "Test.kt"
