package dev.detekt.rules.naming

import dev.detekt.api.Config
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinCoreEnvironmentTest
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class NoNameShadowingSpec(val env: KotlinEnvironmentContainer) {
    val subject = NoNameShadowing(Config.empty)

    @Test
    fun `report shadowing variable`() {
        val code = """
            fun test(i: Int) {
                val i = 1
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).singleElement()
            .hasMessage("Name shadowed: i")
            .hasStartSourceLocation(2, 9)
    }

    @Test
    fun `report shadowing destructuring declaration entry`() {
        val code = """
            fun test(j: Int) {
                val (j, _) = 1 to 2
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).singleElement()
            .hasMessage("Name shadowed: j")
    }

    @Test
    fun `report shadowing lambda parameter`() {
        val code = """
            fun test(k: Int) {
                listOf(1).map { k ->
                }
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).singleElement()
            .hasMessage("Name shadowed: k")
    }

    @Test
    fun `report shadowing nested lambda 'it' parameter`() {
        val code = """
            fun test() {
                listOf(1).forEach {
                    listOf(2).forEach { it ->
                    }
                }
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).singleElement()
            .hasMessage("Name shadowed: it")
    }

    @Test
    fun `does not report when implicit 'it' parameter isn't used`() {
        val code = """
            fun test() {
                listOf(1).forEach {
                    listOf(2).forEach {
                    }
                }
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report not shadowing variable`() {
        val code = """
            fun test(i: Int) {
                val j = i
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report different name with file level prop`() {
        val code = """
            val a: Int = 0
            fun test(b: Int) {
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report not shadowing nested lambda implicit 'it' parameter`() {
        val code = """
            fun test() {
                listOf(1).forEach { i ->
                    listOf(2).forEach {
                        println(it)
                    }
                }
                "".run {
                    listOf(2).forEach {
                        println(it)
                    }
                }
                listOf("").let { list ->
                    list.map { it + "x" }
                }
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `reports shadowing of variable in local function when param name is same`() {
        val code = """
            fun test(a: Int) {
                fun localTest(a: Int) {
                }
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `does not report shadowing of variable in local function when param name is different`() {
        val code = """
            fun test(a: Int) {
                fun localTest(b: Int) {
                }
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `reports when use is inside a call of function - #7741`() {
        val code = """
            fun asdf(rotation: Float, onClick: (Float) -> Unit) {
                foo {
                    asdf(rotation) { rotation -> onClick(rotation) }
                }
            }

            fun foo(block: () -> Unit) {
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `does not report when outer lambda doesn't have it ref - #8069`() {
        val code = """
            class MySettingsPlugin {
                fun apply(settings: Any) {
                    settings.beforeProject {
                        val f = listOf<String>()
                        f.map { java.io.File(it) }
                    }
                }
        
                fun Any.beforeProject(block: Any.() -> Unit) {
                    this.block()
                }
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does report when outer lambda does have it ref and it is used`() {
        val code = """
            class MySettingsPlugin {
                fun apply(settings: Any) {
                    settings.beforeProject {
                        val f = listOf<String>()
                        f.map { java.io.File(it) }
                    }
                }
        
                fun Any.beforeProject(block: (Any) -> Unit) {
                    block(this)
                }
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `does not report when outer lambda does have it ref and it is not used`() {
        val code = """
            class MySettingsPlugin {
                fun apply(settings: Any) {
                    settings.beforeProject {
                        val f = listOf<String>()
                        f.map { java.io.File("soem path") }
                    }
                }
        
                fun Any.beforeProject(block: (Any) -> Unit) {
                    block(this)
                }
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does report when unnamed function is used`() {
        val code = """
            fun test() {
                fun (a: Int) {
                    fun foo(a: Int) {
                        
                    }
                }

                fun foo(b: Int) {
                     fun (b: Int) {}   
                }
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).satisfiesExactlyInAnyOrder(
            { assertThat(it).hasMessage("Name shadowed: a") },
            { assertThat(it).hasMessage("Name shadowed: b") },
        )
    }

    @Test
    fun `does report when unnamed function is used present inside a class`() {
        val code = """
            class Test {
                fun test() {
                fun (a: Int) {
                    fun foo(a: Int) {
                        
                    }
                }

                fun foo(b: Int) {
                     fun (b: Int) {}   
                }
            }
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).satisfiesExactlyInAnyOrder(
            { assertThat(it).hasMessage("Name shadowed: a") },
            { assertThat(it).hasMessage("Name shadowed: b") },
        )
    }

    @Nested
    inner class `with class` {
        @Test
        fun `does not report class with param and property`() {
            val code = """
                class Foo(val a: Int, b: Int)
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report class and function has same param name`() {
            val code = """
                class Foo(val a: Int) {
                    fun foo(a: Int) {
                        // this access
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report class param without val var and function has same param name`() {
            val code = """
                class Foo(a: Int) {
                    fun foo(a: Int) {}
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does report class param without val var and function inside init has same param name`() {
            val code = """
                class Foo(a: Int) {
                    init {
                        fun foo(a: Int) {}
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does not report class and lambda has same param name`() {
            val code = """
                class Foo(val a: Int) {
                    fun foo() {
                        listOf(1).map { a -> }
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report class param without val or var and lambda has same param name`() {
            val code = """
                class Foo(a: Int) {
                    fun foo() {
                        listOf(1).map { a -> }
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does report class param without val or var and lambda inside init has same param name`() {
            val code = """
                class Foo(a: Int, b: Int) {
                    init {
                        fun foo() {
                            listOf(1).map { a -> }
                        }
                        listOf(1).map { b -> }
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).satisfiesExactlyInAnyOrder(
                { assertThat(it).hasMessage("Name shadowed: a") },
                { assertThat(it).hasMessage("Name shadowed: b") },
            )
        }

        @Test
        fun `does not report when same name is used in companion class and class`() {
            val code = """
                class Foo(val a: Int) {
                    companion object {
                        fun foo() {
                            listOf(1).map { a -> }
                        }
                        fun bar(a: Int) {}
                        init {
                            listOf(1).map { a -> }
                        }
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when same name used in object and class`() {
            val code = """
                class Foo(val a: Int) {
                    object A {
                        fun foo() {
                            listOf(1).map { a -> }
                        }
                        fun bar(a: Int) {}
                        init {
                            listOf(1).map { a -> }
                            fun foo() {
                                listOf(1).map { a -> }
                            }
                        }
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when different name used in object and nested object`() {
            val code = """
                object A {
                    val a: Int = 0
                    object B {
                        val b : Int = 0
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when same name used in object and nested object`() {
            val code = """
                object A {
                    val a: Int = 0
                    object B {
                        // A.a access
                        val a : Int = 0
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when different name used in object and nested object with class`() {
            val code = """
                class Abc(val a: Int, val b: Int) {
                    object A {
                        val a: Int = 0
                        object B {
                            val b : Int = 0
                        }
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `report shadowing variable with instance level prop`() {
            val code = """
                class A {
                    val i: Int = 0
                    fun test(i: Int) {
                        // this access
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report shadowing variable with instance level extension prop`() {
            val code = """
                class A {
                    val Int.i: Int
                        get() = this
                    fun test(i: Int) {
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report param from non inner class`() {
            val code = """
                class A(val a: Int, val b: Int, c: Int){
                    class B(val a: Int = 0) {
                        fun test(b: Int) {
                            listOf(1).map { c ->  }
                        }
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report param from inner class separated by non inner class`() {
            val code = """
                class A(val a: Int, val b: Int, c: Int) {
                    class B {
                        inner class C(val a: Int = 0) {
                            fun test(b: Int) {
                                listOf(1).map { c -> }
                            }
                        }
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report param from inner class`() {
            val code = """
                class A(val a: Int, val b: Int, val c: Int, val d: Int){
                    inner class B(val a: Int = 0) {
                        fun test(b: Int) {
                            // this access
                            listOf(1).map { c ->  }
                        }
                        init {
                            listOf(1).map { d ->  }
                        }
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report param from inner class when class doesn't use var or val`() {
            val code = """
                class A(a: Int, b: Int, c: Int, d: Int, e: Int) {
                    inner class B(val a: Int = 0) {
                        fun test(b: Int) {
                            listOf(1).map { c ->  }
                        }
                        init {
                            listOf(1).map { d -> print(d) }
                        }
                        val e: Int = 0
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report param from nested inner class`() {
            val code = """
                class A(val a: Int, val b: Int, val c: Int, val d: Int){
                    inner class B {
                        // this access
                        inner class C(val a: Int = 0) {
                            fun test(b: Int) {
                                listOf(1).map { c ->  }
                            }
                        }
                        init {
                            listOf(1).map { d ->  }
                        }
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report class inside function with different names`() {
            val code = """
                fun foo(a: Int, b: Int, c: Int, d: Int) {
                    class A(val a1: Int) {
                        fun bar(b1: Int) {
                            listOf(1).map { c1 -> }
                        }

                        init {
                            listOf(1).map { d1 -> }
                        }
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when anonymous object is used`() {
            val code = """
                fun test() {
                    object {
                        val a: Int = 0
                        init {
                            listOf<Int>().map { a ->
                                // this access
                            }
                        }
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }
}
