package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CollapsibleIfStatementsSpec {
    val subject = CollapsibleIfStatements(Config.empty)

    @Test
    fun `reports if statements which can be merged`() {
        val code = """
            fun f() {
                if (true) {
                    if (1 == 1) {}
                    // a comment
                }
            }
        """
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `reports nested if statements which can be merged`() {
        val code = """
            fun f() {
                if (true) {
                    if (1 == 1) {
                        if (2 == 2) {}
                    }
                    println()
                }
            }
        """
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `does not report else-if`() {
        val code = """
            fun f() {
                if (true) {}
                else if (1 == 1) {
                    if (true) {}
                }
            }
        """
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report if-else`() {
        val code = """
            fun f() {
                if (true) {
                    if (1 == 1) {}
                } else {}
            }
        """
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report if-elseif-else`() {
        val code = """
            fun f() {
                if (true) {
                    if (1 == 1) {}
                } else if (false) {}
                else {}
            }
        """
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report if with statements in the if body`() {
        val code = """
            fun f() {
                if (true) {
                    if (1 == 1) ;
                    println()
                }
            }
        """
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report nested if-else`() {
        val code = """
            fun f() {
                if (true) {
                    if (1 == 1) {
                    } else {}
                }
            }
        """
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report nested if-elseif`() {
        val code = """
            fun f() {
                if (true) {
                    if (1 == 1) {
                    } else if (2 == 2) {}
                }
            }
        """
        assertThat(subject.compileAndLint(code)).isEmpty()
    }
}
