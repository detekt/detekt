package io.gitlab.arturbosch.detekt.rules.exceptions

import io.github.detekt.test.utils.KotlinEnvironmentContainer
import dev.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.lintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class InstanceOfCheckForExceptionSpec(private val env: KotlinEnvironmentContainer) {
    val subject = InstanceOfCheckForException(Config.empty)

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
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(2)
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
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(2)
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
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
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
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }
}
