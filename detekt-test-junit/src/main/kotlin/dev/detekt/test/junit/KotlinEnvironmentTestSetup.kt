package dev.detekt.test.junit

import dev.detekt.test.utils.KotlinEnvironmentContainer
import dev.detekt.test.utils.createEnvironment
import dev.detekt.test.utils.resourceAsPath
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import java.io.File
import kotlin.reflect.KClass
import kotlin.script.experimental.jvm.util.classpathFromClassloader

/**
 * This annotation must be applied to a test class to make use of type analysis APIs when testing Detekt rules, e.g.
 * via `lintWithContext`. Make sure to pass an instance of [KotlinEnvironmentContainer] into the constructor of your
 * test class, which you can then pass into `lintWithContext` to compile/lint the code snippet against your rule.
 *
 * [additionalJavaSourcePaths] should be paths relative to the `test/resources` directory of your Gradle module. These
 * must be Java (not Kotlin) source files, which will then be importable from your test snippets.
 *
 * [additionalLibraryTypes] is a method of including third-party library artifacts in the classpath when compiling your
 * test snippet. As long as the specified types are part of that library, Detekt will dynamically load the associated
 * artifact when compiling your test snippet. So you will be able to reference any type from that library - not just
 * the one specified here. Specifying two [KClass]s from the same artifact will have no extra effect.
 * `org.jetbrains.kotlin:kotlin-stdlib`, `org.jetbrains.kotlinx:kotlinx-coroutines-core` and
 * `org.jetbrains.kotlinx:kotlinx-coroutines-test` are loaded into the snippet classpath by default.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@ExtendWith(KotlinEnvironmentResolver::class)
annotation class KotlinCoreEnvironmentTest(
    val additionalJavaSourcePaths: Array<String> = [],
    val additionalLibraryTypes: Array<KClass<*>> = [],
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
            additionalRootPaths = classpath,
            additionalJavaSourceRootPaths = annotation.additionalJavaSourcePaths(),
            additionalLibraryTypes = annotation?.additionalLibraryTypes?.toList().orEmpty(),
        )
    }

    companion object {
        private val NAMESPACE = ExtensionContext.Namespace.create("KotlinCoreEnvironment")
        private const val ENV_KEY = "env"

        private fun KotlinCoreEnvironmentTest?.additionalJavaSourcePaths(): List<File> =
            this?.additionalJavaSourcePaths
                ?.map { resourceAsPath(it).toFile() }
                .orEmpty()
    }
}
