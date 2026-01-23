package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.test.junit.KotlinCoreEnvironmentTest
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class RedundantExplicitTypeSpec(val env: KotlinEnvironmentContainer) {
    val subject = RedundantExplicitType(Config.Empty)

    @Test
    fun `reports explicit type for boolean`() {
        val code = """
            fun function() {
                val x: Boolean = true
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports explicit type for integer`() {
        val code = """
            fun function() {
                val x: Int = 3
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports explicit type for long`() {
        val code = """
            fun function() {
                val x: Long = 3L
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports explicit type for float`() {
        val code = """
            fun function() {
                val x: Float = 3.0f
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports explicit type for double`() {
        val code = """
            fun function() {
                val x: Double = 3.0
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports explicit type for char`() {
        val code = """
            fun function() {
                val x: Char = 'f'
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
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
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports explicit type for name reference expression`() {
        val code = """
            object Test
            
            fun foo() {
                val o: Test = Test
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
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
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
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
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }
}
