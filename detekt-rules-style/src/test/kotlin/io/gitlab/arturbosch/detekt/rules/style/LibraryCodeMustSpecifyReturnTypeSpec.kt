package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.lint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

internal class LibraryCodeMustSpecifyReturnTypeSpec : Spek({

    describe("library code must have explicit return types") {

        it("should not report without explicit filters set") {
            assertThat(LibraryCodeMustSpecifyReturnType().compileAndLint("""
                fun foo() = 5
                val bar = 5
                class A {
                    fun b() = 2
                    val c = 2
                }
            """)).isEmpty()
        }

        val subject by memoized {
            LibraryCodeMustSpecifyReturnType(TestConfig(Config.INCLUDES_KEY to "*.kt"))
        }

        describe("positive cases") {

            it("should report a top level function") {
                assertThat(subject.compileAndLint("""
                    fun foo() = 5
                """)).hasSize(1)
            }

            it("should report a top level property") {
                assertThat(subject.compileAndLint("""
                    val foo = 5
                """)).hasSize(1)
            }

            it("should report a public class with public members") {
                assertThat(subject.compileAndLint("""
                    class A {
                        val foo = 5
                        fun bar() = 5
                    }
                """)).hasSize(2)
            }
        }

        describe("negative cases with public scope") {

            it("should not report a top level function") {
                assertThat(subject.compileAndLint("""
                    fun foo(): Int = 5
                """)).isEmpty()
            }

            it("should not report a non expression function") {
                assertThat(subject.compileAndLint("""
                    fun foo() {}
                """)).isEmpty()
            }

            it("should not report a top level property") {
                assertThat(subject.compileAndLint("""
                    val foo: Int = 5
                """)).isEmpty()
            }

            it("should not report a public class with public members") {
                assertThat(subject.compileAndLint("""
                    class A {
                        val foo: Int = 5
                        fun bar(): Int = 5
                    }
                """)).isEmpty()
            }
        }
        describe("negative cases with no public scope") {

            it("should not report a private top level function") {
                // Kotlin Script Engine reports wrongly local functions here
                assertThat(subject.lint("""
                    internal fun bar() = 5
                    private fun foo() = 5
                """)).isEmpty()
            }

            it("should not report a internal top level property") {
                assertThat(subject.compileAndLint("""
                    internal val foo = 5
                """)).isEmpty()
            }

            it("should not report members and local variables") {
                assertThat(subject.compileAndLint("""
                    internal class A {
                        internal val foo = 5
                        private fun bar() {
                            fun stuff() = Unit
                            val a = 5
                        }
                    }
                """)).isEmpty()
            }
        }
    }
})
