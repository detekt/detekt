package io.github.detekt.test.utils

import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.analysis.api.standalone.StandaloneAnalysisAPISession
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.junit.jupiter.api.extension.ExtensionContext
import java.io.File

class KotlinEnvironmentContainer(val project: Project, val configuration: CompilerConfiguration)

/**
 * Make sure to always call [close] or use a [use] block when working with [StandaloneAnalysisAPISession]s.
 */
class KotlinCoreEnvironmentWrapper(
    private val project: Project,
    private val configuration: CompilerConfiguration,
    private val disposable: Disposable,
    val env: KotlinEnvironmentContainer = KotlinEnvironmentContainer(project, configuration),
) :
    @Suppress("DEPRECATION")
    ExtensionContext.Store.CloseableResource,
    AutoCloseable {
    override fun close() {
        Disposer.dispose(disposable)
    }
}

/**
 * Create a {@link KotlinCoreEnvironmentWrapper} used for test.
 *
 * @param additionalRootPaths the optional JVM classpath roots list.
 */
fun createEnvironment(
    additionalRootPaths: List<File> = emptyList(),
    additionalJavaSourceRootPaths: List<File> = emptyList(),
): KotlinCoreEnvironmentWrapper = KtTestCompiler.createEnvironment(additionalRootPaths, additionalJavaSourceRootPaths)
