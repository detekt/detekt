package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object ForbiddenSingleExpressionFunctionSpec : Spek({
    setupKotlinEnvironment()

    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { ForbiddenSingleExpressionFunction() }

    describe("ForbiddenSingleExpressionFunction rule") {

        it("flags a function using single expression syntax with inferred return type Unit") {
            val code = """fun test() = println("something")"""
            val findings = subject.compileAndLintWithContext(env, code)

            assertThat(findings).hasSize(1)
        }

        it("flags a function using single expression syntax with inferred return type Unit?") {
            val code = """fun test(param: String?) = param?.let { println("something") }"""
            val findings = subject.compileAndLintWithContext(env, code)

            assertThat(findings).hasSize(1)
        }

        it("does not flag a function using single expression syntax with inferred return type String") {
            val code = """fun test() = "something" + "else""""
            val findings = subject.compileAndLintWithContext(env, code)

            assertThat(findings).isEmpty()
        }

        it("does not flag a function using single expression syntax with the return type Unit specified") {
            val code = """fun test(): Unit = println("something")"""
            val findings = subject.compileAndLintWithContext(env, code)

            assertThat(findings).isEmpty()
        }

        it("does not flag a function using single expression syntax with the return type Unit? specified") {
            val code = """fun test(param: String?): Unit? = param?.let { println("something") }"""
            val findings = subject.compileAndLintWithContext(env, code)

            assertThat(findings).isEmpty()
        }

        it("does not flag a function using single expression syntax returning the Unit object") {
            val code = """fun test() = Unit"""
            val findings = subject.compileAndLintWithContext(env, code)

            assertThat(findings).isEmpty()
        }

        it("does not flag a function with an empty body") {
            val code = """fun test() { }"""
            val findings = subject.compileAndLintWithContext(env, code)

            assertThat(findings).isEmpty()
        }
    }
})
