package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class InstanceOfCheckForExceptionSpec(val env: KotlinCoreEnvironment) {
    val subject = InstanceOfCheckForException()

    @Test
    fun `has is and as checks`() {
        val code = """
            fun x() {
                try {
                } catch(e: Exception) {
                    if (e is IllegalArgumentException || (e as IllegalArgumentException) != null) {
                        return
                    }
                }
            }
        """
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(2)
    }

    @Test
    fun `has nested is and as checks`() {
        val code = """
            fun x() {
                try {
                } catch(e: Exception) {
                    if (1 == 1) {
                        val b = e !is IllegalArgumentException || (e as IllegalArgumentException) != null
                    }
                }
            }
        """
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(2)
    }

    @Test
    fun `has no instance of check`() {
        val code = """
            fun x() {
                try {
                } catch(e: Exception) {
                    val s = ""
                    if (s is String || (s as String) != null) {
                        val other: Exception? = null
                        val b = other !is IllegalArgumentException || (other as IllegalArgumentException) != null
                    }
                }
            }
        """
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `has no checks for the subtype of an exception`() {
        val code = """
            interface I
            
            fun foo() {
                try {
                } catch(e: Exception) {
                    if (e is I || (e as I) != null) {
                    }
                }
            }
        """
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }
}
