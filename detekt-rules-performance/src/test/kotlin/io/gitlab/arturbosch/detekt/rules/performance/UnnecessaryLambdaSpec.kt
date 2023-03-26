package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import io.gitlab.arturbosch.detekt.test.lintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

// Testcases taken from https://github.com/JetBrains/intellij-community/tree/master/plugins/kotlin/idea/tests/testData/intentions/convertLambdaToReference
@KotlinCoreEnvironmentTest
class UnnecessaryLambdaSpec(private val env: KotlinCoreEnvironment) {
    private val subject = UnnecessaryLambda()

    @Test
    fun `does not report when new lambda performs additional operation before calling passed lambda`() {
        val code = """
            import kotlin.concurrent.thread

            fun test(lambda: () -> Unit) {
                thread {
                    print("Before lambda invoked")
                    lambda()
                }.start()
            }
        """.trimIndent()

        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report when new lambda performs additional operation after calling passed lambda`() {
        val code = """
            import kotlin.concurrent.thread

            fun test(lambda: () -> Unit) {
                thread {
                    lambda()
                    print("after lambda invoked")
                }.start()
            }
        """.trimIndent()

        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report when new lambda is empty`() {
        val code = """
            import kotlin.concurrent.thread

            fun test() {
                thread {
                    // no-op
                }.start()
            }
        """.trimIndent()

        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report when lambda is passed`() {
        val code = """
            import kotlin.concurrent.thread

            fun test(lambda: () -> Unit) {
                thread(block = lambda).start()
            }
        """.trimIndent()

        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report when passed lambda param is not used directly`() {
        val code = """
            class A(s: String) {
                fun bar(s: String) {}
            }

            fun foo(f: (String) -> Unit) {}

            fun test() {
                foo { s -> s.let(::A).bar(s) }
            }
        """.trimIndent()

        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report when super method is called`() {
        val code = """
            open class A {
                open fun method() {}
            }

            class B : A() {
                override fun method() {
                    // reference to super call not supported
                    // https://youtrack.jetbrains.com/issue/KT-11520/Allow-callable-references-to-super-members
                    call { super.method() }
                }
            }

            fun call(f: () -> Unit) {
                f()
            }
        """.trimIndent()

        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report when lambda is passed with additional parameter as well`() {
        val code = """
            inline fun test(lambda: (Int) -> Unit) {
                repeat(3, lambda)
            }
        """.trimIndent()

        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report when available lambda type differs`() {
        val code = """
            import kotlin.concurrent.thread

            fun test(lambda: (Int) -> Unit) {
                thread {
                    lambda(1)
                }.start()
            }
        """.trimIndent()

        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does report when new lambda just calls passed lambda`() {
        val code = """
            import kotlin.concurrent.thread

            fun test(lambda: () -> Unit) {
                thread {
                    lambda()
                }.start()
            }
        """.trimIndent()

        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `does report when new lambda just calls passed lambda inside parenthesis`() {
        val code = """
            import kotlin.concurrent.thread

            fun test(lambda: () -> Unit) {
                thread {
                    (lambda())
                }.start()
            }
        """.trimIndent()

        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `does not report when new lambda calls passed lambda class method inside parenthesis`() {
        val code = """
            import kotlin.concurrent.thread

            fun test(lambda: () -> Unit) {
                thread {
                    (lambda().toString())
                }.start()
            }
        """.trimIndent()

        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Nested
    inner class WithTopLevel {
        @Nested
        inner class ExtensionFunction {
            @Test
            fun `does report when new lambda just calls method instead of using method ref`() {
                val code = """
                    fun Int.toDomain() = this.toString()

                    fun test() {
                        listOf(1).map { it.toDomain() }
                    }
                """.trimIndent()

                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }

            @Test
            fun `does not report when new lambda just calls method with a constant`() {
                val code = """
                    fun Int.toDomain() = this.toString()

                    fun test() {
                        listOf(1).map { 5.toDomain() }
                    }
                """.trimIndent()

                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).isEmpty()
            }

            @Test
            fun `does not report when new lambda paramtere is staisfied with a variable`() {
                val code = """
                    fun Int.toDomain() = this.toString()

                    fun test() {
                        val a = 9
                        listOf(1).map { a.toDomain() }
                    }
                """.trimIndent()

                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).isEmpty()
            }

            @Test
            fun `does not report when method ref is passed directly`() {
                val code = """
                    fun Int.toDomain() = this.toString()

                    fun test() {
                        listOf(1).map(Int::toDomain)
                    }
                """.trimIndent()

                val findings = subject.lintWithContext(env, code)
                assertThat(findings).isEmpty()
            }
        }

        @Nested
        inner class ParameterFunction {
            @Test
            fun `does report when new lambda just calls method`() {
                val code = """
                    fun toDomain(i: Int) = i.toString()

                    fun test() {
                        listOf(1).map { toDomain(it) }
                    }
                """.trimIndent()

                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }

            @Test
            fun `does not report when method ref is passed directly`() {
                val code = """
                    fun toDomain(i: Int) = i.toString()

                    fun test() {
                        listOf(1).map(::toDomain)
                    }
                """.trimIndent()

                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).isEmpty()
            }

            @Test
            fun `does not report when using callable reference with two parameter`() {
                val code = """
                    fun add(a: Int, b: Int) = a + b

                    fun main() {
                       val f: (Int, Int) -> Int = ::add
                       println(f(10, 20))
                    }
                """.trimIndent()

                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).isEmpty()
            }

            @Test
            fun `does report when using add is called inside a lambda with two parameter`() {
                val code = """
                    fun add(a: Int, b: Int) = a + b

                    fun main() {
                       val f: (Int, Int) -> Int = {a, b ->
                            add(a, b)
                        }
                       println(f(10, 20))
                    }
                """.trimIndent()

                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }

            @Test
            fun `does report with lambda is called with type parameters`() {
                val code = """
                    fun <M> test(block: (M) -> Unit) {
                        println("invoked")
                    }

                    fun <M>test2(block: (M) -> Unit) {
                        test<M> { 
                            block(it)
                        }
                    }
                """.trimIndent()
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }

            @Test
            fun `does not report when parameter lambda has extension and called function signature doesn't have it`() {
                val code = """
                    fun callMe(s: String) {
                        /*no-op*/
                    }

                    fun body(receiver: String.(String) -> Unit) {
                        /*no-op*/
                    }

                    fun usage() {
                        body { callMe(it) }
                    }
                """.trimIndent()
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).isEmpty()
            }

            @Test
            fun `does report when function name is _super_`() {
                val code = """
                    fun `super`(x: Int): Int = TODO()

                    fun main(args: Array<String>) {
                        listOf(1).map { `super`(it) }
                    }
                """.trimIndent()
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }

            @Test
            fun `does report when parameter lambda has extension and called function signature also have it`() {
                val code = """
                    fun String.callMe(s: String) {
                        println(this)
                    }

                    fun body(receiver: String.(String) -> Unit) {
                        /*no-op*/
                    }

                    fun usage() {
                        body {
                            callMe(it)
                        }
                    }
                """.trimIndent()
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }

            @Test
            fun `does not report when parameter lambda has extension and is not called with _it_`() {
                val code = """
                    fun String.callMe(s: String) {
                        println(this)
                    }

                    fun body(receiver: String.(String) -> Unit) {
                        /*no-op*/
                    }

                    fun usage() {
                        body {
                            callMe("passed string values")
                        }
                    }
                """.trimIndent()
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).isEmpty()
            }

            @Test
            fun `does report when created lambda calls function with default`() {
                val code = """
                    fun foo(f: () -> Unit) {}

                    fun bar(a: Int = 42) {}

                    fun test() {
                        foo { bar() }
                    }
                """.trimIndent()
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }

            @Test
            fun `does report when created lambda calls function with default and extension`() {
                val code = """
                    fun Int.foo(x: Int, y: Int = 42) = x + y

                    fun bar(f: (Int, Int) -> Int) {}

                    fun test() {
                        bar { i, x -> i.foo(x) }
                    }
                """.trimIndent()

                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }

            @Test
            fun `does not report when created lambda calls function with default and extension with fixed param`() {
                val code = """
                    fun Int.foo(x: Int, y: Int = 42) = x + y

                    fun bar(f: (Int, Int) -> Int) {}

                    fun test() {
                        bar { i, x -> i.foo(9) }
                    }
                """.trimIndent()

                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).isEmpty()
            }
        }

        @Test
        fun `does report when method with one extension and one input param is called outside the class`() {
            val code = """
                data class Seed(val real: String)

                fun Seed.hash(other: Seed): Seed =
                    Seed(real + other.real)

                fun test() {
                    listOf(Seed("seed1"), Seed("seed2")).zipWithNext { a, b -> 
                        a.hash(b)
                    }
                }
            """.trimIndent()

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does not report when method reference with one extension and one input param is used outside the class`() {
            val code = """
                data class Seed(val real: String)

                fun Seed.hash(other: Seed): Seed =
                    Seed(real + other.real)

                fun test() {
                    listOf(Seed("seed1"), Seed("seed2")).zipWithNext(Seed::hash)
                }
            """.trimIndent()

            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when lambda calls generic method with non generic method present`() {
            val code = """
                fun main() {
                    Any().bar { it.remove<Int>() }
                }

                fun Any.bar(action: (Any) -> Unit) {
                    TODO()
                }

                fun <T> Any.remove(): Int = TODO()
                fun Any.remove(): Unit = TODO()
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when called method is reference via package`() {
            val code = """
                package com.example

                class MyClass(val value: Int)

                fun foo(x: Int) = MyClass(1)

                fun f(body: (Int) -> com.example.MyClass) {}

                fun test() {
                    f { i -> com.example.foo(i) } // reference with package doesn't work
                }
            """.trimIndent()

            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does report when lambda calls overloaded method where type can be fixed`() {
            val code = """
                fun overloadFun(p: Int, q: Long) {}
                fun overloadFun(p: String, q: Long) {}

                fun <T, U> foo(fn: (T, U) -> Unit) {}

                fun test() {
                    foo {x: String, y: Long -> overloadFun(x, y) }
                }
            """.trimIndent()

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does report when lambda calls overloaded fun where type can be fixed in vararg parent fun`() {
            val code = """
                fun overloadFun(p: Int) {}
                fun overloadFun(p: String) {}

                fun <T> ambiguityFun(vararg fn: (T) -> Unit) {}

                fun overloadContext() {
                    ambiguityFun({ x: String -> overloadFun(x) }, ::overloadFun)
                }
            """.trimIndent()

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }
    }

    @Nested
    inner class WithClassMethod {
        @Test
        fun `does report when new lambda just calls instance method with a constant`() {
            val code = """
                fun test() {
                    listOf(1).map { 5.times(it) }
                }
            """.trimIndent()

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does report when new lambda just calls instance method with a variable`() {
            val code = """
                fun test() {
                    val a = 9
                    listOf(1).map { a.times(it) }
                }
            """.trimIndent()

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does report when class member method is called inside the class`() {
            val code = """
                class C {
                    private fun toDomain(i: Int) = i.toString()

                    fun test() {
                        listOf(1).map {
                            toDomain(it)
                        }
                    }
                }
            """.trimIndent()

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does report when companion member method is called inside the class`() {
            val code = """
                class Foo {
                    companion object {
                        fun create(x: String): Foo = Foo()
                    }
                }

                fun main(args: Array<String>) {
                    listOf("a").map { Foo.create(it) }
                }
            """.trimIndent()

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does report when class member method is called using _with_`() {
            val code = """
                class C {
                    fun toDomain(i: Int) = i.toString()
                }

                fun test() {
                    with(C()) {
                        listOf(1).map {
                            toDomain(it)
                        }   
                    }
                }
            """.trimIndent()

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does report when class member method is called outside the class`() {
            val code = """
                class C {
                    fun toDomain() = System.currentTimeMillis().toString()
                }

                fun test() {
                    listOf(C()).map {
                        it.toDomain()
                    }
                }
            """.trimIndent()

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does not report when class member method reference is used outside the class`() {
            val code = """
                class C {
                    fun toDomain() = System.currentTimeMillis().toString()
                }

                fun test() {
                    listOf(C()).map(C::toDomain)
                }
            """.trimIndent()

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when class member method is called outside the class with type mismatch`() {
            val code = """
                class C {
                    fun toDomain() = System.currentTimeMillis().toString()
                }

                fun test() {
                    val c = C()
                    listOf(1).map {
                        c.toDomain()
                    }
                }
            """.trimIndent()

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does report when class member method is called outside the class with class variable`() {
            val code = """
                class C {
                    fun toDomain(i: Int) = System.currentTimeMillis().toString()
                }

                fun test() {
                    val c = C()
                    listOf(1).map {
                        c.toDomain(it)
                    }
                }
            """.trimIndent()

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does report when class member method is called outside the class with new class creation`() {
            val code = """
                class C {
                    fun toDomain(i: Int) = System.currentTimeMillis().toString()
                }

                fun test() {
                    listOf(1).map {
                        C().toDomain(it)
                    }
                }
            """.trimIndent()

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does not report when class member extension method is called`() {
            val code = """
                class C {
                    private fun Int.toDomain() = this.toString()

                    private fun test() {
                        listOf(1).map {
                            it.toDomain()
                        }
                    }
                }
            """.trimIndent()

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does report on lambda argument for extension function parameter`() {
            val code = """
                package p

                class A {
                    fun f1() {
                    }
                }

                fun myA(func: A.() -> Unit) {
                    A().func()
                }

                fun main() {
                    myA {
                        f1()
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does report on lambda argument for extension function parameter called with this dispatcher`() {
            val code = """
                package p

                class A {
                    fun f1() {
                    }
                }

                fun myA(func: A.() -> Unit) {
                    A().func()
                }

                fun main() {
                    myA {
                        this.f1()
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does report when lambda calls class method on instance via chained call`() {
            val code = """
                interface Data
                val data: Data? = null

                class Recycler(val adapter: DataAdapter)
                val recycler = Recycler(DataAdapter())

                class DataAdapter {
                    fun newData(data: Data) {}
                }

                fun test() {
                    data?.let { recycler.adapter.newData(it) }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does not report when lambda calls reified method in a class`() {
            val code = """
                class Foo {
                    inline fun <reified T: Any> bar(): String? = T::class.simpleName
                }

                fun test(list: List<Foo>) {
                    list.forEach { it.bar<Int>() }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when lambda calls reified method in a class with non reified method is present`() {
            val code = """
                class Foo {
                    inline fun <reified T: Any> bar(): String? = T::class.simpleName
                    fun bar() {}
                }

                fun test(list: List<Foo>) {
                    list.forEach { it.bar<Int>() } // list.forEach(::bar) calls second method
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does report when created lambda call invoke operator`() {
            val code = """
                fun myInvoke(f: () -> Unit) = f()

                class InvokeContainer {
                    operator fun invoke() {}
                }

                fun test(k: InvokeContainer) {
                    myInvoke { k() }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does not report when invoke operator reference is used`() {
            val code = """
                fun myInvoke(f: () -> Unit) = f()

                class InvokeContainer {
                    operator fun invoke() {}
                }

                fun test(k: InvokeContainer) {
                    myInvoke(k::invoke)
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does report when created lambda call invoke operator in a instance variable is used`() {
            val code = """
                fun myInvoke(f: () -> Unit) = f()

                class InvokeContainer {
                    operator fun invoke() {}
                }

                class C(val k: InvokeContainer)
                
                fun test(c: C) {
                    myInvoke { c.k() }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does not report when method called in lambda has vararg param but single int is passed`() {
            val code = """
                fun test(i: Int?): IntArray? {
                    return i?.let { intArrayOf(it) }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when method called in lambda has vararg param`() {
            val code = """
                fun test(intArray: IntArray?): IntArray? {
                    return intArray?.let { intArrayOf(*it) }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }
    }

    @Nested
    inner class WithConstructor {
        @Test
        fun `does report when class class constructor with out parameter is called`() {
            val code = """
                class C

                fun create(factory: () -> C) {
                    val x: C = factory()
                }

                fun test() {
                    create { C() }
                }
            """.trimIndent()

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does report when class class constructor with parameter is called`() {
            val code = """
                class C(val i: Int)

                fun test() {
                    listOf(1).map {
                        C(it)
                    }
                }
            """.trimIndent()

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does report when class inner class constructor is called`() {
            val code = """
                class A {
                    class B {}
                }
                fun foo(x: () -> A.B) {}

                fun main() {
                    foo { A.B() }
                }
            """.trimIndent()

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does report when class constructor is called reference via package`() {
            val code = """
                package com.example

                class MyClass(val value: Int)

                fun f(body: (Int) -> com.example.MyClass) {}

                fun test() {
                    f { i -> com.example.MyClass(i) }
                }
            """.trimIndent()

            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class WithVariable {
        @Test
        fun `does report when new variable assignment creates lambda with method call`() {
            val code = """
                val a: (Int) -> String = { it.toString() }
                val isInt: (Any) -> Boolean = { a -> Int::class.isInstance(a) }
            """.trimIndent()

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(2)
        }

        @Test
        fun `does not report when new variable assignment uses method ref`() {
            val code = """
                val a: (Int) -> String = Int::toString
            """.trimIndent()

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when new variable assignment creates object with method call`() {
            val code = """
                val a: (Int) -> String = object : (Int) -> String {
                    override fun invoke(i: Int): String = i.toString()
                }
            """.trimIndent()

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when lambda returns top level property`() {
            val code = """
                val name = "Kotlin"
                val x = { obj: Any -> name }
            """.trimIndent()

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does report when new variable creates a lambda to call nested class method`() {
            val code = """
                class Foo {
                    class Bar {
                        fun foo() {}
                    }
                }

                class Bar {
                    fun foo() {}
                }

                fun use() {
                    val f: (Foo.Bar) -> Unit = { it.foo() }
                }
            """.trimIndent()

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does report when new variable creates a lambda to call companion method`() {
            val code = """
                class C {
                    companion object {
                        fun foo(s: String) = 1
                    }
                    val f = { s: String -> foo(s) }
                }
            """.trimIndent()

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does report when new variable creates a lambda to call companion method in different class`() {
            val code = """
                class C {
                    companion object {
                        fun foo(s: String) = 1
                    }
                }

                class Test {
                    val f = { s: String -> C.foo(s) }
                }
            """.trimIndent()

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does not report when type is not clear due to overloads`() {
            val code = """
                fun foo(z: Int, y: Int = 0) = y + z

                val x = { arg: Int -> foo(arg) }
            """.trimIndent()

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does report when using named parameter with lambda params passed to correct fun params`() {
            val code = """
                fun foo(x: Int, y: Int) = x + y

                val x = { y: Int, x: Int -> foo(y = x, x = y) }
            """.trimIndent()

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does not report when using named parameter with lambda params passed to different fun params`() {
            val code = """
                fun foo(x: Int, y: Int) = x + y

                val x = { x: Int, y: Int -> foo(y = x, x = y) }
            """.trimIndent()

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when type is not clear due to generics in a class`() {
            val code = """
                class Generic<T : Any> {
                    val y = { arg: T -> arg.hashCode() }
                }
            """.trimIndent()

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when type is not clear due to generics in a method`() {
            val code = """
                fun <T> id(y: T) = y

                val x = { arg: Int -> id(arg) }
            """.trimIndent()

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class WithSuspension {
        @Test
        fun `does not report when suspend function is called in non-suspending inline method`() {
            val code = """
                suspend fun String.bar() {

                }

                suspend fun x() {
                    listOf("Jack", "Tom").forEach { it.bar() }
                }
            """.trimIndent()

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does report when non-suspending function is called in suspending lambda`() {
            val code = """
                fun coroutine(block: suspend () -> Unit) {}

                suspend fun testAction(obj: Any, action: suspend (Any) -> Unit) {
                    action(action)
                }

                fun println(message: Any?) {}

                fun main() = coroutine {
                    testAction("OK") {
                        println(it)
                    }
                }
            """.trimIndent()

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does report when suspend function is called in suspend lambda`() {
            val code = """
                fun foo(a: suspend () -> Unit) {}

                suspend fun action() {}

                fun usage() {
                    foo { action() }
                }
            """.trimIndent()

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }
    }
}
