package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UnnecessaryFilterSpec : Spek({

    val subject by memoized { UnnecessaryFilter() }

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

        it("Sequence with count") {
            val code = """
                val x = listOf(1, 2, 3)
                    .asSequence()
                    .map { it * 2 }
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
            assertThat(findings).hasSize(1)
        }

        it("Any item") {
            val code = """
                val x = listOf(1, 2, 3)
                    .filter { it > 2 }
                    .isNotEmpty()
            """

            val findings = subject.compileAndLint(code)
            assertThat(findings).hasSize(1)
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

        it("Any item") {
            val code = """
                val x = listOf(1, 2, 3)
                    .any { it > 2 }
            """

            val findings = subject.compileAndLint(code)
            assertThat(findings).isEmpty()
        }

        it("Sequence with count") {
            val code = """
                val x = listOf(1, 2, 3)
                    .asSequence()
                    .map { it * 2 }
                    .count { it > 1 }
            """

            val findings = subject.compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }
})
