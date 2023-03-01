package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class FeatureEnvySpec(private val env: KotlinCoreEnvironment) {

    val subject = FeatureEnvy()

    @Test
    fun `detect feature envy in standard example of feature envy`() {
        val code = """
            data class ContactInfo(
                val city: String,
                val postalCode: String,
                val street: String,
                val number: String
            )
            
            class User(val contactInfo: ContactInfo) {

                val test = "TestString"
            
                fun prettyPrintAddress() {
                    val prettyAddress = buildString {
                        append(this@User.contactInfo.postalCode)
                        append(" ")
                        append(this@User.contactInfo.city)
                        append("\n")
                        append(this@User.contactInfo.street)
                        append(" ")
                        append(this@User.contactInfo.number)
                        append(" " + this@User.test)
                    }
                    println(this.test)
                    println(test)
                    println(prettyAddress)
                }
            
            }
        """.trimIndent()

        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `do not detect feature envy if atfd is too low`() {
        val code = """
            data class Rectangle(val width: Int, val height: Int)

            class RectangleUsageSite(val rectangle: Rectangle) {
                fun printArea() {
                    val area = rectangle.width * rectangle.height
                    println("The area is: \${'$'}{area}")
                }
            }
        """.trimIndent()

        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `no feature envy if class has no functions`() {
        val code = """
            data class Foo(val foo: Boolean)
            
            class Bar
        """.trimIndent()

        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `do not detect feature envy if enough foreign data providers are used`() {
        val code = """
            data class A(val a: String)
            data class B(val b: String)
            data class C(val c: String)
            
            class DataCombiner {
                fun combineDate(a: A, b: B, c: C): String = a.a + b.b + c.c
            }
            
        """.trimIndent()

        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(0)
    }

    @Test
    fun `do not detect feature envy if enough local variables are used`() {

        val code = """
            data class Foo(
                val foo1: String,
                val foo2: String,
                val foo3: String,
                val foo4: String,
            )
            
            class Bar(val foo: Foo) {

                val test1 = "TestString"
                val test2 = "TestString"
            
                fun doCalculationThatUsesLotsOfVariables(): String {
                    return foo.foo1 + foo.foo2 + foo.foo3 + foo.foo4 + test1 + test2
                }
            }
        """.trimIndent()

        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(0)
    }

    @Test
    fun `do not detect feature envy if enough local variables are used in expression block`() {

        val code = """
            data class Foo(
                val foo1: String,
                val foo2: String,
                val foo3: String,
                val foo4: String,
            )
            
            class Bar(val foo: Foo) {

                val test1 = "TestString"
                val test2 = "TestString"
            
                fun doCalculationThatUsesLotsOfVariables(): String = foo.foo1 + foo.foo2 + foo.foo3 + foo.foo4 + test1 + test2
            }
        """.trimIndent()

        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(0)
    }

    @Test
    fun `detect feature envy if not enough local variables are used`() {

        val code = """
            data class Foo(
                val foo1: String,
                val foo2: String,
                val foo3: String,
                val foo4: String,
            )
            
            class Bar(val foo: Foo) {

                val test1 = "TestString"
                val test2 = "TestString"
            
                fun doCalculationThatUsesLotsOfVariables(): String {
                    return foo.foo1 + foo.foo2 + foo.foo3 + foo.foo4 + test1
                }
            }
        """.trimIndent()

        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `detect feature envy if not enough local variables are used in expression block`() {

        val code = """
            data class Foo(
                val foo1: String,
                val foo2: String,
                val foo3: String,
                val foo4: String,
            )
            
            class Bar(val foo: Foo) {

                val test1 = "TestString"
                val test2 = "TestString"
            
                fun doCalculationThatUsesLotsOfVariables(): String =foo.foo1 + foo.foo2 + foo.foo3 + foo.foo4 + test1
            }
        """.trimIndent()

        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }
}
