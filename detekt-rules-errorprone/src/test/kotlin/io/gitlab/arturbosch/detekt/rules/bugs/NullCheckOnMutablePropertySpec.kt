package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class NullCheckOnMutablePropertySpec(private val env: KotlinCoreEnvironment) {
    private val subject = NullCheckOnMutableProperty(Config.empty)

    @Test
    fun `should report a null-check on a mutable constructor property`() {
        val code = """
            class A(private var a: Int?) {
                fun foo() {
                    if (a != null) {
                        println(2 + a!!)
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `should report a null-check on a mutable property in non-initial clauses in an if-statement`() {
        val code = """
            class A(private var a: Int?, private val b: Int) {
                fun foo() {
                    if (b == 5 && a != null) {
                        println(2 + a!!)
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `should report a null-check on a mutable property used in the same if-statement`() {
        val code = """
            class A(private var a: Int?) {
                fun foo() {
                    if (a != null && a == 2) {
                        println("a is 2")
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `should report on a mutable property that is not subject to a double-bang`() {
        val code = """
            class A(private var a: Int?) {
                fun foo() {
                    if (a != null) {
                        println(a)
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `should report on a mutable property even if it is checked multiple times`() {
        val code = """
            class A(private var a: Int?) {
                fun foo() {
                    if (a != null) {
                        if (a == null) {
                            return
                        }
                        println(2 + a!!)
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `should not report when the checked property is not used afterwards`() {
        val code = """
            class A(private var a: Int?) {
                fun foo() {
                    if (a != null) {
                        println("'a' is not null")
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `should not report a null-check on a shadowed property`() {
        val code = """
            class A(private var a: Int?) {
                fun foo() {
                    val a = a
                    if (a != null) {
                        println(2 + a)
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `should not report a null-check on a non-mutable constructor property`() {
        val code = """
            class A(private val a: Int?) {
                fun foo() {
                    if (a != null) {
                        println(2 + a)
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `should report a null-check on a mutable class property`() {
        val code = """
            class A {
                private var a: Int? = 5
                fun foo() {
                    if (a != null) {
                        println(2 + a!!)
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `should not report a null-check on a val property`() {
        val code = """
            class A {
                private val a: Int? = 5
                fun foo() {
                    if (a != null) {
                        println(2 + a)
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `should report a null-check on a val property with a getter`() {
        val code = """
            import kotlin.random.Random
            
            class A {
                private val a: Int?
                    get() = genA()
                fun foo() {
                    if (a != null) {
                        println(2 + a!!)
                    }
                }
                private fun genA(): Int? {
                    val randInt = Random.nextInt()
                    return if (randInt % 2 == 0) randInt else null
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `should report a null-check conducted within an inner class`() {
        val code = """
            class A(private var a: Int?) {
                inner class B {
                    fun foo() {
                        if (a != null) {
                            println(2 + a!!)
                        }
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `should report an inner-class mutable property`() {
        val code = """
            class A(private val a: Int?) {
                inner class B(private var a: Int) {
                    fun foo() {
                        if (a != null) {
                            println(2 + a!!)
                        }
                    }
                }
            
                fun foo() {
                    if (a != null) {
                        println(2 + a)
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `should report a null-check on a mutable file property`() {
        val code = """
            private var a: Int? = 5
            
            class A {
                fun foo() {
                    if (a != null) {
                        println(2 + a!!)
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `should not report a null-check on a non-mutable file property`() {
        val code = """
            private val a: Int? = 5
            
            class A {
                fun foo() {
                    if (a != null) {
                        println(2 + a)
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `should report a null-check when null is the first element in the if-statement`() {
        val code = """
            class A(private var a: Int?) {
                fun foo() {
                    if (null != a) {
                        println(2 + a!!)
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `should not report when the if-expression has no explicit null value`() {
        val code = """
            class A(private var a: Int?) {
                fun foo() {
                    val otherA = null
                    if (a != otherA) {
                        println(2 + a!!)
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `should not report a null-check on a function`() {
        val code = """
            class A {
                private fun otherFoo(): Int? {
                    return null
                }
                fun foo() {
                    if (otherFoo() != null) {
                        println(2 + otherFoo()!!)
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }
}
