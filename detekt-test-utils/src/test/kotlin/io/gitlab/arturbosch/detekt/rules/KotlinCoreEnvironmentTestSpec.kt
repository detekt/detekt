package io.gitlab.arturbosch.detekt.rules

import io.github.detekt.test.utils.compileContentForTest
import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.compiler.NoScopeRecordCliBindingTrace
import org.jetbrains.kotlin.cli.jvm.compiler.TopDownAnalyzerFacadeForJVM
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.resolve.descriptorUtil.getAllSuperclassesWithoutAny
import org.jetbrains.kotlin.resolve.lazy.declarations.FileBasedDeclarationProviderFactory
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class KotlinCoreEnvironmentTestSpec {

    private val code = """
        import org.assertj.core.api.AbstractAssert
        
        class CustomAssert(actual: String) : AbstractAssert<CustomAssert, String>(actual, CustomAssert::class.java)
    """.trimIndent()

    @Nested
    @KotlinCoreEnvironmentTest
    inner class `Without additional types`(private val env: KotlinCoreEnvironment) {
        @Test
        fun `no assertj api are available on classpath`() {
            val actual = code.compileAndGetSuperTypes(env)

            assertThat(actual).isEmpty()
        }
    }

    @Nested
    inner class `With additional types` {
        val expected = listOf("org.assertj.core.api.AbstractAssert")

        @Nested
        @KotlinCoreEnvironmentTest(additionalTypes = [AbstractAssert::class])
        inner class `Only addiontial types`(private val env: KotlinCoreEnvironment) {
            @Test
            fun `types from detekt api are available on classpath`() {
                val actual = code.compileAndGetSuperTypes(env)

                assertThat(actual).isEqualTo(expected)
            }
        }

        @Nested
        @KotlinCoreEnvironmentTest(additionalTypes = [AbstractAssert::class, CharRange::class])
        inner class `Also types that are already available`(private val env: KotlinCoreEnvironment) {
            @Test
            fun `no conflict if types are added multiple times`() {
                val actual = code.compileAndGetSuperTypes(env)

                assertThat(actual).isEqualTo(expected)
            }
        }
    }

    private fun String.compileAndGetSuperTypes(env: KotlinCoreEnvironment): List<String> {
        val ktFile = compileContentForTest(this)
        val ktClass = ktFile.findChildByClass(KtClass::class.java)!!
        val bindingContext = env.getContextForPaths(listOf(ktFile))

        return bindingContext[BindingContext.CLASS, ktClass]
            ?.getAllSuperclassesWithoutAny()
            ?.map { checkNotNull(it.fqNameOrNull()).toString() }
            .orEmpty()
    }
}

// copied from KotlinCoreEnvironmentExtensions.kt
// this is not ideal
private fun KotlinCoreEnvironment.getContextForPaths(paths: List<KtFile>): BindingContext =
    TopDownAnalyzerFacadeForJVM.analyzeFilesWithJavaIntegration(
        this.project,
        paths,
        NoScopeRecordCliBindingTrace(),
        this.configuration,
        this::createPackagePartProvider,
        ::FileBasedDeclarationProviderFactory
    ).bindingContext
