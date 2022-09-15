package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Suppress("ClassName")
@KotlinCoreEnvironmentTest
class UseNamedArgumentForConstantsSpec {

    @Nested
    inner class `Verify function calls` {
        @Test
        fun `Verify detect need to use named arguments`() {
            val code = """
            fun foo(a: Boolean, b: Boolean, c : Boolean)
            
            fun main() {
                foo(true, false, true)
            }
        """.trimIndent()

            assertThat(UseNamedArgumentForConstants().compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `Verify detect custom config`() {
            val code = """
            fun foo(a: Boolean, b: Boolean, c : Boolean)
            
            fun main() {
                foo(true, false, true)
            }
        """.trimIndent()

            assertThat(UseNamedArgumentForConstants(TestConfig(mapOf(UseNamedArgumentForConstants.THRESHOLD to 4))).compileAndLint(code))
                .hasSize(0)
            assertThat(UseNamedArgumentForConstants(TestConfig(mapOf(UseNamedArgumentForConstants.THRESHOLD to 1))).compileAndLint(code))
                .hasSize(1)
        }

        @Test
        fun `Verify not detect report use named arguments`() {
            val code = """
            fun foo(a: Int, b: Int, c : Int)
            
            fun main() {
                foo(a = 10, b = 20, c = 30)
            }
        """.trimIndent()

            assertThat(UseNamedArgumentForConstants().compileAndLint(code)).hasSize(0)
        }
    }

    @Nested
    inner class `boolean cases` {
        @Test
        fun `Verify detect report use named arguments`() {
            val code = """
            data class Test(val a: Boolean, val b: Boolean, val c : Boolean)
            
            fun main() {
                val test = Test(true, false, true)
            }
        """.trimIndent()

            assertThat(UseNamedArgumentForConstants().compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `Verify detect report use named arguments two arguments are named`() {
            val code = """
            data class Test(val a: Boolean, val b: Boolean, val c : Boolean)
            
            fun main() {
                val test = Test(a = true, b = false, true)
            }
        """.trimIndent()

            assertThat(UseNamedArgumentForConstants().compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `Verify no detect report all arguments has name`() {
            val code = """
            data class Test(val a: Boolean, val b: Boolean, val c : Boolean)
            
            fun main() {
                val test = Test(a = true, b = false, c = true)
            }
        """.trimIndent()

            assertThat(UseNamedArgumentForConstants().compileAndLint(code)).hasSize(0)
        }

        @Test
        fun `Verify detect report use named arguments one argument set`() {
            val code = """
            data class Test(val a: Boolean, val b: Boolean)
            
            fun main() {
                val test = Test(a = false, true)
            }
        """.trimIndent()

            assertThat(UseNamedArgumentForConstants().compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `Verify detect not report use named arguments when value provide by properties`() {
            val code = """
            data class Test(val a: Boolean, val b: Boolean)
            
            fun main() {
                val a = true
                val b = false
                val test = Test(a, b)
            }
        """.trimIndent()

            assertThat(UseNamedArgumentForConstants().compileAndLint(code)).hasSize(0)
        }

        @Test
        fun `Verify detect custom config`() {
            val code = """
            data class Test(val a: Boolean, val b: Boolean, val c : Boolean)
            
            fun main() {
                val test = Test(true, false, true)
            }
        """.trimIndent()

            assertThat(UseNamedArgumentForConstants(TestConfig(mapOf(UseNamedArgumentForConstants.THRESHOLD to 4))).compileAndLint(code))
                .hasSize(0)
            assertThat(UseNamedArgumentForConstants(TestConfig(mapOf(UseNamedArgumentForConstants.THRESHOLD to 1))).compileAndLint(code))
                .hasSize(1)
        }
    }

    @Nested
    inner class `int cases` {
        @Test
        fun `Verify detect report use named arguments`() {
            val code = """
            data class Test(val a: Int, val b: Int, val c : Int)
            
            fun main() {
                val test = Test(10, 20, 30)
            }
        """.trimIndent()

            assertThat(UseNamedArgumentForConstants().compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `Verify not detect report use named arguments`() {
            val code = """
            data class Test(val a: Int, val b: Int, val c : Int)
            
            fun main() {
                val test = Test(a = 10, b = 20, c = 30)
            }
        """.trimIndent()

            assertThat(UseNamedArgumentForConstants().compileAndLint(code)).hasSize(0)
        }

        @Test
        fun `Verify detect report use named arguments one argument set`() {
            val code = """
            data class Test(val a: Int, val b: Int)
            
            fun main() {
                val test = Test(a = 10, 20)
            }
        """.trimIndent()

            assertThat(UseNamedArgumentForConstants().compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `Verify detect not report use named arguments when value provide by properties`() {
            val code = """
            data class Test(val a: Int, val b: Int)
            
            fun main() {
                val a = 10
                val b = 20
                val test = Test(a, b)
            }
        """.trimIndent()

            assertThat(UseNamedArgumentForConstants().compileAndLint(code)).hasSize(0)
        }
    }

    @Test
    fun `Verify detect report use named arguments two has same type`() {
        val code = """
            data class Test(val a: Boolean, val b: Int, val c: Boolean)
            
            fun main() {
                val test = Test(true, 10, false)
            }
        """.trimIndent()

        assertThat(UseNamedArgumentForConstants().compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `Verify detect not report use named arguments arguments has different types`() {
        val code = """
            data class Test(val a: Boolean, val b: Int)
            
            fun main() {
                val test = Test(true, 10)
            }
        """.trimIndent()

        assertThat(UseNamedArgumentForConstants().compileAndLint(code)).hasSize(0)
    }

    @Test
    fun `Verify detect not report use named arguments arguments has different types object plus primitive`() {
        val code = """
            class Foo
            
            data class Test(val a: Foo, val b: Int)
            
            fun main() {
                val test = Test(Foo(), 10)
            }
        """.trimIndent()

        assertThat(UseNamedArgumentForConstants().compileAndLint(code)).hasSize(0)
    }

    @Test
    fun `Verify no argument no constants argument`() {
        val code = """
            class Foo
            data class Test(val a: Foo, val b: Foo, val c : Foo)
            
            fun main() {
                val test = Test(Foo(), Foo(), Foo())
            }
        """.trimIndent()

        assertThat(UseNamedArgumentForConstants().compileAndLint(code)).hasSize(0)
    }
}
