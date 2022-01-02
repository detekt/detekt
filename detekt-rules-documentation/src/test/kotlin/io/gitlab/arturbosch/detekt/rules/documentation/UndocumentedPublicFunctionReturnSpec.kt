package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UndocumentedPublicFunctionReturnSpec : Spek({
    setupKotlinEnvironment()

    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { UndocumentedPublicFunctionReturn() }

    describe("UndocumentedPublicFunction rule") {
        context("non-Unit return") {
            it("reports missing return tag on documented function") {
                val code = """
                    /**
                     * I don't have a return tag
                     */
                    fun hello(): String { TODO() }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
            }

            it("reports missing return tag on documented function with throws") {
                val code = """
                    /**
                     * I don't have a return tag
                     *
                     * @throws Exception Although I do have this one
                     */
                    fun hello(): String { TODO() }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
            }

            it("reports missing return tag on documented function with exception") {
                val code = """
                    /**
                     * I don't have a return tag
                     *
                     * @exception Exception Although I do have this one
                     */
                    fun hello(): String { TODO() }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
            }

            it("does not report missing return tag on undocumented function") {
                val code = """
                    fun hello(): String { TODO() }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            it("does not report when return tag is present") {
                val code = """
                    /**
                     * @return I have a return tag
                     */
                    fun hello(): String { TODO() }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }
        }

        context("Unit return") {
            it("does not report when return tag is absent when unit is returned") {
                val code = """
                   /**
                    * I don't have a return tag
                    */
                    fun hello(): Unit { TODO() }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }
        }

        context("Nothing return") {
            it("reports missing return tag on documented function") {
                val code = """
                    /**
                     * I don't have a return tag
                     */
                    fun hello(): Nothing { TODO() }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
            }

            it("does not report on documented function with throws") {
                val code = """
                    /**
                     * I don't have a return tag
                     *
                     * @throws Exception Although I do have this one
                     */
                    fun hello(): Nothing { TODO() }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            it("does not report on documented function with exception") {
                val code = """
                    /**
                     * I don't have a return tag
                     *
                     * @exception Exception Although I do have this one
                     */
                    fun hello(): Nothing { TODO() }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            it("does not report on undocumented function") {
                val code = """
                    fun hello(): Nothing { TODO() }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            it("does not report when return tag is present") {
                val code = """
                    /**
                     * @return I have a return tag
                     */
                    fun hello(): Nothing { TODO() }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }
        }
    }
})
