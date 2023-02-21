package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private const val SEARCH_PROTECTED_PROPERTY = "searchProtectedProperty"

class UndocumentedPublicPropertySpec {
    val subject = UndocumentedPublicProperty()

    @Test
    fun `reports undocumented public property`() {
        val code = "val a = 1"
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `reports undocumented public property in objects`() {
        val code = """
            object Test {
                val a = 1
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `reports undocumented public property in nested objects`() {
        val code = """
            class Test {
                object NestedTest {
                    val a = 1
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `reports undocumented public properties in companion object`() {
        val code = """
            class Test {
                companion object {
                    val a = 1
                    public val b = 1
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).hasSize(2)
    }

    @Test
    fun `reports undocumented public property in an interface`() {
        val code = """
            interface Test {
                val a: Int
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `reports undocumented public properties in a primary constructor`() {
        val code = "class Test(val a: Int)"
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `reports undocumented public property in a primary constructor`() {
        val code = "/* comment */ class Test(val a: Int)"
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `does not report documented public property`() {
        val code = """
            /**
             * Comment
             */
            val a = 1
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report documented public property in class`() {
        val code = """
            class Test {
                /**
                 * Comment
                 */
                val a = 1
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report undocumented, public and overridden property in class`() {
        val code = """
            interface I {
                /**
                 * Comment
                 */
                val a: Int
            }
            
            class Test : I {
                override val a = 1
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report undocumented internal and private property`() {
        val code = """
            class Test {
                internal val a = 1
                private val b = 1
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report local variables`() {
        val code = """
            fun commented(x: Int) {
                var a = x
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report public properties in internal class`() {
        val code = """
            internal class NoComments {
                public val a = 1
                val b = 1
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report public properties in private class`() {
        val code = """
            private class NoComments {
                public val a = 1
                val b = 1
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report properties in a secondary constructor`() {
        val code = """
            class Test() {
                constructor(a: Int) : this()
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report undocumented non-public properties in a primary constructor`() {
        val code = """
            class Test1(internal val a: Int)
            class Test2(b: Int)
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report undocumented public properties in a primary constructor for an internal class`() {
        val code = "internal class Test(val a: Int)"
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report documented public properties in a primary constructor`() {
        val code = """
            /**
             * @property a int1
             * [b] int2
             * @property [c] int3
             * @param d int4
             */
            class Test(
                val a: Int,
                val b: Int,
                val c: Int,
                val d: Int,
                /**
                 * Some docs.
                 */
                val e: Int
            )
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report undocumented public properties in private object`() {
        val code = """
            private object Test {
                val a = 1
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report undocumented protected properties by default`() {
        val code = """
            open class Test {
                protected val a = 1
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `reports undocumented protected properties if configured`() {
        val code = """
            open class Test {
                protected val a = 1
            }
        """.trimIndent()
        val subject = UndocumentedPublicProperty(TestConfig(mapOf(SEARCH_PROTECTED_PROPERTY to "true")))
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Nested
    inner class `public properties in nested classes` {

        @Test
        fun `reports undocumented public properties in nested classes`() {
            val code = """
            class Outer {
                class Inner {
                    val i = 0
                    
                    class InnerInner {
                        val ii = 0
                    }
                }
            }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).hasSize(2)
        }

        @Test
        fun `reports undocumented public properties in inner classes`() {
            val code = """
            class Outer {
                inner class Inner {
                    val i = 0
                }
            }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `reports undocumented public properties in classes nested in objects`() {
            val code = """
            object Outer {
                class Inner {
                    val i = 0
                }
            }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `does not report undocumented and non-public properties in nested classes`() {
            val code = """
            internal class Outer {
                class Inner {
                    val i = 0
                }
            }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        @Test
        fun `does not report undocumented and non-public properties in inner classes`() {
            val code = """
            internal class Outer {
                inner class Inner {
                    val i = 0
                }
            }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }

    @Nested
    inner class `public properties in primary constructors inside nested classes` {

        @Test
        fun `reports undocumented public properties in nested classes`() {
            val code = """
            class Outer(val a: Int) {
                class Inner(val b: Int) {
                    class InnerInner(val c: Int)
                }
            }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).hasSize(3)
        }

        @Test
        fun `reports undocumented public properties in inner classes`() {
            val code = """
            class Outer(val a: Int) {
                inner class Inner(val b: Int)
            }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).hasSize(2)
        }

        @Test
        fun `reports undocumented public properties in classes nested in objects`() {
            val code = """
            object Outer {
                class Inner(val a: Int)
            }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `does not report undocumented and non-public properties in nested classes`() {
            val code = """
            internal class Outer(val a: Int) {
                class Inner(val b: Int)
            }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        @Test
        fun `does not report undocumented and non-public properties in inner classes`() {
            val code = """
            internal class Outer(val a: Int) {
                inner class Inner(val b: Int)
            }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
}
