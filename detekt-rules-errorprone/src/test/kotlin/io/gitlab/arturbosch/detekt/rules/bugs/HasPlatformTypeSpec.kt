package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class HasPlatformTypeSpec(private val env: KotlinCoreEnvironment) {
    private val subject = HasPlatformType(Config.empty)

    @Test
    fun `reports when public function returns expression of platform type`() {
        val code = """
            class Person {
                fun apiCall() = System.getProperty("propertyName")
            }
        """
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report function when private`() {
        val code = """
            class Person {
                private fun apiCall() = System.getProperty("propertyName")
            }
        """
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report when public function returns expression of platform type and type explicitly declared`() {
        val code = """
            class Person {
                fun apiCall(): String = System.getProperty("propertyName")
            }
        """
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports when property initiated with platform type`() {
        val code = """
            class Person {
                val name = System.getProperty("name")
            }
        """
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report property when private`() {
        val code = """
            class Person {
                private val name = System.getProperty("name")
            }
        """
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report when property initiated with platform type and type explicitly declared`() {
        val code = """
            class Person {
                val name: String = System.getProperty("name")
            }
        """
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does report when a platform type is used directly as a non-nullable function argument`() {
        val code = """
            import java.net.URLEncoder.encode

            class Foo {
                private fun doFoo(a: String) {
                    println(a)
                }
                
                fun foo() {
                    doFoo(System.getProperty("foo"))
                    doFoo(encode("foo", "UTF-8"))
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(2)
    }

    @Test
    fun `does report when a platform type is used for a non-nullable vararg function argument`() {
        val code = """
            import java.net.URLEncoder.encode

            class Foo {
                private fun doFoo(vararg aArgs: String) {
                    aArgs.forEach(::println)
                }
                
                fun foo() {
                    doFoo("SomeArg", System.getProperty("foo"), encode("foo", "UTF-8"))
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(2)
    }

    @Test
    fun `does not report when a platform type is used directly as a nullable function argument`() {
        val code = """
            class Foo {
                private fun doFoo(a: String?) {
                    if (a != null) println(a) else println("'a' is null")
                }
                
                fun foo() {
                    doFoo(System.getProperty("foo"))
                    doFoo(encode("foo", "UTF-8"))
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report when a de-nullified platform type is used directly as a function argument`() {
        val code = """
            import java.net.URLEncoder.encode

            class Foo {
                private fun doFoo(a: String) {
                    println(a)
                }
                
                fun foo() {
                    doFoo(System.getProperty("foo")!!)
                    doFoo(encode("foo", "UTF-8") ?: "foo")
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does report when the platform type is used in a call chain`() {
        val code = """
            import java.net.URLEncoder.encode

            class Person {
                val idLength: String = System.getProperty("id").plus("-PERSON")
                val urlLength: Int = encode("url", "UTF-8").length
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(2)
    }

    @Test
    fun `does not report when the de-nullified platform type is used in a call chain`() {
        val code = """
            import java.net.URLEncoder.encode

            class Person {
                val idLength: Int = System.getProperty("id")!!.plus("-PERSON")
                val urlLength: Int? = encode("url", "UTF-8")?.length
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }
}
