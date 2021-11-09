package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UseAnyOrNoneInsteadOfFindSpec : Spek({
    setupKotlinEnvironment()

    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { UseAnyOrNoneInsteadOfFind() }

    describe("UseAnyOrNoneInsteadOfFind rule") {
        it("Reports collections.find != null") {
            val code = "val x = listOf(1, 2, 3).find { it == 4 } != null"
            val actual = subject.compileAndLintWithContext(env, code)
            Assertions.assertThat(actual).hasSize(1)
            Assertions.assertThat(actual[0].message).isEqualTo("Use 'any' instead of 'find'")
        }
        it("Reports sequences.find != null") {
            val code = "val x = sequenceOf(1, 2, 3).find { it == 4 } != null"
            val actual = subject.compileAndLintWithContext(env, code)
            Assertions.assertThat(actual).hasSize(1)
        }
        it("Reports text.find != null") {
            val code = "val x = \"123\".find { it == '4' } != null"
            val actual = subject.compileAndLintWithContext(env, code)
            Assertions.assertThat(actual).hasSize(1)
        }

        it("Reports collections.firstOrNull != null") {
            val code = "val x = arrayOf(1, 2, 3).firstOrNull { it == 4 } != null"
            val actual = subject.compileAndLintWithContext(env, code)
            Assertions.assertThat(actual).hasSize(1)
            Assertions.assertThat(actual[0].message).isEqualTo("Use 'any' instead of 'firstOrNull'")
        }
        it("Reports sequences.firstOrNull != null") {
            val code = "val x = sequenceOf(1, 2, 3).firstOrNull { it == 4 } != null"
            val actual = subject.compileAndLintWithContext(env, code)
            Assertions.assertThat(actual).hasSize(1)
        }
        it("Reports text.firstOrNull != null") {
            val code = "val x = \"123\".firstOrNull { it == '4' } != null"
            val actual = subject.compileAndLintWithContext(env, code)
            Assertions.assertThat(actual).hasSize(1)
        }

        it("Reports collections.find == null") {
            val code = "val x = setOf(1, 2, 3).find { it == 4 } == null"
            val actual = subject.compileAndLintWithContext(env, code)
            Assertions.assertThat(actual).hasSize(1)
            Assertions.assertThat(actual[0].message).isEqualTo("Use 'none' instead of 'find'")
        }

        it("Reports null != collections.find") {
            val code = "val x = null != listOf(1, 2, 3).find { it == 4 }"
            val actual = subject.compileAndLintWithContext(env, code)
            Assertions.assertThat(actual).hasSize(1)
            Assertions.assertThat(actual[0].message).isEqualTo("Use 'any' instead of 'find'")
        }

        it("Reports collections.find != null in extension") {
            val code = "fun List<Int>.test(): Boolean = find { it == 4 } != null"
            val actual = subject.compileAndLintWithContext(env, code)
            Assertions.assertThat(actual).hasSize(1)
        }

        it("Does not reports collections.lastOrNull != null") {
            val code = "val x = listOf(1, 2, 3).lastOrNull { it == 4 } != null"
            val actual = subject.compileAndLintWithContext(env, code)
            Assertions.assertThat(actual).hasSize(0)
        }
    }
})
