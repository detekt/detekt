package io.gitlab.arturbosch.detekt.rules.bugs

import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object HasPlatformTypeSpec : Spek({
    setupKotlinEnvironment()

    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { HasPlatformType(Config.empty) }

    describe("Deprecation detection") {
        context("function initializer") {
            it("reports when public function returns expression of platform type") {
                val code = """
                    class Person {
                        fun apiCall() = System.getProperty("propertyName")
                    }
                    """
                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
            }

            it("does not report when private") {
                val code = """
                    class Person {
                        private fun apiCall() = System.getProperty("propertyName")
                    }
                    """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            it("does not report when public function returns expression of platform type and type explicitly declared") {
                val code = """
                    class Person {
                        fun apiCall(): String = System.getProperty("propertyName")
                    }
                    """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }
        }

        context("property initializer") {
            it("reports when property initiated with platform type") {
                val code = """
                class Person {
                    val name = System.getProperty("name")
                }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
            }

            it("does not report when private") {
                val code = """
                class Person {
                    private val name = System.getProperty("name")
                }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            it("does not report when property initiated with platform type and type explicitly declared") {
                val code = """
                class Person {
                    val name: String = System.getProperty("name")
                }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }
        }

        context("function argument") {
            it("does report when a platform type is used directly as a non-nullable function argument") {
                val code = """
                    import java.net.URLEncoder.encode

                    private fun doFoo(a: String) {
                        println(a)
                    }
                    
                    fun foo() {
                        doFoo(System.getProperty("foo"))
                        doFoo(encode("foo", "UTF-8"))
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(2)
            }

            it("does report when a platform type is used for a non-nullable vararg function argument") {
                val code = """
                    import java.net.URLEncoder.encode

                    private fun doFoo(vararg aArgs: String) {
                        aArgs.forEach(::println)
                    }
                    
                    fun foo() {
                        doFoo("SomeArg", System.getProperty("foo"), encode("foo", "UTF-8"))
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(2)
            }

            it("does not report when a platform type is used directly as a nullable function argument") {
                val code = """
                    private fun doFoo(a: String?) {
                        if (a != null) println(a) else println("'a' is null")
                    }
                    
                    fun foo() {
                        doFoo(System.getProperty("foo"))
                        doFoo(encode("foo", "UTF-8"))
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            it("does not report when a de-nullified platform type is used directly as a function argument") {
                val code = """
                    import java.net.URLEncoder.encode

                    private fun doFoo(a: String) {
                        println(a)
                    }
                    
                    fun foo() {
                        doFoo(System.getProperty("foo")!!)
                        doFoo(System.getProperty("foo")?.plus("bar"))
                        doFoo(encode("foo", "UTF-8") ?: "foo")
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }
        }

        context("call chain") {
            it("does report when the platform type is used in a call chain") {
                val code = """
                    import java.net.URLEncoder.encode

                    class Person {
                        val idLength: String = System.getProperty("id").plus("-PERSON")
                        val urlLength: Int = encode("url", "UTF-8").length
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(2)
            }

            it("does not report when the de-nullified platform type is used in a call chain") {
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
    }
})
