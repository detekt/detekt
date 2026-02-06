package dev.detekt.rules.potentialbugs

import dev.detekt.api.Config
import dev.detekt.test.junit.KotlinCoreEnvironmentTest
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class ExitOutsideMainSpec(private val env: KotlinEnvironmentContainer) {
    private val subject = ExitOutsideMain(Config.empty)

    @Test
    fun `reports exitProcess used outside main()`() {
        val code = """
            import kotlin.system.exitProcess
            fun f() {
                exitProcess(0)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports System_exit used outside main()`() {
        val code = """
            fun f() {
                System.exit(0)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports Runtime_exit used outside main()`() {
        val code = """
            fun f() {
                Runtime.getRuntime().exit(0)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports Runtime_halt used outside main()`() {
        val code = """
            fun f() {
                Runtime.getRuntime().halt(0)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report exitProcess used in main()`() {
        val code = """
            import kotlin.system.exitProcess
            fun main() {
                exitProcess(0)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report System_exit used in main()`() {
        val code = """
            fun main() {
                System.exit(0)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
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
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports System_exit used in nested function in main()`() {
        val code = """
            fun main() {
                fun exit() {
                    System.exit(0)
                }
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }
}
