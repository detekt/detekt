package io.github.detekt.test.utils

import io.github.detekt.parser.KtCompiler
import kotlinx.coroutines.CoroutineScope
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.config.addJavaSourceRoots
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoot
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoots
import org.jetbrains.kotlin.cli.jvm.config.configureJdkClasspathRoots
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.text.StringUtilRt
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory
import java.io.File
import java.nio.file.Path
import kotlin.io.path.name

/**
 * Test compiler extends kt compiler and adds ability to compile from text content.
 */
internal object KtTestCompiler : KtCompiler() {

    private val psiFileFactory = KtPsiFactory(environment.project, markGenerated = false)

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
        val kotlinCoreEnvironment =
            KotlinCoreEnvironment.createForTests(
                parentDisposable,
                configuration,
                EnvironmentConfigFiles.JVM_CONFIG_FILES
            )
        return KotlinCoreEnvironmentWrapper(kotlinCoreEnvironment, parentDisposable)
    }

    fun createKtFile(@Language("kotlin") content: String, path: Path): KtFile =
        psiFileFactory.createPhysicalFile(path.name, StringUtilRt.convertLineSeparators(content))

    private fun kotlinStdLibPath(): File = File(CharRange::class.java.protectionDomain.codeSource.location.path)

    private fun kotlinxCoroutinesCorePath(): File =
        File(CoroutineScope::class.java.protectionDomain.codeSource.location.path)
}
