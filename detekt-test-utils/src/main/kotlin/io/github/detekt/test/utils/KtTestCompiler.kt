package io.github.detekt.test.utils

import io.github.detekt.parser.KtCompiler
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoot
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.openapi.util.text.StringUtilRt
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.psi.KtFile
import java.io.File
import java.nio.file.Path

/**
 * Test compiler extends kt compiler and adds ability to compile from text content.
 */
internal object KtTestCompiler : KtCompiler() {

    private val root = resourceAsPath("/")

    fun compile(path: Path) = compile(root, path)

    fun compileFromContent(@Language("kotlin") content: String, filename: String = TEST_FILENAME): KtFile =
        psiFileFactory.createPhysicalFile(
            filename,
            StringUtilRt.convertLineSeparators(content))

    /**
     * Not sure why but this function only works from this context.
     * Somehow the Kotlin language was not yet initialized.
     */
    fun createEnvironment(): KotlinCoreEnvironmentWrapper {
        val configuration = CompilerConfiguration()
        configuration.put(CommonConfigurationKeys.MODULE_NAME, "test_module")
        configuration.put(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)

        // Get the runtime location of stdlib jar and pass to the compiler so it's available to generate the
        // BindingContext for rules under test.
        val path = File(CharRange::class.java.protectionDomain.codeSource.location.path)
        configuration.addJvmClasspathRoot(path)

        val parentDisposable = Disposer.newDisposable()
        val kotlinCoreEnvironment =
            KotlinCoreEnvironment.createForTests(
                parentDisposable,
                configuration,
                EnvironmentConfigFiles.JVM_CONFIG_FILES
            )
        return KotlinCoreEnvironmentWrapper(kotlinCoreEnvironment, parentDisposable)
    }

    fun project(): Project = environment.project
}

internal const val TEST_FILENAME = "Test.kt"
