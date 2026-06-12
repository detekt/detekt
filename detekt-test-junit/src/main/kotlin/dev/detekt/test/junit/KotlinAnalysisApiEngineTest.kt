package dev.detekt.test.junit

import dev.detekt.test.utils.KotlinAnalysisApiEngine
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import java.lang.AutoCloseable

/**
 * TODO
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@ExtendWith(KotlinAnalysisApiEngineResolver::class)
annotation class KotlinAnalysisApiEngineTest

internal class KotlinAnalysisApiEngineResolver : ParameterResolver {
    override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean =
        parameterContext.parameter.type == KotlinAnalysisApiEngine::class.java

    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext) =
        extensionContext.getStore(NAMESPACE)
            .getOrComputeIfAbsent(
                KEY,
                { _ -> CloseableWrapper(KotlinAnalysisApiEngine()) },
                CloseableWrapper::class.java
            )
            .resource as KotlinAnalysisApiEngine

    companion object {
        private val NAMESPACE = ExtensionContext.Namespace.create("KotlinAnalysisApiEngine")
        private const val KEY = "KotlinAnalysisApiEngine"
    }
}

private class CloseableWrapper<T : AutoCloseable>(val resource: T) :
    ExtensionContext.Store.CloseableResource,
    AutoCloseable {
    override fun close() {
        resource.close()
    }
}
