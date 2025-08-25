package dev.detekt.test.junit

import com.intellij.openapi.util.Disposer
import dev.detekt.test.utils.KotlinEnvironmentContainer
import dev.detekt.test.utils.resourceAsPath
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
    val additionalJavaSourcePaths: Array<String> = [],
)

internal class KotlinEnvironmentResolver : ParameterResolver {
    private var ExtensionContext.wrapper: KotlinCoreEnvironmentWrapper?
        get() = getStore(NAMESPACE)[WRAPPER_KEY, KotlinCoreEnvironmentWrapper::class.java]
        set(value) = getStore(NAMESPACE).put(WRAPPER_KEY, value)

    override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean =
        parameterContext.parameter.type == KotlinEnvironmentContainer::class.java

    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any {
        var closeableWrapper = extensionContext.wrapper
        if (closeableWrapper == null) {
            val disposable = Disposer.newDisposable()
            closeableWrapper = KotlinCoreEnvironmentWrapper(
                createEnvironment(
                    disposable,
                    additionalRootPaths = checkNotNull(
                        classpathFromClassloader(Thread.currentThread().contextClassLoader)
                    ) { "We should always have a classpath" },
                    additionalJavaSourceRootPaths = extensionContext.additionalJavaSourcePaths(),
                ),
                disposable,
            )
            extensionContext.wrapper = closeableWrapper
        }
        return closeableWrapper.env
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
}
