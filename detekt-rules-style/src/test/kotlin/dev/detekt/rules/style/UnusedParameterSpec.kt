package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.lint
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class UnusedParameterSpec {
    val subject = UnusedParameter(Config.empty)

    @Nested
    inner class `function parameters` {
        @Test
        fun `reports single parameters if they are unused in public function`() {
            val code = """
                fun function(unusedParameter: Int): Int {
                    return 5
                }
            """.trimIndent()

            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `does not report single parameters if they used in return statement in public function`() {
            val code = """
                fun function(used: Int): Int {
                    return used
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report single parameters if they used in public function`() {
            val code = """
                fun function(used: Int) {
                    println(used)
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `reports parameters that are unused in return statement in public function`() {
            val code = """
                fun function(unusedParameter: Int, usedParameter: Int): Int {
                    return usedParameter
                }
            """.trimIndent()

            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `reports parameters that are unused in public function`() {
            val code = """
                fun function(unusedParameter: Int, usedParameter: Int) {
                    println(usedParameter)
                }
            """.trimIndent()

            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `reports single parameters if they are unused in private function`() {
            val code = """
                class Test {
                    val value = usedMethod(1)
                
                    private fun usedMethod(unusedParameter: Int): Int {
                        return 5
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `reports two parameters if they are unused and called the same in different methods`() {
            val code = """
                class Test {
                    val value = usedMethod(1)
                    val value2 = usedMethod2(1)
                
                    private fun usedMethod(unusedParameter: Int): Int {
                        return 5
                    }
                
                    private fun usedMethod2(unusedParameter: Int): Int {
                        return 5
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).hasSize(2)
        }

        @Test
        fun `does not report single parameters if they used in return statement in private function`() {
            val code = """
                class Test {
                    val value = usedMethod(1)
                
                    private fun usedMethod(used: Int): Int {
                        return used
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report single parameters if they used in private function`() {
            val code = """
                class Test {
                    val value = usedMethod(1)
                
                    private fun usedMethod(used: Int) {
                        println(used)
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `reports parameters that are unused in return statement in private function`() {
            val code = """
                class Test {
                    val value = usedMethod(1, 2)
                
                    private fun usedMethod(unusedParameter: Int, usedParameter: Int): Int {
                        return usedParameter
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `reports parameters that are unused in private function`() {
            val code = """
                class Test {
                    val value = usedMethod(1, 2)
                
                    private fun usedMethod(unusedParameter: Int, usedParameter: Int) {
                        println(usedParameter)
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `does not report single parameters if they used in guard clause`() {
            val code = """
                fun function(used: Boolean) {
                    val a = '1'.digitToInt() + 1
                    val c = false
                    when (a) {
                        1 if used -> Unit
                        2 -> if (c) Unit else Unit
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `suppress unused parameter warning annotations` {
        @Test
        fun `does not report annotated parameters`() {
            val code = """
                fun foo(@Suppress("UnusedParameter") unused: String){}
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `reports parameters without annotation`() {
            val code = """
                fun foo(@Suppress("UnusedParameter") unused: String, unusedWithoutAnnotation: String){}
            """.trimIndent()

            val lint = subject.lint(code)

            assertThat(lint).singleElement()
                .hasMessage("Function parameter `unusedWithoutAnnotation` is unused.")
        }

        @Test
        fun `does not report parameters in annotated function`() {
            val code = """
                @Suppress("UnusedParameter")
                fun foo(unused: String, otherUnused: String){}
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report parameters in annotated class`() {
            val code = """
                @Suppress("UnusedParameter")
                class Test {
                    fun foo(unused: String, otherUnused: String){}
                    fun bar(unused: String){}
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report parameters in annotated object`() {
            val code = """
                @Suppress("UnusedParameter")
                object Test {
                    fun foo(unused: String){}
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report parameters in class with annotated outer class`() {
            val code = """
                @Suppress("UnusedParameter")
                class Test {
                    fun foo(unused: String){}
                
                    class InnerTest {
                        fun bar(unused: String){}
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `error messages` {
        @Test
        fun `are specific for function parameters`() {
            val code = """
                fun foo(unused: Int){}
            """.trimIndent()

            val lint = subject.lint(code)

            assertThat(lint).singleElement()
                .hasMessage("Function parameter `unused` is unused.")
        }
    }

    @Nested
    inner class `main methods` {

        @Test
        fun `does not report the args parameter of the main function inside an object`() {
            val code = """
                object O {
                
                    @JvmStatic
                    fun main(args: Array<String>) {
                        println("b")
                    }
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report the args parameter of the main function as top level function`() {
            val code = """
                fun main(args: Array<String>) {
                    println("b")
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `backtick identifiers - #5251` {
        @Test
        fun `does not report used backtick parameters`() {
            val code = """
                fun test(`foo bar`: Int) = `foo bar`
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `highlights declaration name - #4916` {

        @Test
        fun parameter() {
            val code = """
                class Test {
                    fun test(
                        /**
                         * kdoc
                         */
                        x: Int
                    ) = 1
                }
            """.trimIndent()
            assertThat(subject.lint(code)).singleElement()
                .hasStartSourceLocation(6, 9)
        }
    }

    @Nested
    inner class `parameter with the same name as a named argument #5373` {
        @Test
        fun `unused parameter`() {
            val code = """
                fun foo(modifier: Int) {
                    bar(modifier = 1)
                }
                
                fun bar(modifier: Int) {
                    println(modifier)
                }
            """.trimIndent()
            assertThat(subject.lint(code)).singleElement()
                .hasStartSourceLocation(1, 9)
        }

        @Test
        fun `used parameter`() {
            val code = """
                fun foo(modifier: Int) {
                    bar(modifier = modifier)
                }
                
                fun bar(modifier: Int) {
                    println(modifier)
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `constructor parameters` {
        @Test
        fun `reports unused parameter of a primary constructor`() {
            val code = """
                class A(unused: String)
            """.trimIndent()

            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `reports unused parameter of a secondary constructor`() {
            val code = """
                class A {
                    constructor(unused: String)
                }
            """.trimIndent()

            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `does not report a used parameter of a primary constructor`() {
            val code = """
                class A(used: String) {
                    val length = used.length
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `reports unused parameter`() {
            val code = """
                class Test(unused: Any)
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `does not report used parameter in init block`() {
            val code = """
                class Test(used: Any) {
                    init {
                        used.toString()
                    }
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report used parameter to initialize property`() {
            val code = """
                class Test(used: Any) {
                    val usedString = used.toString()
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `report unused parameters in secondary constructors`() {
            val code = """
                private class ClassWithSecondaryConstructor {
                    constructor(used: Any, unused: Any) {
                        used.toString()
                    }
                
                    constructor(used: Any)
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(2)
        }

        @Test
        fun `reports unused vararg parameter`() {
            val code = """
               class Test(vararg unused: Any)
            """.trimIndent()

            assertThat(subject.lint(code))
                .hasSize(1)
        }

        @Test
        fun `reports for multiple classes with same properties name in the same file`() {
            val code = """
                class C(initial:Int = 0){
                
                constructor(c:Float, d:Float):this(c.toInt()){
                   println(c)
                }
                  
               }
            """.trimIndent()

            assertThat(subject.lint(code)).isNotEmpty()
        }

        @Test
        fun `does not report unused parameters of a data class secondary constructor`() {
            val code = """
                data class A(val x: Int) {
                    constructor(x: Int, unused: Int) : this(x)
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report unused parameters of a value class constructor`() {
            val code = """
                @JvmInline
                value class A(private val x: Int) {
                    constructor(x: Int, unused: Int) : this(x)
                }
            """.trimIndent()

            assertThat(subject.lint(code, compile = false)).isEmpty()
        }

        @Test
        fun `does not report unused parameters of an inline class constructor`() {
            val code = """
                inline class A(private val x: Int) {
                    constructor(x: Int, unused: Int) : this(x)
                }
            """.trimIndent()

            assertThat(subject.lint(code, compile = false)).isEmpty()
        }

        @Test
        fun `does not report a constructor property`() {
            val code = """
                class A(val unused: String)
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `actual functions and classes` {

        @Test
        fun `should not report unused parameters in actual functions`() {
            val code = """
                actual class Foo {
                    actual fun bar(i: Int) {}
                    actual fun baz(i: Int, s: String) {}
                }
            """.trimIndent()
            assertThat(subject.lint(code, compile = false)).isEmpty()
        }

        @Test
        fun `should not report unused parameters in actual constructors`() {
            val code = """
                actual class Foo actual constructor(bar: String) {}
            """.trimIndent()
            assertThat(subject.lint(code, compile = false)).isEmpty()
        }
    }
}
