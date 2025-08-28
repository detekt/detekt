package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.lint
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class UnusedPrivateClassSpec {

    val subject = UnusedPrivateClass(Config.empty)

    @Nested
    inner class `top level interfaces` {
        @Test
        fun `should report them if not used`() {
            val code = """
                private interface Foo
                class Bar
            """.trimIndent()

            val findings = subject.lint(code)

            assertThat(findings).singleElement()
                .hasStartSourceLocation(1, 1)
        }

        @Nested
        inner class `top level private classes` {

            @Test
            fun `should report them if not used`() {
                val code = """
                    private class Foo
                    class Bar
                """.trimIndent()

                val findings = subject.lint(code)

                assertThat(findings).singleElement()
                    .hasStartSourceLocation(1, 1)
            }

            @Test
            fun `should not report them if used as parent`() {
                val code = """
                    private open class Foo
                    private class Bar : Foo()
                """.trimIndent()

                val findings = subject.lint(code)

                assertThat(findings).singleElement()
                    .hasStartSourceLocation(2, 1)
            }

            @Test
            fun `should not report them used as generic parent type`() {
                val code = """
                    class Bar
                    private interface Foo<in T> {
                        operator fun invoke(b: T): Unit
                    }
                    
                    data class FooOne(val b: Bar) : Foo<Bar> {
                        override fun invoke(b: Bar): Unit = Unit
                    }
                """.trimIndent()

                val findings = subject.lint(code)

                assertThat(findings).isEmpty()
            }
        }

        @Test
        fun `should not report them if used inside a function`() {
            val code = """
                private class Foo
                fun something() {
                    val foo: Foo = Foo()
                }
            """.trimIndent()

            val findings = subject.lint(code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not report them if used as function parameter`() {
            val code = """
                private class Foo
                private object Bar {
                  fun bar(foo: Foo) = Unit
                }
            """.trimIndent()

            val findings = subject.lint(code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not report them if used as nullable variable type`() {
            val code = """
                private class Foo
                private val a: Foo? = null
            """.trimIndent()

            val findings = subject.lint(code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not report them if used as variable type`() {
            val code = """
                private class Foo
                private lateinit var a: Foo
            """.trimIndent()

            val findings = subject.lint(code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not report them if used as generic type`() {
            val code = """
                private class Foo
                private lateinit var foos: List<Foo>
            """.trimIndent()

            val findings = subject.lint(code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not report them if used as inner type parameter`() {
            val code = """
                private val elements = listOf(42).filterIsInstance<Set<Item>>()
                private class Item
            """.trimIndent()

            val findings = subject.lint(code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not report them if used as outer type parameter`() {
            val code = """
                private val elements = listOf(42).filterIsInstance<Something<Int>>()
                private abstract class Something<E>: Collection<E>
            """.trimIndent()

            val findings = subject.lint(code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not report them if used as generic type in functions`() {
            val code = """
                private class Foo
                private var a = bar<Foo>()
                
                fun <T> bar(): T {
                    throw Exception()
                }
            """.trimIndent()

            val findings = subject.lint(code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not report them if used as nested generic type`() {
            val code = """
                private class Foo
                private lateinit var foos: List<List<Foo>>
            """.trimIndent()

            val findings = subject.lint(code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not report them if used as type with generics`() {
            val code = """
                private class Foo<T>
                private lateinit var foos: Foo<String>
            """.trimIndent()

            val findings = subject.lint(code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not report them if used as nullable type with generics`() {
            val code = """
                private class Foo<T>
                private var foos: Foo<String>? = Foo()
            """.trimIndent()

            val findings = subject.lint(code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not report them if used as non-argument constructor`() {
            val code = """
                private class Foo
                private val a = Foo()
            """.trimIndent()

            val findings = subject.lint(code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not report them if used as constructor with arguments`() {
            val code = """
                private class Foo(val a: String)
                private val a = Foo("test")
            """.trimIndent()

            val findings = subject.lint(code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not report them if used as function return type`() {
            val code = """
                private class Foo(val a: String)
                private object Bar {
                  fun foo(): Foo? = null
                }
            """.trimIndent()

            val findings = subject.lint(code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not report them if used as lambda declaration parameter`() {
            val code = """
                private class Foo
                private val lambda: ((Foo) -> Unit)? = null
            """.trimIndent()

            val findings = subject.lint(code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not report them if used as lambda declaration return type`() {
            val code = """
                private class Foo
                private val lambda: (() -> Foo)? = null
            """.trimIndent()

            val findings = subject.lint(code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not report them if used as lambda declaration generic type`() {
            val code = """
                private class Foo
                private val lambda: (() -> List<Foo>)? = null
            """.trimIndent()

            val findings = subject.lint(code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not report them if used as inline object type`() {
            val code = """
                private abstract class Foo {
                    abstract fun bar()
                }
                
                private object Bar {
                    private fun foo() = object : Foo() {
                        override fun bar() = Unit
                    }
                }
            """.trimIndent()

            val findings = subject.lint(code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not report them if used for type conversion`() {
            val code = """
                private class Foo(val bar: String)
                private fun f(a: Any) = a as Foo
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not report them if used for type checking`() {
            val code = """
                private class Foo(val bar: String)
                private fun f(a: Any) = a is Foo
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `testcase for reported false positives` {

        @Test
        fun `does not crash when using wildcards in generics - #1345`() {
            val code = """
                import kotlin.reflect.KClass
                
                private class Foo
                fun bar(clazz: KClass<*>) = Unit
            """.trimIndent()

            val findings = UnusedPrivateClass(Config.empty).lint(code)

            assertThat(findings).hasSize(1)
        }

        @Test
        @DisplayName("does not report (companion-)object/named-dot references - #1347")
        fun doesNotReportObjectNamedDotReferences() {
            val code = """
                class Test {
                    val items = Item.values().map { it.text }.toList()
                }
                
                private enum class Item(val text: String) {
                    A("A"),
                    B("B"),
                    C("C")
                }
            """.trimIndent()

            val findings = UnusedPrivateClass(Config.empty).lint(code)

            assertThat(findings).isEmpty()
        }

        @Test
        @DisplayName("does not report classes that are used with ::class - #1390")
        fun doesNotReportClassesUsedWithinClass() {
            val code = """
                class UnusedPrivateClassTest {
                
                    private data class SomeClass(val name: String)
                
                    private data class AnotherClass(val id: Long)
                
                    fun `verify class is used`(): Boolean {
                        val instance = SomeClass(name = "test")
                        return AnotherClass::class.java.simpleName == instance::class.java.simpleName
                    }
                
                    fun getSomeObject(): ((String) -> Any) = ::InternalClass
                    private class InternalClass(val param: String)
                }
            """.trimIndent()

            val findings = UnusedPrivateClass(Config.empty).lint(code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report used private annotations - #2093`() {
            val code = """
                private annotation class Test1
                private annotation class Test2
                private annotation class Test3
                private annotation class Test4
                
                @Test1 class Custom(@Test2 param: String) {
                    @Test3 val property = ""
                    @Test4 fun function() {}
                }
            """.trimIndent()

            val findings = UnusedPrivateClass(Config.empty).lint(code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report imported enum class - #2809`() {
            val code = """
                package com.example
                
                import com.example.C.E.E1
                
                class C {
                    fun test() {
                        println(E1)
                    }
                
                    private enum class E {
                        E1,
                        E2,
                        E3
                    }
                }
            """.trimIndent()
            val findings = UnusedPrivateClass(Config.empty).lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should report not imported enum class - #2809, #2816`() {
            val code = """
                package com.example
                
                import com.example.C.EFG.EFG1
                
                class C {
                    fun test() {
                        println(EFG1)
                    }
                
                    private enum class E {
                        E1
                    }
                
                    private enum class EFG {
                        EFG1
                    }
                }
            """.trimIndent()
            val findings = UnusedPrivateClass(Config.empty).lint(code)
            assertThat(findings).singleElement()
                .hasStartSourceLocation(10, 5)
        }

        @Test
        fun `should not report when callable ref for constructor is used`() {
            val code = """
                private class A

                private val listOfConstructors = listOf(::A)
            """.trimIndent()
            val findings = UnusedPrivateClass(Config.empty).lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not report when callable ref for inner private class constructor is used with parent name`() {
            val code = """
                class Parent {
                    private class Foo

                    private val list = listOf(Parent::Foo)
                }
            """.trimIndent()
            val findings = UnusedPrivateClass(Config.empty).lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not report callable ref for inner private child class constructor is used with parent private class name`() {
            val code = """
                class Parent {
                    private class Foo {
                        private class Bar
                        private val list = listOf(Foo::Bar)
                    }
                }
            """.trimIndent()
            val findings = UnusedPrivateClass(Config.empty).lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not report when callable ref for inner private class constructor is used without parent name`() {
            val code = """
                class Parent {
                    private class Foo

                    private val list = listOf(::Foo)
                }
            """.trimIndent()
            val findings = UnusedPrivateClass(Config.empty).lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not report when callable ref for inner private class secondary constructor is used`() {
            val code = """
                class Parent {
                    private class Foo {
                        constructor(a: Int)
                    }

                    private val list = listOf(1).map(::Foo)
                }
            """.trimIndent()
            val findings = UnusedPrivateClass(Config.empty).lint(code)
            assertThat(findings).isEmpty()
        }
    }
}
