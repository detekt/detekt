package dev.detekt.test.junit

import dev.detekt.test.utils.KotlinEnvironmentContainer
import dev.detekt.test.utils.createEnvironment
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
    private val ExtensionContext.env: KotlinEnvironmentContainer
        get() = getStore(NAMESPACE).getOrComputeIfAbsent(
            WRAPPER_KEY,
            { _ -> createNewWrapper(this) },
            KotlinEnvironmentContainer::class.java,
        )

    override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean =
        parameterContext.parameter.type == KotlinEnvironmentContainer::class.java

    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any =
        extensionContext.env

    private fun createNewWrapper(extensionContext: ExtensionContext): KotlinEnvironmentContainer =
        createEnvironment(
            additionalRootPaths = checkNotNull(
                classpathFromClassloader(Thread.currentThread().contextClassLoader)
            ) { "We should always have a classpath" },
            additionalJavaSourceRootPaths = extensionContext.additionalJavaSourcePaths(),
        )

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
