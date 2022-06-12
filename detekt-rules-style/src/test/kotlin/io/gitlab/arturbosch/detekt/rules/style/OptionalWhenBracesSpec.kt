package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class OptionalWhenBracesSpec {
    val subject = OptionalWhenBraces()

    @Test
    fun `does not report necessary braces`() {
        val code = """
            fun x() {
                when (1) {
                    1 -> print(1)
                    2 -> {
                        print(2)
                        print(2)
                    }
                    else -> {
                        // a comment
                        println()
                    }
                }
            }
        """
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `reports unnecessary braces`() {
        val code = """
            fun x() {
                when (1) {
                    1 -> { print(1) }
                    else -> println()
                }
            }
        """
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `reports unnecessary braces for nested when`() {
        val code = """
            import kotlin.random.Random
            
            fun main() {
                when(Random.nextBoolean()) {
                    true -> {
                        when(Random.nextBoolean()) {
                            true -> {
                                println("true")
                            }
                            false -> {
                                println("false")
                            }
                        }
                        println("end")
                    }
                    false -> println("false")
                }
            }
        """
        assertThat(subject.compileAndLint(code))
            .hasSize(2)
            .hasSourceLocations(SourceLocation(7, 17), SourceLocation(10, 17))
    }

    @Test
    fun `reports unnecessary braces when the single statement has comments inside`() {
        val code = """
            fun test(i: Int) {
                when {
                    else -> {
                        when (i) {
                            // foo
                            1 -> println(1)
                            // bar
                            else -> println(2)
                        }
                    }
                }
            }
        """
        assertThat(subject.compileAndLint(code))
            .hasSize(1)
            .hasSourceLocations(SourceLocation(3, 9))
    }

    @Nested
    inner class `the statement is a lambda expression` {
        @Test
        fun `does not report if the lambda has no arrow`() {
            val code = """
                fun test(b: Boolean): (Int) -> Int {
                    return when (b) {
                        true -> { { it + 100 } }
                        false -> { { it + 200  } }
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        @Test
        fun `reports if the lambda has an arrow`() {
            val code = """
                fun test(b: Boolean): (Int) -> Int {
                    return when (b) {
                        true -> { { i -> i + 100 } }
                        false -> { { i -> i + 200  } }
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(2)
        }
    }
}
