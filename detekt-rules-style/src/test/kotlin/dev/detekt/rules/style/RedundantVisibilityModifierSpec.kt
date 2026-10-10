package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.test.FakeLanguageVersionSettings
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.lint
import org.jetbrains.kotlin.config.ExplicitApiMode
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class RedundantVisibilityModifierSpec {
    val subject = RedundantVisibilityModifier(Config.empty)

    @Test
    fun `does not report overridden function of abstract class with public modifier`() {
        val code = """
            abstract class A {
                abstract protected fun f()
            }
            
            class Test : A() {
                override public fun f() {}
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report overridden function of abstract class without public modifier`() {
        val code = """
            abstract class A {
                abstract protected fun f()
            }
            
            class Test : A() {
                override fun f() {}
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report overridden function of interface`() {
        val code = """
            interface A {
                fun f()
            }
            
            class Test : A {
                override public fun f() {}
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `reports public function in class`() {
        val code = """
            class Test {
                public fun f() {}
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `does not report function in class without modifier`() {
        val code = """
            class Test {
                fun f() {}
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `reports public class`() {
        val code = """
            public class Test {
                fun f() {}
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports interface with public modifier`() {
        val code = """
            public interface Test {
                public fun f()
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(2)
    }

    @Test
    fun `reports field with public modifier`() {
        val code = """
            class Test {
                public val str : String = "test"
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `does not report field without public modifier`() {
        val code = """
            class Test {
                val str : String = "test"
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report overridden field without public modifier`() {
        val code = """
            abstract class A {
                abstract val test: String
            }
            
            class B : A() {
                override val test: String = "valid"
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report overridden field with public modifier`() {
        val code = """
            abstract class A {
                abstract val test: String
            }
            
            class B : A() {
                override public val test: String = "valid"
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `reports internal modifier on nested class in private object`() {
        val code = """
            private object A {
                internal class InternalClass
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports internal modifier on function declaration in private object`() {
        val code = """
            private object A {
                internal fun internalFunction() {}
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Nested
    inner class `internal constructor` {
        @Test
        fun `reports internal primary constructor of internal class`() {
            val code = """
                internal class A internal constructor(val a: Int)
            """.trimIndent()
            assertThat(subject.lint(code)).singleElement()
                .hasMessage(
                    "The `internal` modifier on the constructor of A is redundant because the class is already internal."
                )
        }

        @Test
        fun `reports internal secondary constructor of internal class`() {
            val code = """
                internal class A(val a: Int) {
                    internal constructor() : this(0)
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `reports internal constructor of internal nested class`() {
            val code = """
                internal class A {
                    internal class B internal constructor()
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `reports internal constructor of private class`() {
            val code = """
                private class A internal constructor()
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `does not report constructor without modifier of internal class`() {
            val code = """
                internal class A constructor(val a: Int) {
                    constructor() : this(0)
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report internal constructor of public class`() {
            val code = """
                class A internal constructor(val a: Int) {
                    internal constructor() : this(0)
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report private constructor of internal class`() {
            val code = """
                internal class A private constructor()
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report internal constructor of public class nested in internal class`() {
            val code = """
                internal class A {
                    class B internal constructor()
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report PublishedApi internal constructor of internal class`() {
            val code = """
                @PublishedApi
                internal class A @PublishedApi internal constructor()
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `reports internal constructor of internal class with other annotation`() {
            val code = """
                internal class A @Deprecated("") internal constructor()
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `reports internal constructor of local class`() {
            val code = """
                fun f() {
                    class A internal constructor()
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `does not report internal constructor of internal sealed class`() {
            val code = """
                internal sealed class A internal constructor()
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report internal top-level declarations`() {
            val code = """
                internal class A
                internal fun f() {}
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report internal members of internal class`() {
            val code = """
                internal class A {
                    internal val a: Int = 0
                    internal fun f() {}
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `Explicit API mode` {
        val code = """
            public class A {
                public fun f() {}
            }
        """.trimIndent()

        @Test
        fun `does not report public modifiers in strict mode`() {
            val findings = subject.lint(code, FakeLanguageVersionSettings(ExplicitApiMode.STRICT))
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report public modifiers in warning mode`() {
            val findings = subject.lint(code, FakeLanguageVersionSettings(ExplicitApiMode.WARNING))
            assertThat(findings).isEmpty()
        }

        @Test
        fun `reports public modifiers in disabled mode`() {
            val findings = subject.lint(code, FakeLanguageVersionSettings(ExplicitApiMode.DISABLED))
            assertThat(findings).hasSize(2)
        }
    }
}
