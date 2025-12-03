package dev.detekt.rules.bugs

import dev.detekt.api.Config
import dev.detekt.test.KotlinEnvironmentContainer
import dev.detekt.test.junit.KotlinCoreEnvironmentTest
import dev.detekt.test.lintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class HasPlatformTypeSpec(private val env: KotlinEnvironmentContainer) {
    private val subject = HasPlatformType(Config.empty)

    @Test
    fun `reports when public function returns expression of platform type`() {
        val code = """
            class Person {
                fun apiCall() = System.getProperty("propertyName")
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report function when private`() {
        val code = """
            class Person {
                private fun apiCall() = System.getProperty("propertyName")
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report when public function returns expression of platform type and type explicitly declared`() {
        val code = """
            class Person {
                fun apiCall(): String = System.getProperty("propertyName")
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports when property initiated with platform type`() {
        val code = """
            class Person {
                val name = System.getProperty("name")
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report property when private`() {
        val code = """
            class Person {
                private val name = System.getProperty("name")
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report when property initiated with platform type and type explicitly declared`() {
        val code = """
            class Person {
                val name: String = System.getProperty("name")
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }
}
