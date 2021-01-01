package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UnnecessaryFilterSpec : Spek({

    val subject by memoized { UnnecessaryFilter(Config.empty) }

    describe("UnnecessaryFilter") {
        it("Filter with size") {
            val code = """
                val x = listOf(1, 2, 3)
                    .filter { it > 1 }
                    .size
            """

            val findings = subject.compileAndLint(code)
            assertThat(findings).hasSize(1)
        }

        it("Filter with count") {
            val code = """
                val x = listOf(1, 2, 3)
                    .filter { it > 1 }
                    .count()
            """

            val findings = subject.compileAndLint(code)
            assertThat(findings).hasSize(1)
        }

        it("None item") {
            val code = """
                val x = listOf(1, 2, 3)
                    .filter { it > 2 }
                    .isEmpty()
            """

            val findings = subject.compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }

    describe("Correct filter") {
        it("Filter with count") {
            val code = """
                val x = listOf(1, 2, 3)
                    .count { it > 2 }
            """

            val findings = subject.compileAndLint(code)
            assertThat(findings).isEmpty()
        }

        it("None item") {
            val code = """
                val x = listOf(1, 2, 3)
                    .none { it > 2 }
            """

            val findings = subject.compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }
})
