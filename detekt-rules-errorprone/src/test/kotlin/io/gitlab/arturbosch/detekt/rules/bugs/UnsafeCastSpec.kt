package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UnsafeCastSpec : Spek({
    setupKotlinEnvironment()

    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { UnsafeCast() }

    describe("check safe and unsafe casts") {

        it("reports cast that cannot succeed") {
            val code = """
                fun test(s: String) {
                    println(s as Int)
                }"""
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("reports 'safe' cast that cannot succeed") {
            val code = """
                fun test(s: String) {
                    println((s as? Int) ?: 0)
                }"""
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("does not report cast that might succeed") {
            val code = """
                fun test(s: Any) {
                    println(s as Int)
                }"""
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("does not report 'safe' cast that might succeed") {
            val code = """
                fun test(s: Any) {
                    println((s as? Int) ?: 0)
                }"""
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }
    }
})
