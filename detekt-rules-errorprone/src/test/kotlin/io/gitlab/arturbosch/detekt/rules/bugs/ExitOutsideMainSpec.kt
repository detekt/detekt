package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class ExitOutsideMainSpec(private val env: KotlinCoreEnvironment) {
    private val subject = ExitOutsideMain()

    @Test
    fun `reports exitProcess used outside main()`() {
        val code = """
            import kotlin.system.exitProcess
            fun f() {
                exitProcess(0)
            }
        """
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports System_exit used outside main()`() {
        val code = """
            fun f() {
                System.exit(0)
            }
        """
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report exitProcess used in main()`() {
        val code = """
            import kotlin.system.exitProcess
            fun main() {
                exitProcess(0)
            }
        """
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report System_exit used in main()`() {
        val code = """
            fun main() {
                System.exit(0)
            }
        """
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports exitProcess used in nested function in main()`() {
        val code = """
            import kotlin.system.exitProcess
            fun main() {
                fun exit() {
                    exitProcess(0)
                }
            }
        """
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports System_exit used in nested function in main()`() {
        val code = """
            fun main() {
                fun exit() {
                    System.exit(0)
                }
            }
        """
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }
}
