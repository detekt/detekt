package io.gitlab.arturbosch.detekt.rules.style

import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.api.CompilerResources
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.jetbrains.kotlin.config.AnalysisFlag
import org.jetbrains.kotlin.config.AnalysisFlags
import org.jetbrains.kotlin.config.ApiVersion
import org.jetbrains.kotlin.config.ExplicitApiMode
import org.jetbrains.kotlin.config.LanguageFeature
import org.jetbrains.kotlin.config.LanguageVersion
import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowValueFactoryImpl
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class RedundantVisibilityModifierRuleSpec {
    val subject = RedundantVisibilityModifierRule(Config.empty)

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
    fun `should ignore the issue by alias suppression`() {
        val code = """
            class Test {
                @Suppress("RedundantVisibilityModifier")
                public fun f() {}
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

        val code = compileContentForTest(
            """
                public class A() {
                    fun f()
                }
            """.trimIndent()
        )

        val rule = RedundantVisibilityModifierRule(Config.empty)

        private fun fakeCompilerResources(mode: ExplicitApiMode): CompilerResources {
            val languageVersionSettings = object : LanguageVersionSettings {
                override val apiVersion: ApiVersion get() = error("This is a fake")

                override val languageVersion: LanguageVersion get() = error("This is a fake")

                override fun getFeatureSupport(feature: LanguageFeature) = error("This is a fake")

                override fun <T> getFlag(flag: AnalysisFlag<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return when (flag) {
                        AnalysisFlags.explicitApiMode -> mode as T
                        else -> error("This is a fake")
                    }
                }

                override fun isPreRelease() = error("This is a fake")
            }
            return CompilerResources(languageVersionSettings, DataFlowValueFactoryImpl(languageVersionSettings))
        }

        @Test
        fun `does not report public function in class if explicit API mode is set to strict`() {
            rule.visitFile(code, compilerResources = fakeCompilerResources(ExplicitApiMode.STRICT))
            assertThat(rule.findings).isEmpty()
        }

        @Test
        fun `reports public function in class if explicit API mode is disabled`() {
            rule.visitFile(code, compilerResources = fakeCompilerResources(ExplicitApiMode.DISABLED))
            assertThat(rule.findings).hasSize(1)
        }

        @Test
        fun `reports public function in class if compiler resources are not available`() {
            rule.visitFile(code, compilerResources = null)
            assertThat(rule.findings).hasSize(1)
        }
    }
}
