package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

private const val METHODS = "methods"

class ForbiddenMethodCallSpec : Spek({
    setupKotlinEnvironment()

    val env: KotlinCoreEnvironment by memoized()

    describe("ForbiddenMethodCall rule") {

        it("should report kotlin print usages by default") {
            val code = """
            fun main() {
                print("3")
                println("4")
            }
            """
            val findings = ForbiddenMethodCall(TestConfig()).compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(2)
            assertThat(findings).hasSourceLocations(
                SourceLocation(2, 5),
                SourceLocation(3, 5)
            )
        }

        it("should report nothing when methods are blank") {
            val code = """
            import java.lang.System
            fun main() {
                System.out.println("hello")
            }
            """
            val findings =
                ForbiddenMethodCall(TestConfig(mapOf(METHODS to "  "))).compileAndLintWithContext(
                    env,
                    code
                )
            assertThat(findings).isEmpty()
        }

        it("should report nothing when methods do not match") {
            val code = """
            import java.lang.System
            fun main() {
                System.out.println("hello")
            }
            """
            val findings = ForbiddenMethodCall(
                TestConfig(mapOf(METHODS to listOf("java.lang.System.gc")))
            ).compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        it("should report method call when using the fully qualified name") {
            val code = """
            fun main() {
                java.lang.System.out.println("hello")
            }
            """
            val findings = ForbiddenMethodCall(
                TestConfig(mapOf(METHODS to listOf("java.io.PrintStream.println")))
            ).compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasTextLocations(38 to 54)
        }

        it("should report method call when not using the fully qualified name") {
            val code = """
            import java.lang.System.out
            fun main() {
                out.println("hello")
            }
            """
            val findings = ForbiddenMethodCall(
                TestConfig(mapOf(METHODS to listOf("java.io.PrintStream.println")))
            ).compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasTextLocations(49 to 65)
        }

        it("should report multiple different methods") {
            val code = """
            import java.lang.System
            fun main() {
            System.out.println("hello")
                System.gc()
            }
            """
            val findings = ForbiddenMethodCall(
                TestConfig(
                    mapOf(
                        METHODS to listOf(
                            "java.io.PrintStream.println",
                            "java.lang.System.gc"
                        )
                    )
                )
            ).compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(2)
            assertThat(findings).hasTextLocations(48 to 64, 76 to 80)
        }

        it("should report multiple different methods config with sting") {
            val code = """
            import java.lang.System
            fun main() {
            System.out.println("hello")
                System.gc()
            }
            """
            val findings = ForbiddenMethodCall(
                TestConfig(mapOf(METHODS to "java.io.PrintStream.println, java.lang.System.gc"))
            ).compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(2)
            assertThat(findings).hasTextLocations(48 to 64, 76 to 80)
        }

        it("should report equals operator") {
            val code = """
                fun main() {
                    java.math.BigDecimal(5.5) == java.math.BigDecimal(5.5)
                }
            """
            val findings = ForbiddenMethodCall(
                TestConfig(mapOf(METHODS to listOf("java.math.BigDecimal.equals")))
            ).compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        it("should report prefix operator") {
            val code = """
                fun test() {
                    var i = 1
                    ++i
                }
            """
            val findings = ForbiddenMethodCall(
                TestConfig(mapOf(METHODS to listOf("kotlin.Int.inc")))
            ).compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        it("should report postfix operator") {
            val code = """
                fun test() {
                    var i = 1
                    i--
                }
            """
            val findings = ForbiddenMethodCall(
                TestConfig(mapOf(METHODS to listOf("kotlin.Int.dec")))
            ).compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        it("should report both methods when using just method name without full signature") {
            val code = """
                import java.time.Clock
                import java.time.LocalDate
                fun test() {
                    val clock = Clock.systemUTC()
                    val date = LocalDate.now()
                    val date2 = LocalDate.now(clock)
                }
            """
            val findings = ForbiddenMethodCall(
                TestConfig(mapOf(METHODS to listOf("java.time.LocalDate.now")))
            ).compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(2)
        }

        it("should report parameterless method when full signature matches") {
            val code = """
                import java.time.Clock
                import java.time.LocalDate
                fun test() {
                    val clock = Clock.systemUTC()
                    val date = LocalDate.now()
                    val date2 = LocalDate.now(clock)
                }
            """
            val findings = ForbiddenMethodCall(
                TestConfig(mapOf(METHODS to listOf("java.time.LocalDate.now()")))
            ).compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasSourceLocation(5, 26)
        }

        it("should report method with param when full signature matches") {
            val code = """
                import java.time.Clock
                import java.time.LocalDate
                fun test() {
                    val clock = Clock.systemUTC()
                    val date = LocalDate.now()
                    val date2 = LocalDate.now(clock)
                }
            """
            val findings = ForbiddenMethodCall(
                TestConfig(mapOf(METHODS to listOf("java.time.LocalDate.now(java.time.Clock)")))
            ).compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasSourceLocation(6, 27)
        }

        it("should report method with multiple params when full signature matches") {
            val code = """
                import java.time.LocalDate
                fun test() {
                    val date = LocalDate.of(2020, 1, 1)
                }
            """
            val findings = ForbiddenMethodCall(
                TestConfig(mapOf(METHODS to listOf("java.time.LocalDate.of(kotlin.Int, kotlin.Int, kotlin.Int)")))
            ).compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasSourceLocation(3, 26)
        }

        it("should report method with multiple params when full signature matches with additional spacing") {
            val code = """
                import java.time.LocalDate
                fun test() {
                    val date = LocalDate.of(2020, 1, 1)
                }
            """
            val findings = ForbiddenMethodCall(
                TestConfig(mapOf(METHODS to listOf("java.time.LocalDate.of(kotlin.Int,kotlin.Int,kotlin.Int)")))
            ).compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasSourceLocation(3, 26)
        }

        it("should report method with multiple params when method has spaces and commas") {
            val code = """
                package io.gitlab.arturbosch.detekt.rules.style
                
                fun `some, test`() = "String"

                fun test() {
                    val s = `some, test`()
                }
            """
            val findings = ForbiddenMethodCall(
                TestConfig(mapOf(METHODS to listOf("io.gitlab.arturbosch.detekt.rules.style.`some, test`()")))
            ).compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasSourceLocation(6, 13)
        }

        it("should report method with default params") {
            val code = """
                package io.gitlab.arturbosch.detekt.rules.style
                
                fun defaultParamsMethod(s:String, i:Int = 0) = s + i

                fun test() {
                    val s = defaultParamsMethod("test")
                }
            """
            val findings = ForbiddenMethodCall(
                TestConfig(
                    mapOf(
                        METHODS to
                            listOf("io.gitlab.arturbosch.detekt.rules.style.defaultParamsMethod(kotlin.String,kotlin.Int)")
                    )
                )
            ).compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasSourceLocation(6, 13)
        }

        it("should report overriding method calls") {
            val code = """
                package org.example.com
                
                interface I {
                    fun f()
                }
                
                class C : I {
                    override fun f() {}
                }
                
                fun foo(i: I, c: C) {
                    i.f()
                    c.f()
                }
            """
            val findings = ForbiddenMethodCall(
                TestConfig(mapOf(METHODS to listOf("org.example.com.I.f")))
            ).compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(2)
        }

        it("should report functions with lambda params") {
            val code = """
                package org.example

                fun bar(b: (String) -> String) = Unit

                fun foo() {
                    bar { "" }
                }
            """
            val findings = ForbiddenMethodCall(
                TestConfig(mapOf(METHODS to listOf("org.example.bar((kotlin.String) -> kotlin.String)")))
            ).compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        it("should report extension functions") {
            val code = """
                package org.example

                fun String.bar() = Unit

                fun foo() {
                    "".bar()
                }
            """
            val findings = ForbiddenMethodCall(
                TestConfig(mapOf(METHODS to listOf("org.example.bar(kotlin.String)")))
            ).compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        context("work with generics") {
            val code = """
                package org.example

                fun <T, U> bar(a: T, b: U, c: String) = Unit

                fun foo() {
                    bar(1, "", "")
                }
            """

            it("raise the issue") {
                val findings = ForbiddenMethodCall(
                    TestConfig(mapOf(METHODS to listOf("org.example.bar(T, U, kotlin.String)")))
                ).compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }

            it("It doesn't raise any issue because the generics don't match") {
                val findings = ForbiddenMethodCall(
                    TestConfig(mapOf(METHODS to listOf("org.example.bar(U, T, kotlin.String)")))
                ).compileAndLintWithContext(env, code)
                assertThat(findings).isEmpty()
            }
        }

        context("work with generic extensions") {
            val code = """
                package org.example

                fun <R> R.bar(a: String) = Unit

                fun foo() {
                    1.bar("")
                }
            """

            it("raise the issue") {
                val findings = ForbiddenMethodCall(
                    TestConfig(mapOf(METHODS to listOf("org.example.bar(R, kotlin.String)")))
                ).compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }

            it("It doesn't raise any issue because the type doesn't match") {
                val findings = ForbiddenMethodCall(
                    TestConfig(mapOf(METHODS to listOf("org.example.bar(kotlin.Int, kotlin.String)")))
                ).compileAndLintWithContext(env, code)
                assertThat(findings).isEmpty()
            }
        }

        context("Should distinguish between runCatching - #4448") {
            val code = """
                package org.example

                class A {
                    fun foo() {
                        kotlin.runCatching {}
                        runCatching {}
                    }
                }
            """

            it("forbid the one without receiver") {
                val findings = ForbiddenMethodCall(
                    TestConfig(mapOf(METHODS to listOf("kotlin.runCatching(() -> R)")))
                ).compileAndLintWithContext(env, code)
                assertThat(findings)
                    .hasSize(1)
                    .hasSourceLocation(5, 16)
            }

            it("forbid the one with receiver") {
                val findings = ForbiddenMethodCall(
                    TestConfig(mapOf(METHODS to listOf("kotlin.runCatching(T, (T) -> R)")))
                ).compileAndLintWithContext(env, code)
                assertThat(findings)
                    .hasSize(1)
                    .hasSourceLocation(6, 9)
            }
        }
    }
})
