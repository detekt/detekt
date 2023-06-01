package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class RedundantExplicitTypeSpec(val env: KotlinCoreEnvironment) {
    val subject = RedundantExplicitType(Config.empty)

    @Test
    fun `reports explicit type for boolean`() {
        val code = """
            fun function() {
                val x: Boolean = true
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports explicit type for integer`() {
        val code = """
            fun function() {
                val x: Int = 3
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports explicit type for long`() {
        val code = """
            fun function() {
                val x: Long = 3L
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports explicit type for float`() {
        val code = """
            fun function() {
                val x: Float = 3.0f
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports explicit type for double`() {
        val code = """
            fun function() {
                val x: Double = 3.0
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports explicit type for char`() {
        val code = """
            fun function() {
                val x: Char = 'f'
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports explicit type for string template`() {
        val substitute = "\$x"
        val code = """
            fun function() {
                val x = 3
                val y: String = "$substitute"
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports explicit type for name reference expression`() {
        val code = """
            object Test
            
            fun foo() {
                val o: Test = Test
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports explicit type for call expression`() {
        val code = """
            interface Person {
                val firstName: String
            }
            
            class TallPerson(override val firstName: String, val height: Int): Person
            
            fun tallPerson() {
                val t: TallPerson = TallPerson("first", 3)
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report explicit type for call expression when type is an interface`() {
        val code = """
            interface Person {
                val firstName: String
            }
            
            class TallPerson(override val firstName: String, val height: Int): Person
            
            fun tallPerson() {
                val t: Person = TallPerson("first", 3)
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }
}
