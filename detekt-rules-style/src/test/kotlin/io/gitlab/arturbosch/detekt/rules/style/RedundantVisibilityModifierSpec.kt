package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.FakeCompilerResources
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
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
        assertThat(subject.compileAndLint(code)).isEmpty()
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
        assertThat(subject.compileAndLint(code)).isEmpty()
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
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `reports public function in class`() {
        val code = """
            class Test {
                public fun f() {}
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `does not report function in class without modifier`() {
        val code = """
            class Test {
                fun f() {}
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `reports public class`() {
        val code = """
            public class Test {
                fun f() {}
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `reports interface with public modifier`() {
        val code = """
            public interface Test {
                public fun f()
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).hasSize(2)
    }

    @Test
    fun `reports field with public modifier`() {
        val code = """
            class Test {
                public val str : String = "test"
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `does not report field without public modifier`() {
        val code = """
            class Test {
                val str : String = "test"
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
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
        assertThat(subject.compileAndLint(code)).isEmpty()
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
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `reports internal modifier on nested class in private object`() {
        val code = """
            private object A {
                internal class InternalClass
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `reports internal modifier on function declaration in private object`() {
        val code = """
            private object A {
                internal fun internalFunction() {}
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Nested
    inner class `Explicit API mode` {
        val code = """
            public class A() {
                fun f() {}
            }
        """.trimIndent()

        @Test
        fun `does not report public function in class if explicit API mode is set to strict`() {
            val findings = subject.compileAndLint(code, FakeCompilerResources(ExplicitApiMode.STRICT))
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report public function in class if explicit API mode is set to warning`() {
            val findings = subject.compileAndLint(code, FakeCompilerResources(ExplicitApiMode.WARNING))
            assertThat(findings).isEmpty()
        }

        @Test
        fun `reports public function in class if explicit API mode is disabled`() {
            val findings = subject.compileAndLint(code, FakeCompilerResources(ExplicitApiMode.DISABLED))
            assertThat(findings).hasSize(1)
        }
    }
}
