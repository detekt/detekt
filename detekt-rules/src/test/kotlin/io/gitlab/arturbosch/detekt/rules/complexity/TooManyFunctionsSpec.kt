package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class TooManyFunctionsSpec : Spek({

    describe("a simple test") {

        val rule = TooManyFunctions()

        it("should find one file with too many functions") {
            assertThat(rule.lint(Case.TooManyFunctions.path())).hasSize(1)
        }

        it("should find one file with too many top level functions") {
            assertThat(rule.lint(Case.TooManyFunctionsTopLevel.path())).hasSize(1)
        }
    }

    describe("different declarations with one function as threshold") {

        val rule = TooManyFunctions(TestConfig(mapOf(
                TooManyFunctions.THRESHOLD_IN_CLASSES to "1",
                TooManyFunctions.THRESHOLD_IN_ENUMS to "1",
                TooManyFunctions.THRESHOLD_IN_FILES to "1",
                TooManyFunctions.THRESHOLD_IN_INTERFACES to "1",
                TooManyFunctions.THRESHOLD_IN_OBJECTS to "1"
        )))

        it("finds one function in class") {
            val code = """
				class A {
					fun a() = Unit
				}
			"""

            assertThat(rule.compileAndLint(code)).hasSize(1)
        }

        it("finds one function in object") {
            val code = """
				object O {
					fun o() = Unit
				}
			"""

            assertThat(rule.compileAndLint(code)).hasSize(1)
        }

        it("finds one function in interface") {
            val code = """
				interface I {
					fun i()
				}
			"""

            assertThat(rule.compileAndLint(code)).hasSize(1)
        }

        it("finds one function in enum") {
            val code = """
				enum class E {
                    A;
					fun e() {}
				}
			"""

            assertThat(rule.compileAndLint(code)).hasSize(1)
        }

        it("finds one function in file") {
            val code = "fun f() = Unit"

            assertThat(rule.compileAndLint(code)).hasSize(1)
        }

        it("finds one function in file ignoring other declarations") {
            val code = """
				fun f1() = Unit
				class C
				object O
				fun f2() = Unit
				interface I
				enum class E
				fun f3() = Unit
			"""

            assertThat(rule.compileAndLint(code)).hasSize(1)
        }

        it("finds one function in nested class") {
            val code = """
				class A {
					class B {
						fun a() = Unit
					}
				}
			"""

            assertThat(rule.compileAndLint(code)).hasSize(1)
        }

        describe("different deprecated functions") {
            val code = """
				@Deprecated("")
				fun f() {
				}

				class A {
					@Deprecated("")
					fun f() {
					}
				}
				"""
            it("finds all deprecated functions per default") {

                assertThat(rule.compileAndLint(code)).hasSize(2)
            }

            it("finds no deprecated functions") {
                val configuredRule = TooManyFunctions(TestConfig(mapOf(
                        TooManyFunctions.THRESHOLD_IN_CLASSES to "1",
                        TooManyFunctions.THRESHOLD_IN_FILES to "1",
                        TooManyFunctions.IGNORE_DEPRECATED to "true"
                )))
                assertThat(configuredRule.compileAndLint(code)).isEmpty()
            }
        }

        describe("different private functions") {

            val code = """
				class A {
					private fun f() {}
				}
				"""

            it("finds the private function per default") {
                assertThat(rule.compileAndLint(code)).hasSize(1)
            }

            it("finds no private functions") {
                val configuredRule = TooManyFunctions(TestConfig(mapOf(
                        TooManyFunctions.THRESHOLD_IN_CLASSES to "1",
                        TooManyFunctions.THRESHOLD_IN_FILES to "1",
                        TooManyFunctions.IGNORE_PRIVATE to "true"
                )))
                assertThat(configuredRule.compileAndLint(code)).isEmpty()
            }
        }

        describe("false negative when private and deprecated functions are ignored - #1439") {

            it("should not report when file has no public functions") {
                val code = """
                    class A {
                        private fun a() = Unit
                        private fun b() = Unit
                        @Deprecated("")
                        private fun c() = Unit
                    }

                    interface I {
                        fun a() = Unit
                        fun b() = Unit
                    }

                    class B : I {
                        override fun a() = Unit
                        override fun b() = Unit
                    }
				""".trimIndent()
                val configuredRule = TooManyFunctions(TestConfig(mapOf(
                        TooManyFunctions.THRESHOLD_IN_CLASSES to "1",
                        TooManyFunctions.THRESHOLD_IN_FILES to "1",
                        TooManyFunctions.IGNORE_PRIVATE to "true",
                        TooManyFunctions.IGNORE_DEPRECATED to "true",
                        TooManyFunctions.IGNORE_OVERRIDDEN to "true"
                )))
                assertThat(configuredRule.compileAndLint(code)).isEmpty()
            }
        }

        describe("overridden functions") {

            val code = """
                    interface I1 {
                        fun func1()
                        fun func2()
                    }

                    class Foo : I1 {
                        override fun func1() = Unit
                        override fun func2() = Unit
                    }
                """.trimIndent()

            it("should not report class with overridden functions, if ignoreOverridden is enabled") {
                val configuredRule = TooManyFunctions(TestConfig(mapOf(
                        TooManyFunctions.THRESHOLD_IN_CLASSES to "1",
                        TooManyFunctions.THRESHOLD_IN_FILES to "1",
                        TooManyFunctions.IGNORE_OVERRIDDEN to "true"
                )))
                assertThat(configuredRule.compileAndLint(code)).isEmpty()
            }

            it("should count overridden functions, if ignoreOverridden is disabled") {
                val configuredRule = TooManyFunctions(TestConfig(mapOf(
                        TooManyFunctions.THRESHOLD_IN_CLASSES to "1",
                        TooManyFunctions.THRESHOLD_IN_FILES to "1",
                        TooManyFunctions.IGNORE_OVERRIDDEN to "false"
                )))
                assertThat(configuredRule.compileAndLint(code)).hasSize(1)
            }
        }
    }
})
