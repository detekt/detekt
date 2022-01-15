// Remove the suppression when https://github.com/pinterest/ktlint/issues/1125 is fixed
@file:Suppress("SpacingBetweenDeclarationsWithAnnotations")

package io.gitlab.arturbosch.detekt.rules

import io.github.detekt.test.utils.KotlinCoreEnvironmentWrapper
import io.github.detekt.test.utils.createEnvironment
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import org.spekframework.spek2.dsl.Root
import org.spekframework.spek2.lifecycle.CachingMode
import java.nio.file.Path

fun Root.setupKotlinEnvironment(additionalJavaSourceRootPath: Path? = null) {
    val wrapper by memoized(
        CachingMode.SCOPE,
        { createEnvironment(additionalJavaSourceRootPaths = listOfNotNull(additionalJavaSourceRootPath?.toFile())) },
        { it.dispose() }
    )

    // name is used for delegation
    @Suppress("UNUSED_VARIABLE")
    val env: KotlinCoreEnvironment by memoized(CachingMode.EACH_GROUP) { wrapper.env }
}

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@ExtendWith(KotlinEnvironmentResolver::class)
annotation class KotlinCoreEnvironmentTest

internal class KotlinEnvironmentResolver : ParameterResolver {
    private var ExtensionContext.wrapper: CloseableWrapper?
        get() = getStore(NAMESPACE)[WRAPPER_KEY, CloseableWrapper::class.java]
        set(value) = getStore(NAMESPACE).put(WRAPPER_KEY, value)

    override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean {
        return parameterContext.parameter.type == KotlinCoreEnvironment::class.java
    }

    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any {
        val closeableWrapper = extensionContext.wrapper
            ?: CloseableWrapper(createEnvironment()).also { extensionContext.wrapper = it }
        return closeableWrapper.wrapper.env
    }

    companion object {
        private val NAMESPACE = ExtensionContext.Namespace.create("KotlinCoreEnvironment")
        private const val WRAPPER_KEY = "wrapper"
    }

    private class CloseableWrapper(val wrapper: KotlinCoreEnvironmentWrapper) :
        ExtensionContext.Store.CloseableResource {
        override fun close() {
            wrapper.dispose()
        }
    }
}
