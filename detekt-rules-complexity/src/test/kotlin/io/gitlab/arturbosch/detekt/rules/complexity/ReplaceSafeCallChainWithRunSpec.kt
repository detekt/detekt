package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object ReplaceSafeCallChainWithRunSpec : Spek({
    setupKotlinEnvironment()

    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { ReplaceSafeCallChainWithRun() }

    describe("ReplaceSafeChainWithRun rule") {

        it("reports long chain of unnecessary safe qualified expressions") {
            val code = """
                val x: String? = "string"

                val y = x
                    ?.asSequence()
                    ?.map { it }
                    ?.distinctBy { it }
                    ?.iterator()
                    ?.forEach(::println)
            """

            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("reports short chain of unnecessary safe qualified expressions") {
            val code = """
                val x: String? = "string"

                val y = x
                    ?.asSequence()
                    ?.map { it }
            """

            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("does not report a safe call chain which is too short to benefit") {
            val code = """
                val x: String? = "string"

                val y = x
                    ?.asSequence()
            """

            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("does not report a safe call chain on left side of assignment") {
            val code = """
                class Something {
                    var element: Element? = null
                }

                class Element(var list: List<String>?)

                val z: Something? = Something()

                fun modifyList() {
                    z?.element?.list = listOf("strings")
                }
            """

            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }
    }
})
