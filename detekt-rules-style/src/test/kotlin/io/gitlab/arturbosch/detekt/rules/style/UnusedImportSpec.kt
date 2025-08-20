package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinCoreEnvironmentTest
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

private const val ADDITIONAL_OPERATOR_SET = "additionalOperatorSet"

@KotlinCoreEnvironmentTest
class UnusedImportSpec(
    val env: KotlinEnvironmentContainer,
) {
    val subject = UnusedImport(Config.empty)

    @Test
    fun `does not report infix operators`() {
        val main =
            """
            import tasks.success
            
            fun task(f: () -> Unit) = 1
            
            fun main() {
                task {
                } success {
                }
            }
            """.trimIndent()
        val additional =
            """
            package tasks
            
            infix fun Int.success(f: () -> Unit) {}
            """.trimIndent()
        assertThat(subject.lintWithContext(env, main, additional)).isEmpty()
    }

    @Test
    fun `does not report range operators`() {
        val main =
            """
            import java.time.Month
            import ranges.rangeTo
            import ranges.rangeUntil
            
            fun main() {
                LocalDate.of(2024, Month.MARCH, 27)..LocalDate.of(2024, Month.MARCH, 27)
                LocalDate.of(2024, Month.MARCH, 27)..<LocalDate.of(2024, Month.MARCH, 27)
            }
            """.trimIndent()
        val additional =
            """
            package ranges
            
            operator fun LocalDate.rangeTo(that: LocalDate) = TODO()
            operator fun LocalDate.rangeUntil(that: LocalDate) = TODO()
            """.trimIndent()
        assertThat(subject.lintWithContext(env, main, additional, allowCompilationErrors = true)).isEmpty()
    }

    @Test
    fun `does not report equals operators in kotlin dsl`() {
        val main =
            """
            import org.gradle.api.Project
            import org.gradle.kotlin.dsl.assign
            import org.gradle.kotlin.dsl.configure
            import org.jetbrains.kotlin.gradle.dsl.JvmTarget
            import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
            
            fun Project.configureKotlinJvm() {
                this.extensions.configure<KotlinJvmProjectExtension> {
                    compilerOptions {
                        jvmTarget = JvmTarget.JVM_17
                    }
                }
            }
            """.trimIndent()
        val configuredSubject = UnusedImport(TestConfig(ADDITIONAL_OPERATOR_SET to listOf("assign")))
        assertThat(configuredSubject.lintWithContext(env, main, allowCompilationErrors = true)).isEmpty()
    }

    @Test
    fun `report equals operators in kotlin dsl`() {
        val main =
            """
            import org.gradle.api.Project
            import org.gradle.kotlin.dsl.assign
            import org.gradle.kotlin.dsl.configure
            import org.jetbrains.kotlin.gradle.dsl.JvmTarget
            import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

            fun Project.configureKotlinJvm() {
                this.extensions.configure<KotlinJvmProjectExtension> {
                    compilerOptions {
                        jvmTarget = JvmTarget.JVM_17
                    }
                }
            }
            """.trimIndent()
        val lint = subject.lintWithContext(env, main, allowCompilationErrors = true)
        assertThat(lint).hasSize(1)
        assertThat(lint[0].entity.signature).endsWith("import org.gradle.kotlin.dsl.assign")
    }

    @Test
    fun `does not report imports in documentation`() {
        val main =
            """
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
            """.trimIndent()
        val additional =
            """
            package tasks
            
            infix fun Int.success(f: () -> Unit) {}
            infix fun Int.failure(f: () -> Unit) {}
            infix fun Int.undefined(f: () -> Unit) {}
            """.trimIndent()
        assertThat(subject.lintWithContext(env, main, additional)).isEmpty()
    }

    @Test
    fun `should ignore import for link`() {
        val main =
            """
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
            """.trimIndent()
        val additional =
            """
            package tasks
            
            infix fun Int.success(f: () -> Unit) {}
            infix fun Int.failure(f: () -> Unit) {}
            infix fun Int.undefined(f: () -> Unit) {}
            """.trimIndent()
        val lint = subject.lintWithContext(env, main, additional)
        assertThat(lint).hasSize(1)
        assertThat(lint[0].entity.signature).endsWith("import tasks.undefined")
    }

    @Test
    fun `reports imports from the current package`() {
        val main =
            """
            package test
            import test.SomeClass
            
            val a: SomeClass? = null
            """.trimIndent()
        val additional =
            """
            package test
            
            class SomeClass
            """.trimIndent()
        val lint = subject.lintWithContext(env, main, additional)
        assertThat(lint).hasSize(1)
        assertThat(lint[0].entity.signature).endsWith("import test.SomeClass")
    }

    @Test
    fun `does not report KDoc references with method calls`() {
        val main =
            """
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
            """.trimIndent()
        val additional =
            """
            package android.text
            
            class TextWatcher {
                fun beforeTextChanged() {}
            }
            """.trimIndent()
        assertThat(subject.lintWithContext(env, main, additional)).isEmpty()
    }

    @Test
    fun `does not report KDoc references with companion method calls`() {
        val main =
            """
            package com.example

            import android.text.TextWatcher.beforeTextChanged

            class Test {
                /**
                 * [beforeTextChanged]
                 */
                fun test() {
                    TODO()
                }
            }
            """.trimIndent()
        val additional =
            """
            package android.text

            object TextWatcher {
                fun beforeTextChanged() {}
            }
            """.trimIndent()
        assertThat(subject.lintWithContext(env, main, additional)).isEmpty()
    }

    @Test
    fun `does not report KDoc references with extension method calls`() {
        val main =
            """
            package com.example

            import android.text.TextWatcher
            import android.text.beforeTextChanged

            class TestClass {
                /**
                 * [TextWatcher.beforeTextChanged]
                 */
                fun test() {
                    TODO()
                }
            }
            """.trimIndent()
        val additional1 =
            """
            package android.text

            class TextWatcher
            """.trimIndent()
        val additional2 =
            """
            package android.text

            fun TextWatcher.beforeTextChanged() {}
            """.trimIndent()
        assertThat(subject.lintWithContext(env, main, additional1, additional2)).isEmpty()
    }

    @Test
    fun `does report imported extension method which is not used`() {
        val main =
            """
            package com.example

            import android.text.TextWatcher
            import android.text.beforeTextChanged
            import android.text.afterTextChanged

            class TestClass {
                /**
                 * [TextWatcher.beforeTextChanged]
                 */
                fun test() {
                    TODO()
                }
            }
            """.trimIndent()
        val additional1 =
            """
            package android.text

            class TextWatcher
            """.trimIndent()
        val additional2 =
            """
            package android.text

            fun TextWatcher.beforeTextChanged() {}
            fun TextWatcher.afterTextChanged() {}
            """.trimIndent()
        assertThat(subject.lintWithContext(env, main, additional1, additional2)).hasSize(1)
    }

    @Test
    fun `reports imports with different cases`() {
        val main =
            """
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
            """.trimIndent()
        val p =
            """
            package p
            
            fun a() {}
            class B6
            class B
            object C {
                fun call() {}
            }
            class D
            """.trimIndent()
        val p2 =
            """
            package p2
            
            object B {
                const val NAME = ""
            }
            """.trimIndent()
        val escaped =
            """
            package escaped
            
            fun `when`() {}
            fun `foo`() {}
            """.trimIndent()
        val lint = subject.lintWithContext(env, main, p, p2, escaped)
        assertThat(lint).hasSize(3)
        assertThat(lint[0].entity.signature).contains("import p.B6")
        assertThat(lint[1].entity.signature).contains("import p.B as B12")
        assertThat(lint[2].entity.signature).contains("import escaped.`foo`")
    }

    @Test
    fun `does not report imports in same package when inner`() {
        val main =
            """
            package test
            
            import test.Outer.Inner
            
            open class Something<T>
            
            class Foo : Something<Inner>()
            """.trimIndent()
        val additional =
            """
            package test
            
            class Outer {
                class Inner
            }
            """.trimIndent()
        val lint = subject.lintWithContext(env, main, additional)
        assertThat(lint).isEmpty()
    }

    @Test
    fun `does not report KDoc @see annotation linking to class`() {
        val main =
            """
            import tasks.success
            
            /**
             * Do something.
             * @see success
             */
            fun doSomething()
            """.trimIndent()
        val additional =
            """
            package tasks
            
            fun success() {}
            """.trimIndent()
        assertThat(subject.lintWithContext(env, main, additional, allowCompilationErrors = true)).isEmpty()
    }

    @Test
    fun `does not report KDoc @see annotation linking to class with description`() {
        val main =
            """
            import tasks.success
            
            /**
             * Do something.
             * @see success something
             */
            fun doSomething() {}
            """.trimIndent()
        val additional =
            """
            package tasks
            
            fun success() {}
            """.trimIndent()
        assertThat(subject.lintWithContext(env, main, additional)).isEmpty()
    }

    @Test
    fun `reports KDoc @see annotation that does not link to class`() {
        val main =
            """
            import tasks.success
            
            /**
             * Do something.
             * @see something
             */
            fun doSomething() {}
            """.trimIndent()
        val additional =
            """
            package tasks
            
            fun success() {}
            """.trimIndent()
        assertThat(subject.lintWithContext(env, main, additional)).hasSize(1)
    }

    @Test
    fun `reports KDoc @see annotation that links after description`() {
        val main =
            """
            import tasks.success
            
            /**
             * Do something.
             * @see something success
             */
            fun doSomething() {}
            """.trimIndent()
        val additional =
            """
            package tasks
            
            fun success() {}
            """.trimIndent()
        assertThat(subject.lintWithContext(env, main, additional)).hasSize(1)
    }

    @Test
    fun `does not report imports in KDoc`() {
        val main =
            """
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
            """.trimIndent()
        val additional =
            """
            package tasks
            
            fun success() {}
            fun undefined() {}
            """.trimIndent()
        assertThat(subject.lintWithContext(env, main, additional)).isEmpty()
    }

    @Test
    fun `should not report import alias as unused when the alias is used`() {
        val main =
            """
            import test.forEach as foreach
            fun foo() = listOf().iterator().foreach {}
            """.trimIndent()
        val additional =
            """
            package test
            fun Iterator<Int>.forEach(f: () -> Unit) {}
            """.trimIndent()
        assertThat(subject.lintWithContext(env, main, additional, allowCompilationErrors = true)).isEmpty()
    }

    @Test
    fun `should not report used alias even when import is from same package`() {
        val main =
            """
            package com.example
            
            import com.example.foo as myFoo // from same package but with alias, check alias usage
            import com.example.other.foo as otherFoo // not from package with used alias
            
            fun f(): Boolean {
                return myFoo() == otherFoo()
            }
            """.trimIndent()
        val additional1 =
            """
            package com.example
            fun foo() = 1
            """.trimIndent()
        val additional2 =
            """
            package com.example.other
            fun foo() = 1
            """.trimIndent()
        assertThat(subject.lintWithContext(env, main, additional1, additional2)).isEmpty()
    }

    @Test
    fun `should not report import of provideDelegate operator overload - #1608`() {
        val main =
            """
            import org.gradle.kotlin.dsl.Foo
            import org.gradle.kotlin.dsl.provideDelegate // this line specifically should not be reported
            
            class DumpVersionProperties {
                private val dumpVersionProperties by Foo()
            }
            """.trimIndent()
        val additional =
            """
            package org.gradle.kotlin.dsl
            
            import kotlin.reflect.KProperty
            
            class Foo
            
            operator fun <T> Foo.provideDelegate(
                thisRef: T,
                prop: KProperty<*>
            ) = lazy { "" }
            """.trimIndent()
        assertThat(subject.lintWithContext(env, main, additional)).isEmpty()
    }

    @Test
    fun `should not report import of componentN operator`() {
        val main =
            """
            import com.example.MyClass.component1
            import com.example.MyClass.component2
            import com.example.MyClass.component543
            
            fun test() {
                val (a, b) = MyClass(1, 2)
            }
            """.trimIndent()
        val additional =
            """
            package com.example
            data class MyClass(val a: Int, val b: Int)
            """.trimIndent()

        assertThat(subject.lintWithContext(env, main, additional, allowCompilationErrors = true)).isEmpty()
    }

    @Test
    fun `should report import of identifiers with component in the name`() {
        val main =
            """
            import com.example.TestComponent
            import com.example.component1.Unused
            import com.example.components
            import com.example.component1AndSomethingElse
            
            fun test() {
                println("Testing")
            }
            """.trimIndent()
        val additional1 =
            """
            package com.example
            class TestComponent
            fun components() {}
            fun component1AndSomethingElse() {}
            """.trimIndent()
        val additional2 =
            """
            package com.example.component1
            class Unused
            """.trimIndent()
        val lint = subject.lintWithContext(env, main, additional1, additional2)

        assertThat(lint).hasSize(4)
        assertThat(lint[0].entity.signature).endsWith("import com.example.TestComponent")
        assertThat(lint[1].entity.signature).endsWith("import com.example.component1.Unused")
        assertThat(lint[2].entity.signature).endsWith("import com.example.components")
        assertThat(lint[3].entity.signature).endsWith("import com.example.component1AndSomethingElse")
    }

    @Test
    fun `reports when same name identifiers are imported and used`() {
        val mainFile =
            """
            import foo.test
            import bar.test
            fun main() {
                test(1)
            }
            """.trimIndent()
        val additionalFile1 =
            """
            package foo
            fun test(i: Int) {}
            """.trimIndent()
        val additionalFile2 =
            """
            package bar
            fun test(s: String) {}
            """.trimIndent()
        val findings = subject.lintWithContext(env, mainFile, additionalFile1, additionalFile2)
        assertThat(findings).hasSize(1)
        assertThat(findings[0].entity.signature).endsWith("import bar.test")
    }

    @Test
    fun `does not report when used as a type`() {
        val code =
            """
            import java.util.HashMap
            
            fun doesNothing(thing: HashMap<String, String>) {
            }
            """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report when used in a class literal expression`() {
        val code =
            """
            import java.util.HashMap
            import kotlin.reflect.KClass
            
            annotation class Ann(val value: KClass<*>)
            
            @Ann(HashMap::class)
            fun foo() {}
            """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report when used as a constructor call`() {
        val mainFile =
            """
            import x.y.z.Foo
            
            val foo = Foo()
            """.trimIndent()
        val additionalFile =
            """
            package x.y.z
            
            class Foo
            """.trimIndent()
        val findings = subject.lintWithContext(env, mainFile, additionalFile)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report when used as a annotation`() {
        val mainFile =
            """
            import x.y.z.Ann
            
            @Ann
            fun foo() {}
            """.trimIndent()
        val additionalFile =
            """
            package x.y.z
            
            annotation class Ann
            """.trimIndent()
        val findings = subject.lintWithContext(env, mainFile, additionalFile)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report companion object`() {
        val mainFile =
            """
            import x.y.z.Foo
            
            val x = Foo
            """.trimIndent()
        val additionalFile =
            """
            package x.y.z
            
            class Foo {
                companion object
            }
            """.trimIndent()
        val findings = subject.lintWithContext(env, mainFile, additionalFile)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report companion object that calls function`() {
        val mainFile =
            """
            import x.y.z.Foo
            
            val x = Foo.create()
            """.trimIndent()
        val additionalFile =
            """
            package x.y.z
            
            class Foo {
                companion object {
                    fun create(): Foo = Foo()
                }
            }
            """.trimIndent()
        val findings = subject.lintWithContext(env, mainFile, additionalFile)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report companion object that references variable`() {
        val mainFile =
            """
            import x.y.z.Foo
            
            val x = Foo.BAR
            """.trimIndent()
        val additionalFile =
            """
            package x.y.z
            
            class Foo {
                companion object {
                    const val BAR = 1
                }
            }
            """.trimIndent()
        val findings = subject.lintWithContext(env, mainFile, additionalFile)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report static import`() {
        val mainFile =
            """
            import x.y.z.FetchType
            
            val x = FetchType.LAZY
            """.trimIndent()
        val additionalFile =
            """
            package x.y.z
            
            enum class FetchType {
                LAZY
            }
            """.trimIndent()
        assertThat(subject.lintWithContext(env, mainFile, additionalFile)).isEmpty()
    }

    @Test
    fun `does not report annotations used as attributes - #3246`() {
        val mainFile =
            """
            import x.y.z.AnnotationA
            import x.y.z.AnnotationB
            
            class SomeClass {
                @AnnotationB(attribute = AnnotationA())
                val someProp: Int = 42
            }
            """.trimIndent()
        val additionalFile =
            """
            package x.y.z
            
            annotation class AnnotationA
            annotation class AnnotationB(val attribute: AnnotationA)
            """.trimIndent()
        assertThat(subject.lintWithContext(env, mainFile, additionalFile)).isEmpty()
    }

    @Test
    fun `does not report unused import for import used in kdoc - #4815`() {
        val mainFile =
            """
            import x.y.z.SomeClass
            
            class MyView
            
            /**
             * Style for [MyView]
             * Blablabla
             *
             * @property someVal Someval for [SomeClass]
             */
             data class StyleClass(val someVal: String)
            """.trimIndent()

        val additionalFile =
            """
            package x.y.z
            
            class SomeClass
            """.trimIndent()

        assertThat(subject.lintWithContext(env, mainFile, additionalFile)).isEmpty()
    }

    @Test
    fun `does not report imports which detekt cannot resolve but have string matches`() {
        val mainFile =
            """
            import x.y.z.foo
            import x.y.z.Bar
            
            fun test() {
                foo()
                foo("", 123)
                foo
            
                Bar().baz()
            }
            """.trimIndent()

        assertThat(subject.lintWithContext(env, mainFile, allowCompilationErrors = true)).isEmpty()
    }

    @Test
    fun `reports imports which detekt cannot resolve and do not have string matches`() {
        val mainFile =
            """
            import x.y.z.foo
            import x.y.z.Bar
            
            fun test() {
                2 + 3
            }
            """.trimIndent()

        assertThat(subject.lintWithContext(env, mainFile, allowCompilationErrors = true)).hasSize(2)
    }

    @Test
    fun `does not report used inline val import`() {
        val mainFile =
            """
            import additional.myVal
            
            fun main() {
                println(myVal)
            }
            """.trimIndent()
        val additionalFile =
            """
            package additional

            inline val myVal get() = 1
            """.trimIndent()

        assertThat(subject.lintWithContext(env, mainFile, additionalFile)).isEmpty()
    }
}
