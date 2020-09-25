package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UnusedImportsSpec : Spek({
    setupKotlinEnvironment()

    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { UnusedImports(Config.empty) }

    describe("UnusedImports rule") {

        it("does not report infix operators") {
            val main = """
                import tasks.success

                fun task(f: () -> Unit) = 1

                fun main() {
                    task {
                    } success {
                    }
                }
            """
            val additional = """
                package tasks

                infix fun Int.success(f: () -> Unit) {}
            """
            assertThat(subject.compileAndLintWithContext(env, main, additional)).isEmpty()
        }

        it("does not report imports in documentation") {
            val main = """
                import tasks.success
                import tasks.failure
                import tasks.undefined

                fun task(f: () -> Unit) = 1

                /**
                 *  Reference to [failure]
                 */
                class Test {
                    /** Reference to [undefined]*/
                    fun main() {
                        task {
                        } success {
                        }
                    }
                }
            """
            val additional = """
                package tasks

                infix fun Int.success(f: () -> Unit) {}
                infix fun Int.failure(f: () -> Unit) {}
                infix fun Int.undefined(f: () -> Unit) {}
            """
            assertThat(subject.compileAndLintWithContext(env, main, additional)).isEmpty()
        }

        it("should ignore import for link") {
            val main = """
                import tasks.success
                import tasks.failure
                import tasks.undefined

                fun task(f: () -> Unit) = 1

                /**
                 * Reference [undefined][failure]
                 */
                fun main() {
                    task {
                    } success {
                    }
                }                
            """
            val additional = """
                package tasks
                
                infix fun Int.success(f: () -> Unit) {}
                infix fun Int.failure(f: () -> Unit) {}
                infix fun Int.undefined(f: () -> Unit) {}
            """
            val lint = subject.compileAndLintWithContext(env, main, additional)
            with(lint) {
                assertThat(this).hasSize(1)
                assertThat(this[0].entity.signature).endsWith("import tasks.undefined")
            }
        }

        it("reports imports from the current package") {
            val main = """
                package test
                import test.SomeClass

                val a: SomeClass? = null
            """
            val additional = """
                package test

                class SomeClass
            """
            val lint = subject.compileAndLintWithContext(env, main, additional)
            with(lint) {
                assertThat(this).hasSize(1)
                assertThat(this[0].entity.signature).endsWith("import test.SomeClass")
            }
        }

        it("does not report KDoc references with method calls") {
            val main = """
                package com.example

                import android.text.TextWatcher

                class Test {
                    /**
                     * [TextWatcher.beforeTextChanged]
                     */
                    fun test() {
                        TODO()
                    }
                }
            """
            val additional = """
                package android.text

                class TextWatcher {
                    fun beforeTextChanged() {}
                }                
            """
            assertThat(subject.compileAndLintWithContext(env, main, additional)).isEmpty()
        }

        it("reports imports with different cases") {
            val main = """
                import p.a
                import p.B6 // positive
                import p.B as B12 // positive
                import p2.B as B2
                import p.C
                import escaped.`when`
                import escaped.`foo` // positive
                import p.D

                /** reference to [D] */
                fun main() {
                    println(a())
                    C.call()
                    fn(B2.NAME)
                    `when`()
                }

                fun fn(s: String) {}
            """
            val p = """
                package p

                fun a() {}
                class B6
                class B
                object C {
                    fun call() {}
                }
                class D
            """
            val p2 = """
                package p2

                object B {
                    const val NAME = ""
                }
            """
            val escaped = """
                package escaped

                fun `when`() {}
                fun `foo`() {}
            """
            val lint = subject.compileAndLintWithContext(env, main, p, p2, escaped)
            with(lint) {
                assertThat(this).hasSize(3)
                assertThat(this[0].entity.signature).contains("import p.B6")
                assertThat(this[1].entity.signature).contains("import p.B as B12")
                assertThat(this[2].entity.signature).contains("import escaped.`foo`")
            }
        }

        it("does not report imports in same package when inner") {
            val main = """
                package test
                
                import test.Outer.Inner
                
                open class Something<T>
                
                class Foo : Something<Inner>()                
            """
            val additional = """
                package test
                
                class Outer {
                    class Inner
                }
            """
            val lint = subject.compileAndLintWithContext(env, main, additional)
            with(lint) {
                assertThat(this).isEmpty()
            }
        }

        it("does not report KDoc @see annotation linking to class") {
            val main = """
                import tasks.success

                /**
                 * Do something.
                 * @see success
                 */
                fun doSomething()
            """
            val additional = """
                package tasks

                fun success() {}
            """
            assertThat(subject.compileAndLintWithContext(env, main, additional)).isEmpty()
        }

        it("does not report KDoc @see annotation linking to class with description") {
            val main = """
                import tasks.success

                /**
                 * Do something.
                 * @see success something
                 */
                fun doSomething() {}
            """
            val additional = """
                package tasks

                fun success() {}
            """
            assertThat(subject.compileAndLintWithContext(env, main, additional)).isEmpty()
        }

        it("reports KDoc @see annotation that does not link to class") {
            val main = """
                import tasks.success

                /**
                 * Do something.
                 * @see something
                 */
                fun doSomething() {}
            """
            val additional = """
                package tasks

                fun success() {}
            """
            assertThat(subject.compileAndLintWithContext(env, main, additional)).hasSize(1)
        }

        it("reports KDoc @see annotation that links after description") {
            val main = """
                import tasks.success

                /**
                 * Do something.
                 * @see something success
                 */
                fun doSomething() {}
            """
            val additional = """
                package tasks

                fun success() {}
            """
            assertThat(subject.compileAndLintWithContext(env, main, additional)).hasSize(1)
        }

        it("does not report imports in KDoc") {
            val main = """
                import tasks.success   // here
                import tasks.undefined // and here

                /**
                 * Do something.
                 * @throws success when ...
                 * @exception success when ...
                 * @see undefined
                 * @sample success when ...
                 */
                fun doSomething() {}
            """
            val additional = """
                package tasks

                fun success() {}
                fun undefined() {}
            """
            assertThat(subject.compileAndLintWithContext(env, main, additional)).isEmpty()
        }

        it("should not report import alias as unused when the alias is used") {
            val main = """
                import test.forEach as foreach
                fun foo() = listOf().iterator().foreach {}
            """
            val additional = """
                package test
                fun Iterator<Int>.forEach(f: () -> Unit) {}
            """
            assertThat(subject.compileAndLintWithContext(env, main, additional)).isEmpty()
        }

        it("should not report used alias even when import is from same package") {
            val main = """
                package com.example

                import com.example.foo as myFoo // from same package but with alias, check alias usage
                import com.example.other.foo as otherFoo // not from package with used alias

                fun f(): Boolean {
                    return myFoo() == otherFoo()
                }
            """
            val additional1 = """
                package com.example
                fun foo() = 1                
            """
            val additional2 = """
                package com.example.other
                fun foo() = 1                
            """
            assertThat(subject.compileAndLintWithContext(env, main, additional1, additional2)).isEmpty()
        }

        it("should not report import of provideDelegate operator overload - #1608") {
            val main = """
                import org.gradle.kotlin.dsl.Foo
                import org.gradle.kotlin.dsl.provideDelegate // this line specifically should not be reported
                
                class DumpVersionProperties {
                    private val dumpVersionProperties by Foo()
                }                
            """
            val additional = """
                package org.gradle.kotlin.dsl
                
                import kotlin.reflect.KProperty
                
                class Foo
                
                operator fun <T> Foo.provideDelegate(
                    thisRef: T,
                    prop: KProperty<*>
                ) = lazy { "" }                
            """
            assertThat(subject.compileAndLintWithContext(env, main, additional)).isEmpty()
        }

        it("should not report import of componentN operator") {
            val main = """
                import com.example.MyClass.component1
                import com.example.MyClass.component2
                import com.example.MyClass.component543

                fun test() {
                    val (a, b) = MyClass(1, 2)
                }
            """
            val additional = """
                package com.example
                data class MyClass(val a: Int, val b: Int)                
            """

            assertThat(subject.compileAndLintWithContext(env, main, additional)).isEmpty()
        }

        it("should report import of identifiers with component in the name") {
            val main = """
                import com.example.TestComponent
                import com.example.component1.Unused
                import com.example.components
                import com.example.component1AndSomethingElse

                fun test() {
                    println("Testing")
                }
            """
            val additional1 = """
                package com.example
                class TestComponent
                fun components() {}
                fun component1AndSomethingElse() {}
            """
            val additional2 = """
                package com.example.component1
                class Unused
            """
            val lint = subject.compileAndLintWithContext(env, main, additional1, additional2)

            with(lint) {
                assertThat(this).hasSize(4)
                assertThat(this[0].entity.signature).endsWith("import com.example.TestComponent")
                assertThat(this[1].entity.signature).endsWith("import com.example.component1.Unused")
                assertThat(this[2].entity.signature).endsWith("import com.example.components")
                assertThat(this[3].entity.signature).endsWith("import com.example.component1AndSomethingElse")
            }
        }

        it("reports when same name identifiers are imported and used") {
            val mainFile = """
                import foo.test
                import bar.test
                fun main() {
                    test(1)
                }      
            """
            val additionalFile1 = """
                package foo
                fun test(i: Int) {}
            """
            val additionalFile2 = """
                package bar
                fun test(s: String) {}
            """
            val findings = subject.compileAndLintWithContext(env, mainFile, additionalFile1, additionalFile2)
            assertThat(findings).hasSize(1)
            assertThat(findings[0].entity.signature).endsWith("import bar.test")
        }

        it("does not report when used as a type") {
            val code = """
                import java.util.HashMap
                
                fun doesNothing(thing: HashMap<String, String>) {
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        it("does not report when used in a class literal expression") {
            val code = """
                import java.util.HashMap
                import kotlin.reflect.KClass
                
                annotation class Ann(val value: KClass<*>)
                
                @Ann(HashMap::class)
                fun foo() {}
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        it("does not report when used as a constructor call") {
            val mainFile = """
                import x.y.z.Foo
                
                val foo = Foo()
            """
            val additionalFile = """
                package x.y.z
                
                class Foo
            """
            val findings = subject.compileAndLintWithContext(env, mainFile, additionalFile)
            assertThat(findings).isEmpty()
        }

        it("does not report when used as a annotation") {
            val mainFile = """
                import x.y.z.Ann

                @Ann
                fun foo() {}
            """
            val additionalFile = """
                package x.y.z
                
                annotation class Ann
            """
            val findings = subject.compileAndLintWithContext(env, mainFile, additionalFile)
            assertThat(findings).isEmpty()
        }

        it("does not report companion object") {
            val mainFile = """
                import x.y.z.Foo
                
                val x = Foo
            """
            val additionalFile = """
                package x.y.z
                
                class Foo {
                    companion object
                }
            """
            val findings = subject.compileAndLintWithContext(env, mainFile, additionalFile)
            assertThat(findings).isEmpty()
        }

        it("does not report companion object that calls function") {
            val mainFile = """
                import x.y.z.Foo
                
                val x = Foo.create()
            """
            val additionalFile = """
                package x.y.z
                
                class Foo {
                    companion object {
                        fun create(): Foo = Foo()
                    }
                }
            """
            val findings = subject.compileAndLintWithContext(env, mainFile, additionalFile)
            assertThat(findings).isEmpty()
        }

        it("does not report companion object that references variable") {
            val mainFile = """
                import x.y.z.Foo
                
                val x = Foo.BAR
            """
            val additionalFile = """
                package x.y.z
                
                class Foo {
                    companion object {
                        const val BAR = 1
                    }
                }
            """
            val findings = subject.compileAndLintWithContext(env, mainFile, additionalFile)
            assertThat(findings).isEmpty()
        }
    }
})
