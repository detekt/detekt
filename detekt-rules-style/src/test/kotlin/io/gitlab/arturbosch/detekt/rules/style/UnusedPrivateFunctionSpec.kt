package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.SourceLocation
import dev.detekt.test.assertThat
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinCoreEnvironmentTest
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UnusedPrivateFunctionSpec(val env: KotlinEnvironmentContainer) {
    val subject = UnusedPrivateFunction(Config.empty)

    @Nested
    inner class `interface functions` {

        @Test
        fun `should not report parameters in interface functions`() {
            val code = """
                interface UserPlugin {
                    fun plug(application: Application)
                    fun unplug()
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code, allowCompilationErrors = true)).isEmpty()
        }
    }

    @Nested
    inner class `expect functions and classes` {

        @Test
        fun `should not report parameters in expect class functions`() {
            val code = """
                expect class Foo {
                    fun bar(i: Int)
                    fun baz(i: Int, s: String)
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code, allowCompilationErrors = true)).isEmpty()
        }

        @Test
        fun `should not report parameters in expect object functions`() {
            val code = """
                expect object Foo {
                    fun bar(i: Int)
                    fun baz(i: Int, s: String)
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code, allowCompilationErrors = true)).isEmpty()
        }

        @Test
        fun `should not report parameters in expect functions`() {
            val code = """
                expect fun bar(i: Int)
                expect fun baz(i: Int, s: String)
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code, allowCompilationErrors = true)).isEmpty()
        }

        @Test
        fun `should not report parameters in expect class with constructor`() {
            val code = """
                expect class Foo1(private val bar: String) {}
                expect class Foo2(bar: String) {}
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code, allowCompilationErrors = true)).isEmpty()
        }
    }

    @Nested
    inner class `external functions` {

        @Test
        fun `should not report parameters in external functions`() {
            val code = "external fun foo(bar: String)"
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }

    @Nested
    inner class `external classes` {

        @Test
        fun `should not report functions in external classes`() {
            val code = """
                external class Bugsnag {
                    companion object {
                        fun start(value: Int)
                        fun notify(error: String)
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code, allowCompilationErrors = true)).isEmpty()
        }
    }

    @Nested
    inner class `protected functions` {

        @Test
        fun `should not report parameters in protected functions`() {
            val code = """
                open class Foo {
                    protected fun fee(bar: String) {}
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }

    @Nested
    inner class `overridden functions` {

        @Test
        fun `should not report parameters in not private functions`() {
            val code = """
                override fun funA() {
                    objectA.resolve(valA, object : MyCallback {
                        override fun onResolveFailed(throwable: Throwable) {
                            errorMessage.visibility = View.VISIBLE
                        }
                    })
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code, allowCompilationErrors = true)).isEmpty()
        }

        @Test
        fun `should not report in overridden classes`() {
            val code = """
                abstract class Parent {
                    abstract fun abstractFun(arg: Any)
                    open fun openFun(arg: Any): Int = 0
                }
                
                class Child : Parent() {
                    override fun abstractFun(arg: Any) {
                        println(arg)
                    }
                
                    override fun openFun(arg: Any): Int {
                        println(arg)
                        return 1
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }

    @Nested
    inner class `non-private classes` {
        @Test
        fun `should not report internal classes`() {
            val code = """
                internal class IC // unused but internal
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }

    @Nested
    inner class `classes accessing constants from companion objects` {

        @Test
        fun `should not report used constants`() {
            val code = """
                class A {
                    companion object {
                        private const val MY_CONST = 42
                    }
                
                    fun a() {
                        println("My const = " + MY_CONST.toLong())
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }

    @Nested
    inner class `top level functions` {

        @Test
        fun `reports top-level unused functions`() {
            val code = """
                private fun unusedTopLevelFunction() = 5
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `does not report used top level functions`() {
            val code = """
                private fun calledFromMain() {}
                
                fun main(args: Array<String>) {
                    calledFromMain()
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }

    @Nested
    inner class `unused private functions` {
        @Test
        fun `does not report used private functions`() {
            val code = """
                class Test {
                    val value = usedMethod()
                
                    private fun usedMethod(): Int {
                        return 5
                    }
                }
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `reports unused private functions`() {
            val code = """
                class Test {
                    private fun unusedFunction(): Int {
                        return 5
                    }
                }
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `does not report function used in interface - #1613`() {
            val code = """
                interface Bar {
                    fun doSomething() {
                        doSomethingElse()
                    }
                }
                private fun doSomethingElse() {}
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code, allowCompilationErrors = true)).isEmpty()
        }
    }

    @Nested
    inner class `private functions only used by unused private functions` {

        @Test
        fun `reports the non called private function`() {
            val code = """
                class Test {
                    private fun unusedFunction() {
                        return someOtherUnusedFunction()
                    }
                
                    private fun someOtherUnusedFunction() {
                        println("Never used")
                    }
                }
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }
    }

    @Nested
    inner class `error messages` {

        @Test
        fun `are specific for private functions`() {
            val code = """
                class Test {
                    private fun unusedFunction(): Int {
                        return 5
                    }
                }
            """.trimIndent()

            val lint = subject.lintWithContext(env, code)

            assertThat(lint.first().message).startsWith("Private function")
        }
    }

    @Nested
    inner class `suppress unused function warning annotations` {
        @Test
        fun `does not report annotated private functions`() {
            val code = """
                @Suppress("UnusedPrivateFunction")
                private fun foo(): String = ""
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `reports private functions without annotation`() {
            val code = """
                private fun foo(): String = ""
            """.trimIndent()

            val findings = subject.lintWithContext(env, code)

            assertThat(findings).hasSize(1)
            assertThat(findings[0].message).isEqualTo("Private function `foo` is unused.")
        }

        @Test
        fun `does not report private functions in annotated class`() {
            val code = """
                @Suppress("UnusedPrivateFunction")
                class Test {
                    private fun foo(): String = ""
                }
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report private functions in class with annotated outer class`() {
            val code = """
                @Suppress("UnusedPrivateFunction")
                class Test {
                    private fun foo(): String = ""
                    private fun bar(): String = ""
                
                    class InnerTest {
                        private fun baz(): String = ""
                    }
                }
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }

    @Nested
    inner class Operators {

        @Test
        fun `does not report used plus operator - #1354`() {
            val code = """
                import java.util.Date
                class Foo {
                    val bla: Date = Date(System.currentTimeMillis()) + 300L
                    companion object {
                        private operator fun Date.plus(diff: Long): Date = Date(this.time + diff)
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Suppress("ClassName")
        @Nested
        inner class `containing invoke operator` {
            @Test
            fun `does not report when invoke operator is used - #4435`() {
                val code = """
                    object Test {
                        private operator fun invoke(i: Int): Int = i
                    
                        fun answer() = Test(1)
                    }
                    
                    val answer = Test.answer()
                """.trimIndent()
                assertThat(subject.lintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `does not report used invoke operator defined in companion`() {
                val code = """
                    class A {
                        companion object {
                            private operator fun invoke(i: Int): Int = i
                        }
                        val answer = A(1)
                    }
                """.trimIndent()
                assertThat(subject.lintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `does not report used invoke operator in file with instance`() {
                val code = """
                    class A
                    private operator fun A.invoke(i: Int): Int = i
                    fun answer() = A()(9)
                    val answer = answer()
                """.trimIndent()
                assertThat(subject.lintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `does not report used nullable dispatch receiver invoke operator in file`() {
                val code = """
                    class A
                    private operator fun A?.invoke(i: Int): Int = i
                    fun answer() = A()(9)
                    val answer = answer()
                """.trimIndent()
                assertThat(subject.lintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `does not report used invoke operator is used with child class dispatcher`() {
                val code = """
                    open class A
                    class B : A()
                    private operator fun A.invoke(i: Int): Int = i
                    val answer = B()(1)
                """.trimIndent()
                assertThat(subject.lintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `does report unused overloaded invoke operator`() {
                val code = """
                    open class A {
                        companion object {
                            private operator fun invoke(i: Int): Int = i
                            private operator fun invoke(i: Int, j: Int): Int = i
                        }
                        val answer = A(1, 1)
                    }
                """.trimIndent()
                assertThat(subject.lintWithContext(env, code))
                    .hasSize(1)
                    .hasStartSourceLocations(
                        SourceLocation(3, 30)
                    )
            }

            @Test
            fun `does report unused overloaded invoke operator with nullable int`() {
                val code = """
                    class A
                    private operator fun A.invoke(i: Int): Int = i
                    private operator fun A.invoke(i: Int?): Int = i ?: 0
                    fun answer() = A()(9)
                    val answer = answer()
                """.trimIndent()
                assertThat(subject.lintWithContext(env, code))
                    .hasSize(1)
                    .hasStartSourceLocations(
                        SourceLocation(3, 24)
                    )
            }

            @Test
            fun `does report unused overloaded invoke operator with non-null int`() {
                val code = """
                    class A
                    private operator fun A.invoke(i: Int): Int = i
                    private operator fun A.invoke(i: Int?): Int = i ?: 0
                    val nullableInt: Int? = if (System.currentTimeMillis() % 2 == 0L) 0 else null
                    fun answer() = A()(nullableInt)
                    val answer = answer()
                """.trimIndent()
                assertThat(subject.lintWithContext(env, code))
                    .hasSize(1)
                    .hasStartSourceLocations(
                        SourceLocation(2, 24)
                    )
            }

            @Test
            fun `does not report used invoke operator when both dispatch and extension is present`() {
                val code = """
                    class A
                    class B {
                        private operator fun A.invoke(i: Int): Int = i
                        val answer = A()(1)
                    }
                """.trimIndent()
                assertThat(subject.lintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `does not report used invoke operator in companion when both dispatch and extension is present`() {
                val code = """
                    class A
                    class B {
                        companion object {
                            private operator fun A.invoke(i: Int): Int = i
                        }
                        val answer = A()(1)
                    }
                """.trimIndent()
                assertThat(subject.lintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `does not report unused overridden invoke operator`() {
                val code = """
                    interface I {
                        operator fun invoke(): String
                    }
                    class A : I {
                        override operator fun invoke() = "A"
                    }
                """.trimIndent()
                assertThat(subject.lintWithContext(env, code)).isEmpty()
            }
        }

        @Test
        fun `does not report used operator methods when used with the equal sign`() {
            val code = """
                class Test {
                    fun f() {
                        var number: Int? = 0
                        number += 1
                    }
                    fun f2() {
                        var number: Int? = 0
                        number -= 1
                    }
                    fun f3() {
                        var number: Int? = 0
                        number *= 1
                    }
                    fun f4() {
                        var number: Int? = 0
                        number /= 1
                    }
                    fun f5() {
                        var number: Int? = 0
                        number %= 1
                    }
                    private operator fun Int?.plus(other: Int) = 1
                    private operator fun Int?.minus(other: Int) = 2
                    private operator fun Int?.times(other: Int) = 3
                    private operator fun Int?.div(other: Int) = 4
                    private operator fun Int?.rem(other: Int) = 5
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report 'contains' operator function that is used as 'in'`() {
            val code = """
                class C {
                    val isInside = "bar" in listOf("foo".toRegex())
                
                    private operator fun Iterable<Regex>.contains(a: String): Boolean {
                        return any { it.matches(a) }
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report 'contains' operator function that is used as '!in'`() {
            val code = """
                class C {
                    val isInside = "bar" !in listOf("foo".toRegex())
                
                    private operator fun Iterable<Regex>.contains(a: String): Boolean {
                        return any { it.matches(a) }
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `report unused minus operator`() {
            val code = """
                import java.util.Date
                class Foo {
                    companion object {
                        private operator fun Date.minus(diff: Long): Date = Date(this.time - diff)
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }
    }

    @Nested
    inner class `same named functions` {

        @Test
        fun `report it when the file has same named functions`() {
            val code = """
                class Test {
                    private fun f(): Int {
                        return 5
                    }
                }
                
                class Test2 {
                    private fun f(): Int {
                        return 5
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(2)
        }

        @Test
        fun `report it when the class has same named functions`() {
            val code = """
                class Test {
                    val value = f(1)
                
                    private fun f(): Int {
                        return 5
                    }
                
                    private fun f(num: Int): Int {
                        return num
                    }
                
                    private fun f(num: String): Int {
                        return num.toInt()
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(2)
        }

        @Test
        fun `report it when the class has same named extension functions`() {
            val code = """
                class Test {
                    val value = 1.f()
                
                    private fun f(): Int {
                        return 5
                    }
                
                    private fun Int.f(): Int {
                        return this
                    }
                
                    private fun String.f(): Int {
                        return toInt()
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(2)
        }
    }

    @Nested
    inner class `operator functions - #2579` {

        @Test
        fun `Does not report unused operators`() {
            val code = """
                class Test {
                    private operator fun Foo.plus(other: Foo): Foo = Foo(value + other.value)
                
                    inner class Foo(val value: Int) {
                        fun double(): Foo = this + this
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `Report unused operators`() {
            val code = """
                class Test {
                    private operator fun Foo.plus(other: Foo): Foo = Foo(value + other.value)
                    private operator fun Foo.minus(other: Foo): Foo = Foo(value - other.value)
                
                    inner class Foo(val value: Int) {
                        fun double(): Foo = this + this
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1).hasStartSourceLocations(
                SourceLocation(3, 30),
            )
        }
    }

    @Nested
    inner class `overloaded extension functions - #2579` {

        @Test
        fun `Does not report used private extension functions`() {
            val code = """
                class A
                class B
                class C(val elements: Set<B>, val flag: Boolean)
                
                class Test {
                    private fun A.someMethod(
                          param1: B,
                          param2: Boolean = true
                      ) = someMethod(setOf(param1), param2)
                
                    private fun A.someMethod(
                          param1: Set<B>,
                          param2: Boolean = true
                      ) = C(param1, param2)
                
                    fun main() {
                        val aInstance = A()
                        aInstance.someMethod(B(), true)
                        aInstance.someMethod(setOf(B(), B()), false)
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }

    @Nested
    inner class `getValue and setValue operator functions - #3128` {

        @Test
        fun `does not report used private getValue and setValue operator functions`() {
            val code = """
                import kotlin.reflect.KProperty
                
                class Test {
                    var delegated by "Hello"
                
                    private operator fun String.getValue(test: Test, prop: KProperty<*>): String {
                        return "working"
                    }
                
                    private operator fun String.setValue(test: Test, prop: KProperty<*>, value: String) {
                        error("setValue")
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report getValue and setValue operator function parameters`() {
            val code = """
                import kotlin.reflect.KProperty
                
                class SingleAssign<String> {
                
                    operator fun getValue(thisRef: Any?, property: KProperty<*>): kotlin.String {
                        return ""
                    }
                
                    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `reports unused private getValue and setValue operator functions`() {
            val code = """
                import kotlin.reflect.KProperty
                
                class Test {
                    private operator fun String.getValue(test: Test, prop: KProperty<*>): String {
                        return "working"
                    }
                
                    private operator fun String.setValue(test: Test, prop: KProperty<*>, value: String) {
                        error("setValue")
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(2)
        }
    }

    @Nested
    inner class `list get overloaded operator function - #3640` {
        @Test
        fun `report used private list get operator function - declared in a class - called by operator`() {
            val code = """
                class StringWrapper(
                    val s: String
                )
                
                class TestWrapper {
                    private operator fun List<StringWrapper>.get(s: String) =
                        this.firstOrNull { it.s == s }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `doesn't report used private list get operator function - declared in a class - called by operator`() {
            val code = """
                class StringWrapper(
                    val s: String
                )
                
                class TestWrapper(
                    private val strings: List<StringWrapper>
                ) {
                    fun getWrapperForString(s: String) = strings[s]
                
                    private operator fun List<StringWrapper>.get(s: String) =
                        this.firstOrNull { it.s == s }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `doesn't report used private list get operator function - declared in a class - called by operator - multiple parameters`() {
            val code = """
                class StringWrapper(
                    val s: String
                )
                
                class TestWrapper(
                    private val strings: List<StringWrapper>
                ) {
                    fun getWrapperForString(a: String, b: String) = strings[a, b]
                
                    private operator fun List<StringWrapper>.get(a: String, b: String) =
                        this.firstOrNull { it.s == b }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `doesn't report used private list get operator function - declared in a class - called directly`() {
            val code = """
                class StringWrapper(
                    val s: String
                )
                
                class TestWrapper(
                    private val strings: List<StringWrapper>
                ) {
                    fun getWrapperForString(s: String) = strings.get(s)
                
                    private operator fun List<StringWrapper>.get(s: String) =
                        this.firstOrNull { it.s == s }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `report used private list get operator function - declared in a file - called by operator`() {
            val code = """
                class StringWrapper(
                    val s: String
                )
                
                private operator fun List<StringWrapper>.get(s: String) =
                    this.firstOrNull { it.s == s }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `doesn't report used private list get operator function - declared in a file - called by operator`() {
            val code = """
                class StringWrapper(
                    val s: String
                )
                
                class Test(
                    private val strings: List<StringWrapper>
                ) {
                    fun getWrapperForString(s: String) = strings[s]
                }
                
                private operator fun List<StringWrapper>.get(s: String) =
                    this.firstOrNull { it.s == s }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `doesn't report used private list get operator function - declared in a file - called directly`() {
            val code = """
                class StringWrapper(
                    val s: String
                )
                
                class Test(
                    private val strings: List<StringWrapper>
                ) {
                    fun getWrapperForString(s: String) = strings.get(s)
                }
                
                private operator fun List<StringWrapper>.get(s: String) =
                    this.firstOrNull { it.s == s }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }

    @Nested
    inner class `highlights declaration name - #4916` {
        @Test
        fun function() {
            val code = """
                class Test {
                    /**
                     * kdoc
                     */
                    private fun foo() = 1
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).singleElement().hasStartSourceLocation(5, 17)
        }
    }
}
