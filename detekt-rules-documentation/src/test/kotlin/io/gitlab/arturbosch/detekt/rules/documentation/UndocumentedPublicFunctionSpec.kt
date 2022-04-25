package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class UndocumentedPublicFunctionSpec {
    val subject = UndocumentedPublicFunction()

    @Test
    fun `reports undocumented public functions`() {
        val code = """
            fun noComment1() {}
        """
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `reports undocumented public function in object`() {
        val code = """
            object Test {
                fun noComment1() {}
            }
        """
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `reports undocumented public function in nested object`() {
        val code = """
            class Test {
                object Test2 {
                    fun noComment1() {}
                }
            }
        """
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `reports undocumented public functions in companion object`() {
        val code = """
            class Test {
                companion object {
                    fun noComment1() {}
                    public fun noComment2() {}
                }
            }
        """
        assertThat(subject.compileAndLint(code)).hasSize(2)
    }

    @Test
    fun `reports undocumented public function in an interface`() {
        val code = """
            interface Test {
                fun noComment1()
            }
        """
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `does not report documented public function`() {
        val code = """
            /**
             * Comment
             */
            fun commented1() {}
        """
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report documented public function in class`() {
        val code = """
        class Test {
            /**
            *
            */
            fun commented() {}
        }
        """
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report undocumented internal and private function`() {
        val code = """
            class Test {
                internal fun no1(){}
                private fun no2(){}
            }
        """
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report undocumented nested function`() {
        val code = """
            /**
             * Comment
             */
            fun commented() {
                fun iDontNeedDoc() {}
            }
        """
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report public functions in internal class`() {
        val code = """
            internal class NoComments {
                fun nope1() {}
                public fun nope2() {}
            }
        """
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report public functions in private class`() {
        val code = """
            private class NoComments {
                fun nope1() {}
                public fun nope2() {}
            }
        """
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report public functions in private object`() {
        val code = """
            private object Test {
                fun noComment1() {}
            }
        """
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Nested
    inner class `nested class` {
        @Test
        fun `does not report public functions in internal interface`() {
            val code = """
                internal interface Foo {
                    interface Bar {
                        fun f() {
                        }
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        @Test
        fun `does not report public functions in private class`() {
            val code = """
                class Foo {
                    private class Bar {
                        class Baz {
                            fun f() {
                            }
                        }
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        @Test
        fun `does not report public functions in private object`() {
            val code = """
                private object Foo {
                    class Bar {
                        fun f() {
                        }
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
}
