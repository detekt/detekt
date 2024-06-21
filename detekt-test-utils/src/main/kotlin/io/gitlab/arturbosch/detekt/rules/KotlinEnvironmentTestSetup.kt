package io.gitlab.arturbosch.detekt.rules

import io.github.detekt.test.utils.KotlinCoreEnvironmentWrapper
import io.github.detekt.test.utils.createEnvironment
import io.github.detekt.test.utils.resourceAsPath
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import java.io.File
import kotlin.script.experimental.jvm.util.classpathFromClassloader

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@ExtendWith(KotlinEnvironmentResolver::class)
annotation class KotlinCoreEnvironmentTest(
    val additionalJavaSourcePaths: Array<String> = []
)

internal class KotlinEnvironmentResolver : ParameterResolver {
    private var ExtensionContext.wrapper: CloseableWrapper?
        get() = getStore(NAMESPACE)[WRAPPER_KEY, CloseableWrapper::class.java]
        set(value) = getStore(NAMESPACE).put(WRAPPER_KEY, value)

    override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean =
        parameterContext.parameter.type == KotlinCoreEnvironment::class.java

    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any {
        val closeableWrapper = extensionContext.wrapper
            ?: CloseableWrapper(
                createEnvironment(
                    additionalRootPaths = checkNotNull(
                        classpathFromClassloader(Thread.currentThread().contextClassLoader)
                    ) { "We should always have a classpath" },
                    additionalJavaSourceRootPaths = extensionContext.additionalJavaSourcePaths(),
                )
            ).also { extensionContext.wrapper = it }
        return closeableWrapper.wrapper.env
    }

    companion object {
        private val NAMESPACE = ExtensionContext.Namespace.create("KotlinCoreEnvironment")
        private const val WRAPPER_KEY = "wrapper"
        private fun ExtensionContext.additionalJavaSourcePaths(): List<File> {
            val annotation = requiredTestClass.annotations
                .find { it is KotlinCoreEnvironmentTest } as? KotlinCoreEnvironmentTest ?: return emptyList()
            return annotation.additionalJavaSourcePaths.map { resourceAsPath(it).toFile() }
        }
    }

    private class CloseableWrapper(val wrapper: KotlinCoreEnvironmentWrapper) :
        ExtensionContext.Store.CloseableResource {
        override fun close() {
            wrapper.dispose()
        }
    }
}
