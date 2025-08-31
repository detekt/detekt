package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.test.assertThat
import dev.detekt.test.lint
import org.junit.jupiter.api.Test

class ClassOrderingSpec {
    val subject = ClassOrdering(Config.empty)

    @Test
    fun `does not report when class contents are in expected order with property first`() {
        val code = """
            class InOrder(private val x: String) {
                val y = x
            
                init {
                    check(x == "yes")
                }
            
                constructor(z: Int): this(z.toString())
            
                fun returnX() = x
            
                companion object {
                    const val IMPORTANT_VALUE = 3
                }
            }
        """.trimIndent()

        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report when class contents are in expected order with class initializer first`() {
        val code = """
            class InOrder(private val x: String) {
                init {
                    check(x == "yes")
                }
            
                val y = x
            
                constructor(z: Int): this(z.toString())
            
                fun returnX() = x
            
                companion object {
                    const val IMPORTANT_VALUE = 3
                }
            }
        """.trimIndent()

        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report when class is empty with empty body`() {
        val code = """
            class InOrder {

            }
        """.trimIndent()

        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report when class is empty with out body`() {
        val code = """
            class InOrder
        """.trimIndent()

        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report when class has some order multiple properties`() {
        val code = """
            class InOrder {
                val y1 = 1
                val y2 = 2
                val y3 = 3
            }
        """.trimIndent()

        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `reports when class initializer block is out of order`() {
        val code = """
            class OutOfOrder(private val x: String) {
                val y = x
            
                constructor(z: Int): this(z.toString())
            
                init {
                    check(x == "yes")
                }
            
                fun returnX() = x
            
                companion object {
                    const val IMPORTANT_VALUE = 3
                }
            }
        """.trimIndent()

        val findings = subject.lint(code)
        assertThat(findings).singleElement()
            .hasMessage(
                "initializer blocks should be declared before secondary constructors."
            )
    }

    @Test
    fun `reports when secondary constructor is out of order`() {
        val code = """
            class OutOfOrder(private val x: String) {
                constructor(z: Int): this(z.toString())
            
                val y = x
            
                init {
                    check(x == "yes")
                }
            
                fun returnX() = x
            
                companion object {
                    const val IMPORTANT_VALUE = 3
                }
            }
        """.trimIndent()

        val findings = subject.lint(code)
        assertThat(findings).singleElement()
            .hasMessage(
                "secondary constructor should be declared after property declarations and initializer blocks."
            )
    }

    @Test
    fun `reports when method is out of order`() {
        val code = """
            class OutOfOrder(private val x: String) {
                fun returnX() = x
            
                val y = x
            
                init {
                    check(x == "yes")
                }
            
                constructor(z: Int): this(z.toString())
            
                companion object {
                    const val IMPORTANT_VALUE = 3
                }
            }
        """.trimIndent()

        val findings = subject.lint(code)
        assertThat(findings).singleElement()
            .hasMessage("method `returnX()` should be declared after secondary constructors.")
    }

    @Test
    fun `reports when companion object is out of order`() {
        val code = """
            class OutOfOrder(private val x: String) {
                val y = x
            
                init {
                    check(x == "yes")
                }
            
                constructor(z: Int): this(z.toString())
            
                companion object {
                    const val IMPORTANT_VALUE = 3
                }
            
                fun returnX() = x
            }
        """.trimIndent()

        val findings = subject.lint(code)
        assertThat(findings).singleElement()
            .hasMessage("method `returnX()` should be declared before companion object.")
    }

    @Test
    fun `does not report nested class order`() {
        val code = """
            class OutOfOrder(private val x: String) {
                val y = x
            
                init {
                    check(x == "yes")
                }
            
                constructor(z: Int): this(z.toString())
            
                class Nested {
                    fun foo() = 2
                }
            
                fun returnX() = x
            }
        """.trimIndent()

        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report anonymous object order`() {
        val code = """
            class OutOfOrder(private val x: String) {
                val y = x
            
                init {
                    check(x == "yes")
                }
            
                constructor(z: Int): this(z.toString())
            
                object AnonymousObject {
                    fun foo() = 2
                }
            
                fun returnX() = x
            }
        """.trimIndent()

        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `report all issues with interleaving nested class`() {
        val code = """
            class MultipleMisorders(private val x: String) {
                companion object {
                    const val IMPORTANT_VALUE = 3
                }
            
                class Nested { }
            
                fun returnX() = x
            
                class Nested2 { }
            
                constructor(z: Int): this(z.toString())
            
                class Nested3 { }
            
                val y = x
            }
        """.trimIndent()

        val findings = subject.lint(code)
        assertThat(findings).hasSize(3)
        assertThat(findings).element(0)
            .hasMessage("method `returnX()` should be declared before companion object.")
        assertThat(findings).element(1)
            .hasMessage("secondary constructor should be declared before companion object.")
        assertThat(findings).element(2)
            .hasMessage("property `y` should be declared before companion object.")
    }

    @Test
    fun `does report all issues in a class with multiple misorderings`() {
        val code = """
            class MultipleMisorders(private val x: String) {
                companion object {
                    const val IMPORTANT_VALUE = 3
                }
            
                fun returnX() = x
            
                constructor(z: Int): this(z.toString())
            
                val y = x
            }
        """.trimIndent()

        val findings = subject.lint(code)
        assertThat(findings).hasSize(3)
        assertThat(findings).element(0)
            .hasMessage("method `returnX()` should be declared before companion object.")
        assertThat(findings).element(1)
            .hasMessage("secondary constructor should be declared before companion object.")
        assertThat(findings).element(2)
            .hasMessage("property `y` should be declared before companion object.")
    }

    @Test
    fun `does report before issue when lowest sections comes after longest change`() {
        val code = """
            class SingleMisOrderAtFirst(private val x: String) {
                companion object {
                    const val IMPORTANT_VALUE = 3
                }

                val y = x

                init {
                    println(y)
                }

                constructor(z: Int): this(z.toString())


                fun returnX() = x
            }
        """.trimIndent()

        val findings = subject.lint(code)
        assertThat(findings).singleElement()
            .hasMessage("companion object should be declared after method declarations.")
    }

    @Test
    fun `#7407 does report before issue when there is not lower section above violating section`() {
        val code = """
            class ExampleClass {

                var var1 = 1

                fun bar() {
            
                }

                var var2 = 2

                init {
                    /* no-op */
                }
 
                companion object {
                    /* no-op */
                }
            }

        """.trimIndent()

        val findings = subject.lint(code)
        assertThat(findings).singleElement()
            .hasMessage("method `bar()` should be declared after property declarations and initializer blocks.")
    }
}
