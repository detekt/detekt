package io.gitlab.arturbosch.detekt.rules

import io.github.detekt.test.utils.KotlinCoreEnvironmentWrapper
import io.github.detekt.test.utils.createEnvironment
import io.github.detekt.test.utils.resourceAsPath
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import org.spekframework.spek2.dsl.Root
import org.spekframework.spek2.lifecycle.CachingMode
import java.io.File
import java.nio.file.Path
import kotlin.script.experimental.jvm.util.classpathFromClassloader

@Deprecated(
    "This is specific to Spek and will be removed in a future release. Documentation has been updated to " +
        "show alternative approaches: https://detekt.dev/type-resolution.html#testing-a-rule-that-uses-type-resolution"
)
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
annotation class KotlinCoreEnvironmentTest(
    val additionalJavaSourcePaths: Array<String> = []
)

internal class KotlinEnvironmentResolver : ParameterResolver {
    private var ExtensionContext.wrapper: CloseableWrapper?
        get() = getStore(NAMESPACE)[WRAPPER_KEY, CloseableWrapper::class.java]
        set(value) = getStore(NAMESPACE).put(WRAPPER_KEY, value)

    override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean {
        return parameterContext.parameter.type == KotlinCoreEnvironment::class.java
    }

    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any {
        val closeableWrapper = extensionContext.wrapper
            ?: CloseableWrapper(
                createEnvironment(
                    additionalRootPaths = checkNotNull(
                        classpathFromClassloader(Thread.currentThread().contextClassLoader)
                    ) { "We should always have a classpath" },
                    additionalJavaSourceRootPaths = extensionContext.additionalJavaSourcePaths()
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
