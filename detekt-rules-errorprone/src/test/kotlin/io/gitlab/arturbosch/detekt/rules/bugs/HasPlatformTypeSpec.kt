package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class HasPlatformTypeSpec(private val env: KotlinCoreEnvironment) {
    private val subject = HasPlatformType(Config.empty)

    @Test
    fun `reports when public function returns expression of platform type`() {
        val code = """
            class Person {
                fun apiCall() = System.getProperty("propertyName")
            }
        """
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report function when private`() {
        val code = """
            class Person {
                private fun apiCall() = System.getProperty("propertyName")
            }
        """
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report when public function returns expression of platform type and type explicitly declared`() {
        val code = """
            class Person {
                fun apiCall(): String = System.getProperty("propertyName")
            }
        """
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports when property initiated with platform type`() {
        val code = """
            class Person {
                val name = System.getProperty("name")
            }
        """
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report property when private`() {
        val code = """
            class Person {
                private val name = System.getProperty("name")
            }
        """
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report when property initiated with platform type and type explicitly declared`() {
        val code = """
            class Person {
                val name: String = System.getProperty("name")
            }
        """
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }
}
