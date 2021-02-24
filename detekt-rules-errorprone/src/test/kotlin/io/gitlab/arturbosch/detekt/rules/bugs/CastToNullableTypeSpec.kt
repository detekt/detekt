package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class CastToNullableTypeSpec : Spek({
    setupKotlinEnvironment()
    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { CastToNullableType() }

    describe("CastToNullableTypeSpec rule") {
        it("casting to nullable types") {
            val code = """
                fun foo(a: Any?) {
                    val x: String? = a as String?
                } 
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasSourceLocation(2, 22)
            assertThat(findings[0]).hasMessage("Use the safe cast ('as? String') instead of 'as String?'.")
        }

        it("safe casting") {
            val code = """
                fun foo(a: Any?) {
                    val x: String? = a as? String
                } 
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(0)
        }

        it("type checking") {
            val code = """
                fun foo(a: Any?) {
                    val x = a is String?
                } 
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(0)
        }
    }
})
