package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object UseRequireNotNullSpec : Spek({
    setupKotlinEnvironment()

    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { UseRequireNotNull() }

    describe("UseRequireNotNull rule") {
        it("reports `require` calls with a non-null check") {
            val code = """
                fun test(i: Int?) {
                    require(i != null)
                }
            """
            val actual = subject.compileAndLintWithContext(env, code)
            assertThat(actual).hasSize(1)
        }

        it("reports `require` calls with a non-null check that has `null` on the left side") {
            val code = """
                fun test(i: Int?) {
                    require(null != i)
                }
            """
            val actual = subject.compileAndLintWithContext(env, code)
            assertThat(actual).hasSize(1)
        }

        it("does not report a `require` call without a non-null check") {
            val code = """
                fun test(i: Int) {
                    require(i > 0)
                }
            """
            val actual = subject.compileAndLintWithContext(env, code)
            assertThat(actual).isEmpty()
        }
    }
})
