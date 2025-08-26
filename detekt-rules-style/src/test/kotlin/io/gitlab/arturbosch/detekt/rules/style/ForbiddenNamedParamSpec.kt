package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.test.TestConfig
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinCoreEnvironmentTest
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private const val METHODS = "methods"

@KotlinCoreEnvironmentTest
class ForbiddenNamedParamSpec(val env: KotlinEnvironmentContainer) {
    @Test
    fun `should report nothing when methods are blank`() {
        val code = """
            import java.lang.System
            fun main() {
                System.out.println("hello")
                println(message = "")
            }
        """.trimIndent()
        val findings = ForbiddenNamedParam(
            TestConfig(METHODS to listOf("  "))
        ).lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `should report nothing when methods do not match`() {
        val code = """
            fun main() {
                println(message = "hello")
            }
        """.trimIndent()
        val findings = ForbiddenNamedParam(
            TestConfig(METHODS to listOf("kotlin.io.println(kotlin.Double)"))
        ).lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `should report method call when using the fully qualified name`() {
        val code = """
            fun main() {
                println(message = "")
                println(message = 1)
                println(message = 1.0)
            }
        """.trimIndent()
        val findings = ForbiddenNamedParam(
            TestConfig(METHODS to listOf("kotlin.io.println"))
        ).lintWithContext(env, code)
        assertThat(findings).satisfiesExactlyInAnyOrder(
            { assertThat(it).hasTextLocation(17 to 38) },
            { assertThat(it).hasTextLocation(43 to 63) },
            { assertThat(it).hasTextLocation(68 to 90) },
        )
    }

    @Test
    fun `should report method call when using the fully qualified name with custom reason`() {
        val code = """
            fun main() {
                println(message = "")
                println(message = 1)
                println(message = 1.0)
            }
        """.trimIndent()
        val findings = ForbiddenNamedParam(
            TestConfig(
                METHODS to listOf(
                    mapOf(
                        "value" to "kotlin.io.println",
                        "reason" to "As it is self explanatory"
                    )
                )
            )
        ).lintWithContext(env, code)
        assertThat(findings).hasSize(3)
        assertThat(findings).element(0)
            .hasTextLocation(17 to 38)
            .hasMessage(
                "The method `kotlin.io.println` has been forbidden from using named param: " +
                    "As it is self explanatory"
            )
        assertThat(findings).element(1)
            .hasTextLocation(43 to 63)
        assertThat(findings).element(2)
            .hasTextLocation(68 to 90)
    }

    @Test
    fun `should report nothing when not using named param`() {
        val code = """
            fun main() {
                println("")
                println(1)
                println(1.0)
            }
        """.trimIndent()
        val findings = ForbiddenNamedParam(
            TestConfig(METHODS to listOf("kotlin.io.println"))
        ).lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `should report only method using named param when using just method name without full signature`() {
        val code = """
            fun test() {
                kotlin.io.println()
                kotlin.io.println(message = "")
            }
        """.trimIndent()
        val findings = ForbiddenNamedParam(
            TestConfig(METHODS to listOf("kotlin.io.println"))
        ).lintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `should not report method with multiple params with full signature matching with no named param`() {
        val code = """
            import kotlin.math.atan2
            fun test() {
                val tan2 = atan2(1.0, 2.0)
            }
        """.trimIndent()
        val findings = ForbiddenNamedParam(
            TestConfig(METHODS to listOf("kotlin.math.atan2(kotlin.Double, kotlin.Double)"))
        ).lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `should report method with multiple params with full signature matching`() {
        val code = """
            import kotlin.math.atan2
            fun test() {
                val tan2 = atan2(y = 1.0, x = 2.0)
            }
        """.trimIndent()
        val findings = ForbiddenNamedParam(
            TestConfig(METHODS to listOf("kotlin.math.atan2(kotlin.Double, kotlin.Double)"))
        ).lintWithContext(env, code)
        assertThat(findings).singleElement()
            .hasStartSourceLocation(3, 16)
    }

    @Test
    fun `should report method with one named param with full signature matching`() {
        val code = """
            import kotlin.math.atan2
            fun test() {
                val tan21 = atan2(y = 1.0, 2.0)
                val tan22 = atan2(1.0, x = 2.0)
            }
        """.trimIndent()
        val findings = ForbiddenNamedParam(
            TestConfig(METHODS to listOf("kotlin.math.atan2(kotlin.Double, kotlin.Double)"))
        ).lintWithContext(env, code)
        assertThat(findings).hasSize(2)
    }

    @Test
    fun `should report method with array argument`() {
        val code = """
            package com.example

            fun arrayMethod(args: Array<Any>) = args.size

            fun test() {
                val s = arrayMethod(args = arrayOf("test"))
            }
        """.trimIndent()
        val methodName = "com.example.arrayMethod(kotlin.Array)"
        val findings = ForbiddenNamedParam(TestConfig(METHODS to listOf(methodName)))
            .lintWithContext(env, code)
        assertThat(findings).singleElement()
            .hasStartSourceLocation(6, 13)
    }

    @Test
    fun `should report method with vararg argument`() {
        val code = """
            package com.example
            
            fun varargMethod(vararg args: Any) = args.size
            
            fun test() {
                val s = varargMethod(args = arrayOf("test"))
            }
        """.trimIndent()
        val methodName = "com.example.varargMethod(vararg kotlin.Any)"
        val findings = ForbiddenNamedParam(TestConfig(METHODS to listOf(methodName)))
            .lintWithContext(env, code)
        assertThat(findings).singleElement()
            .hasStartSourceLocation(6, 13)
    }

    @Test
    fun `should report @JvmStatic method`() {
        val code = """
            package com.example

            class TestClass {
                companion object {
                    @JvmStatic
                    fun staticMethod(int: Int) {}
                }
            }

            fun test() {
                TestClass.staticMethod(int = 1)
            }
        """.trimIndent()
        val methodName =
            "com.example.TestClass.Companion.staticMethod(kotlin.Int)"
        val findings = ForbiddenNamedParam(TestConfig(METHODS to listOf(methodName)))
            .lintWithContext(env, code)
        assertThat(findings).singleElement()
            .hasStartSourceLocation(11, 15)
    }

    @Test
    fun `should report overriding method calls`() {
        val code = """
            package org.example.com
            
            interface I {
                fun f(value: Int)
            }
            
            class C : I {
                override fun f(value: Int) {}
            }
            
            fun foo(i: I, c: C) {
                i.f(value = 1)
                c.f(value = 1)
            }
        """.trimIndent()
        val findings = ForbiddenNamedParam(
            TestConfig(METHODS to listOf("org.example.com.I.f"))
        ).lintWithContext(env, code)
        assertThat(findings).hasSize(2)
    }

    @Test
    fun `should not report functions with lambda params when using trailing lambda`() {
        val code = """
            package org.example
            
            fun bar(b: (String) -> String) = Unit
            
            fun foo() {
                bar { "" }
            }
        """.trimIndent()
        val findings = ForbiddenNamedParam(
            TestConfig(METHODS to listOf("org.example.bar((kotlin.String) -> kotlin.String)"))
        ).lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `should report functions with lambda params`() {
        val code = """
            package org.example
            
            fun bar(b: (String) -> String) = Unit
            
            fun foo() {
                bar(b = { "" })
            }
        """.trimIndent()
        val findings = ForbiddenNamedParam(
            TestConfig(METHODS to listOf("org.example.bar((kotlin.String) -> kotlin.String)"))
        ).lintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `should report extension functions`() {
        val code = """
            package org.example
            
            fun String.bar(shift: Int) = Unit
            
            fun foo() {
                "".bar(shift = 0)
            }
        """.trimIndent()
        val findings = ForbiddenNamedParam(
            TestConfig(METHODS to listOf("org.example.bar(kotlin.String, kotlin.Int)"))
        ).lintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Nested
    inner class `work with generics` {
        val code = """
            package org.example
            
            fun <T, U> bar(a: T, b: U, c: String) = Unit
            
            fun foo() {
                bar(a = 1, b = "", c = "")
            }
        """.trimIndent()

        @Test
        fun `raise the issue`() {
            val findings = ForbiddenNamedParam(
                TestConfig(METHODS to listOf("org.example.bar(T, U, kotlin.String)"))
            ).lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `doesn't raise any issue because the generics don't match`() {
            val findings = ForbiddenNamedParam(
                TestConfig(METHODS to listOf("org.example.bar(U, T, kotlin.String)"))
            ).lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `work with generic extensions` {
        val code = """
            package org.example
            
            fun <R> R.bar(a: String) = Unit
            
            fun foo() {
                1.bar(a = "")
            }
        """.trimIndent()

        @Test
        fun `raise the issue`() {
            val findings = ForbiddenNamedParam(
                TestConfig(METHODS to listOf("org.example.bar(R, kotlin.String)"))
            ).lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `doesn't raise any issue because the type doesn't match`() {
            val findings = ForbiddenNamedParam(
                TestConfig(METHODS to listOf("org.example.bar(kotlin.Int, kotlin.String)"))
            ).lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `Forbid constructors` {
        val classCode = """
            package org.example
            
            class A() {
                constructor(a: Int) : this()
                constructor(a: Int, b: Int) : this()
            }

        """.trimIndent()

        @Nested
        inner class NameOnly {
            @Test
            fun `empty constructor`() {
                val code = """
                    $classCode
                    val a = A()
                """.trimIndent()
                val findings = ForbiddenNamedParam(
                    TestConfig(METHODS to listOf("org.example.A.<init>"))
                ).lintWithContext(env, code)
                assertThat(findings).isEmpty()
            }

            @Test
            fun `no-empty constructor`() {
                val code = """
                    $classCode
                    val a = A(a = 1)
                    val ab = A(a = 1, b = 1)
                """.trimIndent()
                val findings = ForbiddenNamedParam(
                    TestConfig(METHODS to listOf("org.example.A.<init>"))
                ).lintWithContext(env, code)
                assertThat(findings).hasSize(2)
            }
        }

        @Nested
        inner class WithParameters {
            @Test
            fun `empty constructor`() {
                val code = """
                    $classCode
                    val a = A()
                """.trimIndent()
                val findings = ForbiddenNamedParam(
                    TestConfig(METHODS to listOf("org.example.A.<init>()"))
                ).lintWithContext(env, code)
                assertThat(findings).isEmpty()
            }

            @Test
            fun `no-empty constructor`() {
                val code = """
                    $classCode
                    val a = A(a = 1, b = 1)
                """.trimIndent()
                val findings = ForbiddenNamedParam(
                    TestConfig(METHODS to listOf("org.example.A.<init>(kotlin.Int, kotlin.Int)"))
                ).lintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }

            @Test
            fun `no-empty constructor with only one named param`() {
                val code = """
                    $classCode
                    val a = A(1, b = 1)
                """.trimIndent()
                val findings = ForbiddenNamedParam(
                    TestConfig(METHODS to listOf("org.example.A.<init>(kotlin.Int, kotlin.Int)"))
                ).lintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }

            @Test
            fun `mismatched no-empty constructor`() {
                val code = """
                    $classCode
                    val a = A(a = 1)
                """.trimIndent()
                val findings = ForbiddenNamedParam(
                    TestConfig(METHODS to listOf("org.example.A.<init>(kotlin.Int, kotlin.Int)"))
                ).lintWithContext(env, code)
                assertThat(findings).isEmpty()
            }
        }
    }
}
