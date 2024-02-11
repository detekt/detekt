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
    fun `super method has user defined annotation`() {
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
    fun `overriding has super call`() {
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
                    super.someMethod(arg)
                    println()
                }
            }
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).isEmpty()
    }
}
