package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private const val IGNORE_OVERRIDDEN = "ignoreOverridden"
private const val IGNORE_USAGE_IN_GENERICS = "ignoreUsageInGenerics"

@KotlinCoreEnvironmentTest
class ForbiddenVoidSpec(val env: KotlinCoreEnvironment) {
    val subject = ForbiddenVoid(Config.empty)

    @Test
    fun `should report all Void type usage`() {
        val code = """
            lateinit var c: () -> Void
            
            fun method(param: Void) {
                val a: Void? = null
                val b: Void = null!!
            }
        """.trimIndent()

        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(4)
    }

    @Test
    fun `should not report Void class literal`() {
        val code = """
            val clazz = java.lang.Void::class
            val klass = Void::class
        """.trimIndent()

        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report when functions or classes are called 'Void'`() {
        val code = """
            class Void {
                fun void() {}
                val void = "string"
            }
            enum class E {
                Void;
            }
            abstract class Test {
                fun myFun2(): E = E.Void
                abstract fun myFun(): Void
            }
        """.trimIndent()

        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Nested
    inner class `ignoreOverridden is enabled` {

        val config = TestConfig(IGNORE_OVERRIDDEN to "true")

        @Test
        fun `should not report Void in overriding function declarations`() {
            val code = """
                abstract class A {
                    @Suppress("ForbiddenVoid")
                    abstract fun method(param: Void) : Void
                }
                
                class B : A() {
                    override fun method(param: Void) : Void {
                        throw IllegalStateException()
                    }
                }
            """.trimIndent()

            val findings = ForbiddenVoid(config).compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not report Void in overriding function declarations with parameterized types`() {
            val code = """
                class Foo<T> {}
                
                abstract class A {
                    @Suppress("ForbiddenVoid")
                    abstract fun method(param: Foo<Void>) : Foo<Void>
                }
                
                class B : A() {
                    override fun method(param: Foo<Void>) : Foo<Void> {
                        throw IllegalStateException()
                    }
                }
            """.trimIndent()

            val findings = ForbiddenVoid(config).compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should report Void in body of overriding function even`() {
            val code = """
                abstract class A {
                    abstract fun method(param: String)
                }
                
                class B : A() {
                    override fun method(param: String) {
                        val a: Void? = null
                    }
                }
            """.trimIndent()

            val findings = ForbiddenVoid(config).compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should report Void in not overridden function declarations`() {
            val code = """
                fun method(param: Void) : Void {
                    return param
                }
            """.trimIndent()

            val findings = ForbiddenVoid(config).compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(2)
        }
    }

    @Nested
    inner class `ignoreUsageInGenerics is enabled` {

        val config = TestConfig(IGNORE_USAGE_IN_GENERICS to "true")

        @Test
        fun `should not report Void in generic type declaration`() {
            val code = """
                interface A<T>
                
                class B {
                    fun method(): A<Void>? = null
                }
                
                class C(private val b: B) {
                    fun method() {
                        val a: A<Void>? = b.method()
                    }
                }
                
                class D : A<Void>
            """.trimIndent()

            val findings = ForbiddenVoid(config).compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not report Void in nested generic type definition`() {
            val code = """
                interface A<T>
                interface B<T>
                class C : A<B<Void>>
            """.trimIndent()

            val findings = ForbiddenVoid(config).compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not report Void in definition with multiple generic parameters`() {
            val code = """
                val foo = mutableMapOf<Int, Void>()
            """.trimIndent()

            val findings = ForbiddenVoid(config).compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should report non-generic Void type usage`() {
            val code = """
                lateinit var c: () -> Void
                
                fun method(param: Void) {
                    val a: Void? = null
                    val b: Void = null!!
                }
            """.trimIndent()

            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(4)
        }
    }
}
