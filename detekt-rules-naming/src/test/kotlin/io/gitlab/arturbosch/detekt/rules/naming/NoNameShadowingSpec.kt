package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class NoNameShadowingSpec : Spek({
    setupKotlinEnvironment()
    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { NoNameShadowing() }

    describe("NoNameShadowing rule") {
        it("report shadowing variable") {
            val code = """
                fun test(i: Int) {
                    val i = 1
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasSourceLocation(2, 9)
            assertThat(findings[0]).hasMessage("Name shadowed: i")
        }

        it("report shadowing destructuring declaration entry") {
            val code = """
                fun test(j: Int) {
                    val (j, _) = 1 to 2
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings[0]).hasMessage("Name shadowed: j")
        }

        it("report shadowing lambda parameter") {
            val code = """
                fun test(k: Int) {
                    listOf(1).map { k ->
                    }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings[0]).hasMessage("Name shadowed: k")
        }

        it("does not report not shadowing variable") {
            val code = """
                fun test(i: Int) {
                    val j = i
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }
})
