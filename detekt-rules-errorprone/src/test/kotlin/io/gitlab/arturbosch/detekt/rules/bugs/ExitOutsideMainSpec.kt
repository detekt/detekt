package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object ExitOutsideMainSpec : Spek({
    setupKotlinEnvironment()

    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { ExitOutsideMain() }

    describe("ExitOutsideMainSpec rule") {

        it("reports exitProcess used outside main()") {
            val code = """
                import kotlin.system.exitProcess
                fun f() {
                    exitProcess(0)
                }"""
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("reports System.exit used outside main()") {
            val code = """
                fun f() {
                    System.exit(0)
                }"""
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("does not report exitProcess used in main()") {
            val code = """
                import kotlin.system.exitProcess
                fun main() {
                    exitProcess(0)
                }"""
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("does not report System.exit used in main()") {
            val code = """
                fun main() {
                    System.exit(0)
                }"""
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("reports exitProcess used in nested function in main()") {
            val code = """
                import kotlin.system.exitProcess
                fun main() {
                    fun exit() {
                        exitProcess(0)
                    }
                }"""
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("reports System.exit used in nested function in main()") {
            val code = """
                fun main() {
                    fun exit() {
                        System.exit(0)
                    }
                }"""
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }
    }
})
