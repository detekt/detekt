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

/**
 * [additionalJavaSourcePaths] should be paths relative to the `test/resources` directory of your Gradle module.
 * [additionalJarPaths] should be absolute file paths.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@ExtendWith(KotlinEnvironmentResolver::class)
annotation class KotlinCoreEnvironmentTest(
    val additionalJavaSourcePaths: Array<String> = [],
    val additionalJarPaths: Array<String> = [],
)

internal class KotlinEnvironmentResolver : ParameterResolver {
    private val ExtensionContext.env: KotlinEnvironmentContainer
        get() = getStore(NAMESPACE).getOrComputeIfAbsent(
            ENV_KEY,
            { _ -> createNewContainer(this) },
            KotlinEnvironmentContainer::class.java,
        )

    override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean =
        parameterContext.parameter.type == KotlinEnvironmentContainer::class.java

    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any =
        extensionContext.env

    private fun createNewContainer(extensionContext: ExtensionContext): KotlinEnvironmentContainer {
        val annotation = extensionContext.requiredTestClass.annotations
            .find { it is KotlinCoreEnvironmentTest } as? KotlinCoreEnvironmentTest

        val classLoader = Thread.currentThread().contextClassLoader
        val classpath = checkNotNull(classpathFromClassloader(classLoader)) {
            "We should always have a classpath"
        }

        return createEnvironment(
            additionalRootPaths = classpath + annotation.additionalJarPaths(),
            additionalJavaSourceRootPaths = annotation.additionalJavaSourcePaths(),
        )
    }

    companion object {
        private val NAMESPACE = ExtensionContext.Namespace.create("KotlinCoreEnvironment")
        private const val ENV_KEY = "env"

        private fun KotlinCoreEnvironmentTest?.additionalJavaSourcePaths(): List<File> =
            this?.additionalJavaSourcePaths
                ?.map { resourceAsPath(it).toFile() }
                .orEmpty()

        private fun KotlinCoreEnvironmentTest?.additionalJarPaths(): List<File> =
            this?.additionalJarPaths
                ?.map(::File)
                .orEmpty()
    }
}
