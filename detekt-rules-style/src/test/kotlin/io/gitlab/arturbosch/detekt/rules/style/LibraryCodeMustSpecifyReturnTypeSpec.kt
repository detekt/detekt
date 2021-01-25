package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import io.gitlab.arturbosch.detekt.test.lintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

internal class LibraryCodeMustSpecifyReturnTypeSpec : Spek({

    setupKotlinEnvironment()
    val env: KotlinCoreEnvironment by memoized()

    describe("library code must have explicit return types") {

        it("should not report without explicit filters set") {
            val subject = LibraryCodeMustSpecifyReturnType(TestConfig(Config.EXCLUDES_KEY to "**"))
            assertThat(subject.compileAndLintWithContext(env, """
                fun foo() = 5
                val bar = 5
                class A {
                    fun b() = 2
                    val c = 2
                }
            """)).isEmpty()
        }

        val subject by memoized {
            LibraryCodeMustSpecifyReturnType()
        }

        describe("positive cases") {

            it("should report a top level function") {
                assertThat(subject.compileAndLintWithContext(env, """
                    fun foo() = 5
                """)).hasSize(1)
            }

            it("should report a top level property") {
                assertThat(subject.compileAndLintWithContext(env, """
                    val foo = 5
                """)).hasSize(1)
            }

            it("should report a public class with public members") {
                assertThat(subject.compileAndLintWithContext(env, """
                    class A {
                        val foo = 5
                        fun bar() = 5
                    }
                """)).hasSize(2)
            }

            it("should report a public class with protected members") {
                assertThat(subject.compileAndLintWithContext(env, """
                    open class A {
                        protected val foo = 5
                        protected fun bar() = 5
                    }
                """)).hasSize(2)
            }
        }

        describe("negative cases with public scope") {

            it("should not report a top level function") {
                assertThat(subject.compileAndLintWithContext(env, """
                    fun foo(): Int = 5
                """)).isEmpty()
            }

            it("should not report a non expression function") {
                assertThat(subject.compileAndLintWithContext(env, """
                    fun foo() {}
                """)).isEmpty()
            }

            it("should not report a top level property") {
                assertThat(subject.compileAndLintWithContext(env, """
                    val foo: Int = 5
                """)).isEmpty()
            }

            it("should not report a public class with public members") {
                assertThat(subject.compileAndLintWithContext(env, """
                    class A {
                        val foo: Int = 5
                        fun bar(): Int = 5
                    }
                """)).isEmpty()
            }
        }
        describe("negative cases with no public scope") {

            it("should not report a private top level function") {
                assertThat(subject.lintWithContext(env, """
                    internal fun bar() = 5
                    private fun foo() = 5
                """)).isEmpty()
            }

            it("should not report a internal top level property") {
                assertThat(subject.compileAndLintWithContext(env, """
                    internal val foo = 5
                """)).isEmpty()
            }

            it("should not report members and local variables") {
                assertThat(subject.compileAndLintWithContext(env, """
                    internal class A {
                        internal val foo = 5
                        private fun bar() {
                            fun stuff() = Unit
                            val a = 5
                        }
                    }
                """)).isEmpty()
            }

            it("should not report effectively private properties and functions") {
                assertThat(subject.compileAndLintWithContext(env, """
                    internal class A {
                        fun baz() = 5
                        val qux = 5
                    }
                """)).isEmpty()
            }
        }
    }
})
