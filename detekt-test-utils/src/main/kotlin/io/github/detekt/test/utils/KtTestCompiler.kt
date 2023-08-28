package io.github.detekt.test.utils

import io.github.detekt.parser.KtCompiler
import kotlinx.coroutines.CoroutineScope
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.config.addJavaSourceRoots
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoot
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoots
import org.jetbrains.kotlin.cli.jvm.config.configureJdkClasspathRoots
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.openapi.util.text.StringUtilRt
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import org.jetbrains.kotlin.psi.KtFile
import java.io.File
import java.nio.file.Path

/**
 * Test compiler extends kt compiler and adds ability to compile from text content.
 */
internal object KtTestCompiler : KtCompiler() {

    /*
     * If tests are executed through Bazel, there is no File based resource path as all classpath elements
     * are JAR files, which leads to crashes. By initializing the root on demand, it's at least possible to
     * use String based input from Bazel.
     */
    private val root by lazy { resourceAsPath("/") }

    fun compile(path: Path) = compile(root, path)

    fun compileFromContent(@Language("kotlin") content: String, filename: String = TEST_FILENAME): KtFile {
        require('/' !in filename && '\\' !in filename) {
            "filename must be a file name only and not contain any path elements"
        }
        return psiFileFactory.createPhysicalFile(
            filename,
            StringUtilRt.convertLineSeparators(content)
        )
    }

    /**
     * Not sure why but this function only works from this context.
     * Somehow the Kotlin language was not yet initialized.
     */
    fun createEnvironment(
        additionalRootPaths: List<File> = emptyList(),
        additionalJavaSourceRootPaths: List<File> = emptyList()
    ): KotlinCoreEnvironmentWrapper {
        val configuration = CompilerConfiguration()
        configuration.put(CommonConfigurationKeys.MODULE_NAME, "test_module")
        configuration.put(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)

        if (System.getenv("JAVA_HOME") != null) {
            configuration.put(JVMConfigurationKeys.JDK_HOME, File(System.getenv("JAVA_HOME")))
        }

        // Get the runtime locations of both the stdlib and kotlinx coroutines core jars and pass
        // to the compiler so it's available to generate the BindingContext for rules under test.
        configuration.apply {
            addJvmClasspathRoot(kotlinStdLibPath())
            addJvmClasspathRoot(kotlinxCoroutinesCorePath())
            addJvmClasspathRoots(additionalRootPaths)
            addJavaSourceRoots(additionalJavaSourceRootPaths)
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

    fun project(): Project = environment.project

    private fun kotlinStdLibPath(): File {
        return File(CharRange::class.java.protectionDomain.codeSource.location.path)
    }

    private fun kotlinxCoroutinesCorePath(): File {
        return File(CoroutineScope::class.java.protectionDomain.codeSource.location.path)
    }
}

internal const val TEST_FILENAME = "Test.kt"
