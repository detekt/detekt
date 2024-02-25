package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class MissingSuperCallSpec(private val env: KotlinCoreEnvironment) {
    private val subject = MissingSuperCall(Config.empty)

    @Test
    fun `super method has CallSuper annotation`() {
        val code = """
            package androidx.annotation
        
            annotation class CallSuper
            
            open class ParentClass {
                @CallSuper
                open fun someMethod(arg: Int) {
                }
            }
            
            class MyClass : ParentClass() {
                override fun someMethod(arg: Int) {
                }
            }
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    fun `super method has OverridingMethodsMustInvokeSuper annotation`() {
        val code = """
            package javax.annotation
        
            annotation class OverridingMethodsMustInvokeSuper
            
            open class ParentClass {
                @OverridingMethodsMustInvokeSuper
                open fun someMethod(arg: Int) {
                }
            }
            
            class MyClass : ParentClass() {
                override fun someMethod(arg: Int) {
                }
            }
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    fun `super method has user defined annotation`() {
        val subject = MissingSuperCall(
            TestConfig("mustInvokeSuperAnnotations" to listOf("p.Ann"))
        )

        val code = """
            package p
            annotation class Ann
            open class Foo {
                open fun x() {}
            }
            open class Bar: Foo() {
                @Ann
                override fun x() {
                    super.x()
                    println("Bar")
                }
            }
            class Baz: Bar() {
                override fun x() {
                    println("Baz")
                }
            }
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    fun `super super methods has the annotation`() {
        val subject = MissingSuperCall(
            TestConfig("mustInvokeSuperAnnotations" to listOf("Ann"))
        )

        val code = """
            annotation class Ann
            open class Foo {
                @Ann
                open fun x() {}
            }
            open class Bar: Foo() {
                override fun x() {
                    super.x()
                    println("Bar")
                }
            }
            class Baz: Bar() {
                override fun x() {
                    println("Baz")
                }
            }
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    fun `super methods has no annotation`() {
        val subject = MissingSuperCall(
            TestConfig("mustInvokeSuperAnnotations" to listOf("Ann"))
        )

        val code = """
            annotation class Ann
            open class Foo {
                open fun x() {}
            }
            class Bar: Foo() {
                override fun x() {
                    println("Bar")
                }
            }
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).isEmpty()
    }

    @Test
    fun `overriding method calls super method`() {
        val subject = MissingSuperCall(
            TestConfig("mustInvokeSuperAnnotations" to listOf("foo.Bar"))
        )

        val code = """
            package foo
        
            annotation class Bar
            
            open class ParentClass {
                @Bar
                open fun someMethod(arg: Int) {
                }
                @Bar
                open fun someMethod(b: Boolean) {
                }
            }
            
            class MyClass : ParentClass() {
                override fun someMethod(arg: Int) {
                    super.someMethod(arg)
                    println()
                }
                override fun someMethod(b: Boolean) {
                    if (b) {
                        super.someMethod(b)
                    }
                    println()
                }
            }
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).isEmpty()
    }

    @Test
    fun `overriding method calls super method with explicit type`() {
        val subject = MissingSuperCall(
            TestConfig("mustInvokeSuperAnnotations" to listOf("Ann"))
        )

        val code = """
            annotation class Ann
            interface A {
                @Ann
                fun f() {}
            }
            interface B {
                fun f() {}
            }
            class X : A, B {
                override fun f() {
                    super<A>.f()
                }
            }
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).isEmpty()
    }

    @Test
    fun `overriding method calls another super method`() {
        val subject = MissingSuperCall(
            TestConfig("mustInvokeSuperAnnotations" to listOf("Ann"))
        )

        val code = """
            annotation class Ann
            open class Foo {
                @Ann
                open fun x() {}
                fun y() {}
            }
            class Bar: Foo() {
                override fun x() {
                    super.y()
                }
            }
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }
}
