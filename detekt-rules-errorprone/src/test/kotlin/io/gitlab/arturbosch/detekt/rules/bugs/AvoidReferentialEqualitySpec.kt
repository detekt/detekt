package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object AvoidReferentialEqualitySpec : Spek({
    setupKotlinEnvironment()

    val env: KotlinCoreEnvironment by memoized()

    describe("ReferentialEquality with defaults") {
        val subject by memoized { AvoidReferentialEquality(Config.empty) }
        it("reports usage of === for strings") {
            val code = """
                val s = "a string" 
                val b = s === "something"
                fun f(other: String) = s === other
                fun g(other: String) = if (s === other) 1 else 2
            """.trimIndent()

            val actual = subject.compileAndLintWithContext(env, code)

            assertThat(actual).hasSize(3)
        }
        it("reports usage of === with nullable") {
            val code = """
                var s: String? = "a string" 
                val b1 = s === "something"
                val b2 = "something" === s
                fun f(other: String) = s === other
                fun g(other: String?) = s === other
            """.trimIndent()

            val actual = subject.compileAndLintWithContext(env, code)

            assertThat(actual).hasSize(4)
        }
        it("reports usage of !== for strings") {
            val code = """
                var s: String = "a string" 
                val b = s !== "something"
            """.trimIndent()

            val actual = subject.compileAndLintWithContext(env, code)

            assertThat(actual).hasSize(1)
        }
        it("ignores usage of === for non strings") {
            val code = """
                val i = 42 
                val l = 99L
                val c = 'a'
                val b = i === 1 || l === 100L || c === 'b'
            """.trimIndent()

            val actual = subject.compileAndLintWithContext(env, code)

            assertThat(actual).isEmpty()
        }
        it("ignores usage of == for strings") {
            val code = """
                val s = "a string" 
                val b = s == "something"
                fun f(other: String) = s == other
                fun g(other: String) = if (s == other) 1 else 2
            """.trimIndent()

            val actual = subject.compileAndLintWithContext(env, code)

            assertThat(actual).isEmpty()
        }
        it("ignores usage of === with generic parameters") {
            val code = """
                fun <T : Any> same(one: T, two: T): Boolean = one === two
                val b = same("this", "that")
            """.trimIndent()

            val actual = subject.compileAndLintWithContext(env, code)

            assertThat(actual).isEmpty()
        }
    }

    describe("ReferentialEquality enabled for all types") {
        val subject by memoized { AvoidReferentialEquality(TestConfig("forbiddenTypesRegex" to "*")) }
        it("reports usage of === for strings") {
            val code = """
                val s = "a string" 
                val i = 1 
                val list = listOf(1)
                val b = s === "other" || i === 42 || list === listOf(2)
            """.trimIndent()

            val actual = subject.compileAndLintWithContext(env, code)

            assertThat(actual).hasSize(3)
        }
    }

    describe("ReferentialEquality enabled for all lists") {
        val pattern = """kotlin.collections.*List"""
        val subject by memoized { AvoidReferentialEquality(TestConfig("forbiddenTypesRegex" to pattern)) }
        it("reports usage of ===") {
            val code = """
                val listA = listOf(1)
                val listB = listOf(1)
                val mutableList = mutableListOf(1)
                val b = listOf(2) === listA || listA === listB || mutableList === mutableListOf(2)
            """.trimIndent()

            val actual = subject.compileAndLintWithContext(env, code)

            assertThat(actual).hasSize(3)
        }
        it("ignores usage of ==") {
            val code = """
                val listA = listOf(1)
                val listB = listOf(1)
                val mutableList = mutableListOf(1)
                val b = listOf(2) == listA || listA == listB || mutableList == mutableListOf(2)
            """.trimIndent()

            val actual = subject.compileAndLintWithContext(env, code)

            assertThat(actual).isEmpty()
        }
    }
})
