package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UnreachableCatchBlockSpec : Spek({
    setupKotlinEnvironment()
    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { UnreachableCatchBlock() }

    describe("UnreachableCatchBlock rule") {
        it("reports a unreachable catch block that is after the super class catch block") {
            val code = """
                fun test() {
                    try {
                    } catch (t: Throwable) {
                    } catch (e: Exception) {
                    }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasSourceLocation(4, 7)
        }

        it("reports a unreachable catch block that is after the same class catch block") {
            val code = """
                fun test() {
                    try {
                    } catch (e: Exception) {
                    } catch (e: Exception) {
                    }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasSourceLocation(4, 7)
        }

        it("reports two unreachable catch blocks that is after the super class catch block") {
            val code = """
                fun test() {
                    try {
                    } catch (e: RuntimeException) {
                    } catch (e: IllegalArgumentException) {
                    } catch (e: IllegalStateException) {
                    }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(2)
            assertThat(findings).hasSourceLocations(
                SourceLocation(4, 7),
                SourceLocation(5, 7)
            )
        }

        it("does not report unreachable catch block") {
            val code = """
                fun test() {
                    try {
                    } catch (e: IllegalArgumentException) {
                    } catch (e: IllegalStateException) {
                    } catch (e: RuntimeException) {
                    }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }
})
