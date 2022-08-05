package io.gitlab.arturbosch.detekt.core.config

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.resolve.descriptorUtil.getAllSuperclassesWithoutAny
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class KotlinCoreEnvironmentTestSpec {

    private val code = """
        import io.gitlab.arturbosch.detekt.api.Config
        import io.gitlab.arturbosch.detekt.api.Rule
        
        class B(config: Config) : Rule(config) {
            override val issue = error("I don't care")
        }
    """.trimIndent()

    private val rule = VerifyTypeHierarchieAvailable()

    @Nested
    @KotlinCoreEnvironmentTest
    inner class `Without additional types`(private val env: KotlinCoreEnvironment) {
        @Test
        fun `no types from detekt api are available on classpath`() {
            val actual = rule.compileAndLintWithContext(env, code).single()

            assertThat(actual.message).isEqualTo("[]")
        }
    }

    @Nested
    inner class `With additional types` {
        val expected = "[" +
            "io.gitlab.arturbosch.detekt.api.Rule, " +
            "io.gitlab.arturbosch.detekt.api.BaseRule, " +
            "io.gitlab.arturbosch.detekt.api.DetektVisitor, " +
            "org.jetbrains.kotlin.psi.KtTreeVisitorVoid" +
            "]"

        @Nested
        @KotlinCoreEnvironmentTest(additionalTypes = [Rule::class])
        inner class `Only addiontial types`(private val env: KotlinCoreEnvironment) {
            @Test
            fun `types from detekt api are available on classpath`() {
                val actual = rule.compileAndLintWithContext(env, code).single()

                assertThat(actual.message).isEqualTo(expected)
            }
        }

        @Nested
        @KotlinCoreEnvironmentTest(additionalTypes = [Rule::class, CharRange::class])
        inner class `Also types that are already available`(private val env: KotlinCoreEnvironment) {
            @Test
            fun `no conflict if types are added multiple times`() {
                val actual = rule.compileAndLintWithContext(env, code).single()

                assertThat(actual.message).isEqualTo(expected)
            }
        }
    }

    @RequiresTypeResolution
    private class VerifyTypeHierarchieAvailable : Rule() {
        override val issue: Issue = Issue("a", Severity.Minor, "", Debt.FIVE_MINS)

        override fun visitClass(klass: KtClass) {
            super.visitClass(klass)

            val superTypes = bindingContext[BindingContext.CLASS, klass]
                ?.getAllSuperclassesWithoutAny()
                ?.map { checkNotNull(it.fqNameOrNull()).toString() }
                .orEmpty()

            report(
                CodeSmell(issue, Entity.atName(klass), superTypes.toString())
            )
        }
    }
}
