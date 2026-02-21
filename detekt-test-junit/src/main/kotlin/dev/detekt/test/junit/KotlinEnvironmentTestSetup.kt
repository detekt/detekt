package dev.detekt.test.junit

import dev.detekt.test.utils.KotlinEnvironmentContainer
import dev.detekt.test.utils.createEnvironment
import dev.detekt.test.utils.resourceAsPath
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import java.io.File

/**
 * This annotation must be applied to a test class to make use of type analysis APIs when testing Detekt rules, e.g.
 * via `lintWithContext`. Make sure to pass an instance of [KotlinEnvironmentContainer] into the constructor of your
 * test class, which you can then pass into `lintWithContext` to compile/lint the code snippet against your rule.
 *
 * @param additionalJavaSourcePaths should be paths relative to the `test/resources` directory of your Gradle module. These
 * must be Java (not Kotlin) source files, which will then be importable from your test snippets.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@ExtendWith(KotlinEnvironmentResolver::class)
annotation class KotlinCoreEnvironmentTest(val additionalJavaSourcePaths: Array<String> = [])

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

    private fun createNewContainer(extensionContext: ExtensionContext) =
        createEnvironment(
            additionalJavaSourceRootPaths = extensionContext.additionalJavaSourcePaths(),
        )

    companion object {
        private val NAMESPACE = ExtensionContext.Namespace.create("KotlinCoreEnvironment")
        private const val ENV_KEY = "env"
        private fun ExtensionContext.additionalJavaSourcePaths(): List<File> {
            val annotation = requiredTestClass.annotations
                .find { it is KotlinCoreEnvironmentTest } as? KotlinCoreEnvironmentTest ?: return emptyList()
            return annotation.additionalJavaSourcePaths.map { resourceAsPath(it).toFile() }
        }
    }
}
