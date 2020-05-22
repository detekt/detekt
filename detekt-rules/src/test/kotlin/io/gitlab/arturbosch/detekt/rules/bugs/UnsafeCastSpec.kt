package io.gitlab.arturbosch.detekt.rules.bugs

import io.github.detekt.test.utils.KtTestCompiler
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UnsafeCastSpec : Spek({
    val subject by memoized { UnsafeCast() }

    val wrapper by memoized(
        factory = { KtTestCompiler.createEnvironment() },
        destructor = { it.dispose() }
    )

    describe("check safe and unsafe casts") {

        it("reports cast that cannot succeed") {
            val code = """
                fun test(s: String) {
                    println(s as Int)
                }"""
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).hasSize(1)
        }

        it("reports 'safe' cast that cannot succeed") {
            val code = """
                fun test(s: String) {
                    println((s as? Int) ?: 0)
                }"""
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).hasSize(1)
        }

        it("does not report cast that might succeed") {
            val code = """
                fun test(s: Any) {
                    println(s as Int)
                }"""
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).isEmpty()
        }

        it("does not report 'safe' cast that might succeed") {
            val code = """
                fun test(s: Any) {
                    println((s as? Int) ?: 0)
                }"""
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).isEmpty()
        }
    }
})
