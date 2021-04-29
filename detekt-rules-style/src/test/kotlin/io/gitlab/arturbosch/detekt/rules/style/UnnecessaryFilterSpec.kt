package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UnnecessaryFilterSpec : Spek({
    setupKotlinEnvironment()
    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { UnnecessaryFilter() }

    describe("UnnecessaryFilter") {
        it("Filter with size") {
            val code = """
                val x = listOf(1, 2, 3)
                    .filter { it > 1 }
                    .size
            """

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings[0]).hasMessage("'filter { it > 1 }' can be replaced by 'size { it > 1 }'")
        }

        it("Filter with count") {
            val code = """
                val x = listOf(1, 2, 3)
                    .filter { it > 1 }
                    .count()
            """

            val findings = subject.compileAndLintWithContext(env, code)
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

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        it("None item") {
            val code = """
                val x = listOf(1, 2, 3)
                    .filter { it > 2 }
                    .isEmpty()
            """

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        it("Any item") {
            val code = """
                val x = listOf(1, 2, 3)
                    .filter { it > 2 }
                    .isNotEmpty()
            """

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }
    }

    describe("Correct filter") {
        it("Not stdlib count list function") {
            val code = """
                fun <T> List<T>.count() : Any{
                    return Any()
                }
                
                val x = listOf<Int>().count()
                val y = listOf<Int>().filter { it > 0 }.count()
            """

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        it("Not stdlib count sequences function") {
            val code = """
                fun <T> Sequence<T>.count() : Any{
                    return Any()
                }
                
                val x = listOf<Int>().asSequence().count()
                val y = listOf<Int>().asSequence().filter { it > 0 }.count()
            """

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        it("Not stdlib filter function") {
            val code = """
                fun filter() : List<Any>{
                    return emptyList()
                }
                
                val x = filter().size
            """

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        it("Filter with count") {
            val code = """
                val x = listOf(1, 2, 3)
                    .count { it > 2 }
            """

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        it("None item") {
            val code = """
                val x = listOf(1, 2, 3)
                    .none { it > 2 }
            """

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        it("Any item") {
            val code = """
                val x = listOf(1, 2, 3)
                    .any { it > 2 }
            """

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        it("Sequence with count") {
            val code = """
                val x = listOf(1, 2, 3)
                    .asSequence()
                    .map { it * 2 }
                    .count { it > 1 }
            """

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }
})
