package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.test.KtTestCompiler
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UnnecessarySafeCallSpec : Spek({
    val subject by memoized { UnnecessarySafeCall() }

    val wrapper by memoized(
        factory = { KtTestCompiler.createEnvironment() },
        destructor = { it.dispose() }
    )

    describe("check unnecessary safe operators") {

        it("reports a simple safe operator usage") {
            val code = """
                fun test(s: String) {
                    val a = 1
                    val b = a?.toString()
                }"""
            val findings = subject.compileAndLintWithContext(wrapper.env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasTextLocations(48 to 61)
        }

        it("reports a chained safe operator usage") {
            val code = """
                fun test(s: String) {
                    val a = 1
                    val b = a?.plus(42)
                }"""
            val findings = subject.compileAndLintWithContext(wrapper.env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasTextLocations(48 to 59)
        }

        it("reports multiple chained safe operator usage") {
            val code = """
                fun test(s: String) {
                    val a = 1
                    val b = a?.plus(42)?.minus(24)
                }"""
            val findings = subject.compileAndLintWithContext(wrapper.env, code)
            assertThat(findings).hasSize(2)
            assertThat(findings).hasTextLocations(48 to 59, 48 to 70)
        }
    }

    describe("check valid safe operators usage") {

        it("does not reports a simple safe operator usage on nullable type") {
            val code = """
                fun test(s: String) {
                    val a : Int? = 1
                    val b = a?.plus(42)
                }"""
            val findings = subject.compileAndLintWithContext(wrapper.env, code)
            assertThat(findings).isEmpty()
        }
    }
})
