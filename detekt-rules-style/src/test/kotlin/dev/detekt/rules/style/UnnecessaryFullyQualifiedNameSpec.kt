@file:Suppress("RemoveRedundantQualifierName")

package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.lint
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class UnnecessaryFullyQualifiedNameSpec {

    val subject = UnnecessaryFullyQualifiedName(Config.empty)

    @Nested
    inner class `fully qualified class names in type annotations` {
        @Test
        fun `reports fully qualified class names in return types`() {
            val code = """
                class Test {
                    fun method(): java.util.ArrayList<String> {
                        return java.util.ArrayList()
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).hasSize(2)
        }

        @Test
        fun `reports fully qualified class names in variable declarations`() {
            val code = """
                class Test {
                    val list: java.util.ArrayList<String> = java.util.ArrayList()
                }
            """.trimIndent()

            assertThat(subject.lint(code)).hasSize(2)
        }

        @Test
        fun `reports fully qualified names starting with non-standard packages`() {
            val code = """
                package asdf.server.foo

                class Bar

                class Test {
                    val svc: asdf.server.foo.Bar = asdf.server.foo.Bar()
                }
            """.trimIndent()

            assertThat(subject.lint(code)).hasSize(2)
        }
    }

    @Nested
    inner class `fully qualified static method calls` {
        @Test
        fun `reports fully qualified class names in static method calls`() {
            val code = """
                class Test {
                    fun method() {
                        val date = java.time.LocalDate.now()
                        val list = java.util.Collections.emptyList<String>()
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).hasSize(2)
        }
    }

    @Nested
    inner class `simple names without packages` {
        @Test
        fun `does not report simple class names without packages`() {
            val code = """
                class Test {
                    val name: String = "test"
                    fun method(): Int = 42
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report method calls on objects with single dot`() {
            val code = """
                object MyObject {
                    fun doSomething() = "done"
                }

                object Helper {
                    fun getValue() = 42
                }

                class Test {
                    fun method() {
                        val result = MyObject.doSomething()
                        val value = Helper.getValue()
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `imports and package declarations` {
        @Test
        fun `does not report imports themselves`() {
            val code = """
                import java.util.ArrayList
                import java.time.LocalDate
                import java.util.concurrent.TimeUnit

                class Test {
                    fun method(): ArrayList<String> {
                        return ArrayList()
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report package declarations`() {
            val code = """
                package com.example.mypackage

                class Test {
                    fun method() {
                        println("Hello")
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `string literals` {
        @Test
        fun `does not report strings containing dots`() {
            val code = """
                class Test {
                    fun method() {
                        val text = "com.example.MyClass"
                        println("Loading from java.util.List")
                        val stackTrace = "at com.example.MyClass.method()"
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `constructor calls` {
        @Test
        fun `reports constructor calls with fully qualified names`() {
            val code = """
                class Test {
                    fun method() {
                        val list = java.util.ArrayList<String>()
                        val date = java.time.LocalDate.of(2024, 1, 1)
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).hasSize(2)
        }
    }

    @Nested
    inner class Annotations {
        @Test
        fun `reports fully qualified names in annotations`() {
            val code = """
                @kotlin.Deprecated("Use NewClass instead")
                class Test {
                    @java.lang.SuppressWarnings("unchecked")
                    fun method() {}
                }
            """.trimIndent()

            assertThat(subject.lint(code)).hasSize(2)
        }
    }

    @Nested
    inner class `nested classes` {
        @Test
        fun `does not report nested class names without package`() {
            val code = """
                class Outer {
                    class Inner
                    companion object { const val CONST = 1 }
                }

                class Test {
                    val t: Outer.Inner = Outer.Inner()
                    fun method() {
                        val const = Outer.CONST
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `reports fully qualified names in inner class references`() {
            val code = """
                class Test {
                    fun method() {
                        val entry: java.util.AbstractMap.SimpleEntry<String, Int> = java.util.AbstractMap.SimpleEntry("key", 1)
                        val immutableEntry: java.util.AbstractMap.SimpleImmutableEntry<String, Int> = java.util.AbstractMap.SimpleImmutableEntry("key", 2)
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).hasSize(4)
        }
    }

    @Nested
    inner class `class literals` {
        @Test
        fun `reports fully qualified names in class literals`() {
            val code = """
                class Test {
                    fun method() {
                        val clazz = java.lang.String::class
                        val javaClass = java.util.ArrayList::class.java
                        val kClass = kotlin.collections.List::class
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).hasSize(3)
        }
    }

    @Nested
    inner class `generic type parameters` {
        @Test
        fun `reports fully qualified names in generic type parameters`() {
            val code = """
                class Test {
                    val map: java.util.HashMap<String, java.lang.Integer> = java.util.HashMap()
                    val list: java.util.ArrayList<java.lang.Double> = java.util.ArrayList()
                }
            """.trimIndent()

            assertThat(subject.lint(code)).hasSize(6)
        }
    }

    @Nested
    inner class `extension functions` {
        @Test
        fun `reports fully qualified names in extension function receivers`() {
            val code = """
                fun java.util.List<String>.customExtension(): Int {
                    return this.size
                }

                fun java.lang.StringBuilder.appendTwice(text: String) {
                    this.append(text).append(text)
                }
            """.trimIndent()

            assertThat(subject.lint(code)).hasSize(2)
        }
    }

    @Nested
    inner class `when expressions` {
        @Test
        fun `reports fully qualified names in when expression branches`() {
            val code = """
                class Test {
                    fun method(obj: Any) {
                        when (obj) {
                            is java.util.List<*> -> println("list")
                            is java.util.Map<*, *> -> println("map")
                            is java.lang.String -> println("string")
                            else -> println("other")
                        }
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).hasSize(3)
        }
    }

    @Nested
    inner class `try-catch blocks` {
        @Test
        fun `reports fully qualified names in try-catch blocks`() {
            val code = """
                class Test {
                    fun method() {
                        try {
                            // some code
                        } catch (e: java.io.IOException) {
                            println("IO error")
                        } catch (e: java.lang.IllegalArgumentException) {
                            println("Illegal argument")
                        } catch (e: java.sql.SQLException) {
                            println("SQL error")
                        }
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).hasSize(3)
        }
    }

    @Nested
    inner class `lambda parameters` {
        @Test
        fun `reports fully qualified names in lambda parameters`() {
            val code = """
                class Test {
                    fun method() {
                        val processor: (java.util.List<String>) -> Unit = { list ->
                            println(list.size)
                        }

                        val transformer: (java.lang.String) -> Int = { str ->
                            str.length
                        }
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).hasSize(2)
        }
    }

    @Nested
    inner class `type aliases` {
        @Test
        fun `reports fully qualified names in type aliases`() {
            val code = """
                typealias StringMap = java.util.HashMap<String, String>
                typealias Predicate<T> = (T) -> Boolean
                typealias DateList = java.util.ArrayList<java.time.LocalDate>
            """.trimIndent()

            assertThat(subject.lint(code)).hasSize(3)
        }
    }

    @Nested
    inner class `supertype declarations` {
        @Test
        fun `reports fully qualified names in supertype declarations`() {
            val code = """
                class MyList : java.util.ArrayList<String>() {
                    // implementation
                }

                interface MyComparator : java.util.Comparator<String> {
                    // implementation
                }

                class MyException : java.lang.RuntimeException() {
                    // implementation
                }
            """.trimIndent()

            assertThat(subject.lint(code)).hasSize(3)
        }
    }

    @Nested
    inner class `nullable types` {
        @Test
        fun `reports fully qualified names in function return types with nullability`() {
            val code = """
                class Test {
                    fun method1(): java.util.List<String>? = null
                    fun method2(): java.lang.String? = null
                    fun method3(): java.util.Map<String, Int>? = null
                }
            """.trimIndent()

            assertThat(subject.lint(code)).hasSize(3)
        }
    }

    @Nested
    inner class `object expressions` {
        @Test
        fun `reports fully qualified names in object expressions`() {
            val code = """
                class Test {
                    fun method() {
                        val comparator = object : java.util.Comparator<String> {
                            override fun compare(o1: String, o2: String): Int = o1.compareTo(o2)
                        }

                        val runnable = object : java.lang.Runnable {
                            override fun run() {
                                println("Running")
                            }
                        }
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).hasSize(2)
        }
    }

    @Nested
    inner class `cast expressions` {
        @Test
        fun `reports fully qualified names in cast expressions`() {
            val code = """
                class Test {
                    fun method(obj: Any) {
                        val list = obj as java.util.List<String>
                        val map = obj as? java.util.Map<String, Int>
                        val string = obj as java.lang.String
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).hasSize(3)
        }
    }

    @Nested
    inner class `Kotlin standard library` {
        @Test
        fun `reports Kotlin standard library fully qualified names`() {
            val code = """
                class Test {
                    fun method() {
                        val list: kotlin.collections.MutableList<String> = kotlin.collections.mutableListOf()
                        val map = kotlin.collections.hashMapOf<String, Int>()
                        val pair = kotlin.Pair("key", "value")
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).hasSize(4)
        }
    }

    @Nested
    inner class `fully qualified function calls` {
        @Test
        fun `reports fully qualified function calls from kotlin io`() {
            val code = """
                class Test {
                    fun method() {
                        kotlin.io.println("Hello")
                        kotlin.io.print("World")
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).hasSize(2)
        }

        @Test
        fun `reports fully qualified function calls from kotlin collections`() {
            val code = """
                class Test {
                    fun method() {
                        val list = kotlin.collections.listOf(1, 2, 3)
                        val map = kotlin.collections.mapOf("a" to 1)
                        val set = kotlin.collections.setOf("x", "y")
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).hasSize(3)
        }

        @Test
        fun `reports fully qualified function calls from java lang System`() {
            val code = """
                class Test {
                    fun method() {
                        java.lang.System.currentTimeMillis()
                        java.lang.System.nanoTime()
                        java.lang.System.exit(0)
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).hasSize(3)
        }

        @Test
        fun `does not report simple function calls without full qualification`() {
            val code = """
                class Test {
                    fun method() {
                        println("Hello")
                        print("World")
                        listOf(1, 2, 3)
                        System.currentTimeMillis()
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `reports fully qualified kotlin math functions`() {
            val code = """
                class Test {
                    fun method() {
                        val max = kotlin.math.max(1, 2)
                        val min = kotlin.math.min(3, 4)
                        val abs = kotlin.math.abs(-5)
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).hasSize(3)
        }
    }

    @Nested
    inner class `vararg parameters` {
        @Test
        fun `reports fully qualified names in vararg parameters`() {
            val code = """
                class Test {
                    fun method(vararg items: java.util.concurrent.Future<String>) {
                        println(items.size)
                    }

                    fun process(vararg values: java.lang.Number) {
                        println(values.size)
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).hasSize(2)
        }
    }

    @Nested
    inner class `secondary constructors` {
        @Test
        fun `reports fully qualified names in secondary constructor parameters`() {
            val code = """
                class Test {
                    constructor(list: java.util.List<String>)
                    constructor(map: java.util.Map<String, Int>, set: java.util.Set<String>)
                }
            """.trimIndent()

            assertThat(subject.lint(code)).hasSize(3)
        }
    }

    @Nested
    inner class `constructor references` {
        @Test
        fun `reports fully qualified names in callable reference types`() {
            val code = """
                class Test {
                    fun method() {
                        val factory: () -> java.util.ArrayList<String> = ::ArrayList
                        val supplier: java.util.function.Supplier<String> = java.util.function.Supplier { "test" }
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).hasSize(3)
        }
    }

    @Nested
    inner class `companion object and static property access` {
        @Test
        fun `reports fully qualified access to companion object properties`() {
            val code = """
                class Test {
                    fun method() {
                        val month = java.util.Calendar.JANUARY
                        val field = java.lang.Integer.MAX_VALUE
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).hasSize(2)
        }

        @Test
        fun `does not report simple companion object access`() {
            val code = """
                object MyConstants {
                    const val VALUE = 42
                }

                class Test {
                    fun method() {
                        val v = MyConstants.VALUE
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `edge cases for validation` {
        @Test
        fun `does not report types with invalid identifier characters`() {
            val code = """
                object `some-invalid` {
                    object foo {
                        class Bar
                    }
                }

                class Test {
                    fun method() {
                        val x = `some-invalid`.foo.Bar()
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report all-uppercase path segments`() {
            val code = """
                class Outer {
                    class Inner {
                        class Nested
                    }
                }

                class Test {
                    val x: Outer.Inner.Nested = Outer.Inner.Nested()
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report single segment paths`() {
            val code = """
                class Test {
                    fun method() {
                        val x = String.format("test")
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report expressions starting with numbers`() {
            val code = """
                class Test {
                    fun method(): Int {
                        return 123.toString().length
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `chained method calls` {
        @Test
        fun `does not report method chains on local variables`() {
            val code = """
                class Test {
                    fun method() {
                        val list = mutableListOf<String>()
                        list.add("item").also { println(it) }
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `function calls with lowercase names` {
        @Test
        fun `does not report method calls on instances`() {
            val code = """
                class Test {
                    fun method() {
                        val obj = Object()
                        obj.hashCode()
                        obj.toString()
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report single-segment package qualified calls`() {
            val code = """
                class Test {
                    fun method() {
                        kotlin.run { println("test") }
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `empty and null selector expressions` {
        @Test
        fun `handles expressions with empty function names gracefully`() {
            val code = """
                class Test {
                    fun method() {
                        val x = java.lang.String()
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).hasSize(1)
        }
    }

    @Nested
    inner class `nested type qualifiers` {
        @Test
        fun `does not duplicate reports for nested qualified types`() {
            val code = """
                class Test {
                    fun method(): java.util.Map.Entry<String, Int>? = null
                }
            """.trimIndent()

            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `reports the full type not the qualifier part`() {
            val code = """
                class Test {
                    val entry: java.util.AbstractMap.SimpleEntry<String, String> = TODO()
                }
            """.trimIndent()

            val findings = subject.lint(code)
            assertThat(findings).hasSize(1)
            assertThat(findings.first()).hasMessage(
                "Fully qualified class name 'java.util.AbstractMap.SimpleEntry' can be replaced with an import."
            )
        }
    }
}
