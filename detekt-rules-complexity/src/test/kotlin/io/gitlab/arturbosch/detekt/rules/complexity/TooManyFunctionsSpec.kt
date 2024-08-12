package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private const val ALLOWED_FUNCTIONS_PER_FILE = "allowedFunctionsPerFile"
private const val ALLOWED_FUNCTIONS_PER_CLASS = "allowedFunctionsPerClass"
private const val ALLOWED_FUNCTIONS_PER_INTERFACE = "allowedFunctionsPerInterface"
private const val ALLOWED_FUNCTIONS_PER_OBJECT = "allowedFunctionsPerObject"
private const val ALLOWED_FUNCTIONS_PER_ENUM = "allowedFunctionsPerEnum"
private const val IGNORE_DEPRECATED = "ignoreDeprecated"
private const val IGNORE_PRIVATE = "ignorePrivate"
private const val IGNORE_INTERNAL = "ignoreInternal"
private const val IGNORE_OVERRIDDEN = "ignoreOverridden"
private const val IGNORE_ANNOTATED_FUNCTIONS = "ignoreAnnotatedFunctions"

class TooManyFunctionsSpec {
    val rule = TooManyFunctions(
        TestConfig(
            ALLOWED_FUNCTIONS_PER_CLASS to "1",
            ALLOWED_FUNCTIONS_PER_ENUM to "1",
            ALLOWED_FUNCTIONS_PER_FILE to "1",
            ALLOWED_FUNCTIONS_PER_INTERFACE to "1",
            ALLOWED_FUNCTIONS_PER_OBJECT to "1",
        )
    )

    @Test
    fun `finds two functions in class`() {
        val code = """
            class A {
                fun a() = Unit
                fun b() = Unit
            }
        """.trimIndent()

        val findings = rule.compileAndLint(code)
        assertThat(findings).hasSize(1)
        assertThat(findings).hasTextLocations(6 to 7)
    }

    @Test
    fun `finds two functions in object`() {
        val code = """
            object O {
                fun o() = Unit
                fun p() = Unit
            }
        """.trimIndent()

        val findings = rule.compileAndLint(code)
        assertThat(findings).hasSize(1)
        assertThat(findings).hasTextLocations(7 to 8)
    }

    @Test
    fun `finds two functions in interface`() {
        val code = """
            interface I {
                fun i()
                fun j()
            }
        """.trimIndent()

        val findings = rule.compileAndLint(code)
        assertThat(findings).hasSize(1)
        assertThat(findings).hasTextLocations(10 to 11)
    }

    @Test
    fun `finds two functions in enum`() {
        val code = """
            enum class E {
                A;
                fun e() {}
                fun f() {}
            }
        """.trimIndent()

        val findings = rule.compileAndLint(code)
        assertThat(findings).hasSize(1)
        assertThat(findings).hasTextLocations(11 to 12)
    }

    @Test
    fun `finds two functions in file`() {
        val code = """
            fun f() = Unit
            fun g() = Unit
        """.trimIndent()

        assertThat(rule.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `finds one function in file ignoring other declarations`() {
        val code = """
            fun f1() = Unit
            class C
            object O
            fun f2() = Unit
            interface I
            enum class E
            fun f3() = Unit
        """.trimIndent()

        assertThat(rule.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `finds two functions in nested class`() {
        val code = """
            class A {
                class B {
                    fun a() = Unit
                    fun b() = Unit
                }
            }
        """.trimIndent()

        val findings = rule.compileAndLint(code)
        assertThat(findings).hasSize(1)
        assertThat(findings).hasTextLocations(20 to 21)
    }

    @Nested
    inner class `different deprecated functions` {
        val code = """
            @Deprecated("")
            fun f() {
            }
            @Deprecated("")
            fun g() {
            }
            
            class A {
                @Deprecated("")
                fun f() {
                }
                @Deprecated("")
                fun g() {
                }
            }
        """.trimIndent()

        @Test
        fun `finds all deprecated functions per default`() {
            assertThat(rule.compileAndLint(code)).hasSize(2)
        }

        @Test
        fun `finds no deprecated functions`() {
            val configuredRule = TooManyFunctions(
                TestConfig(
                    ALLOWED_FUNCTIONS_PER_CLASS to "1",
                    ALLOWED_FUNCTIONS_PER_FILE to "1",
                    IGNORE_DEPRECATED to "true",
                )
            )
            assertThat(configuredRule.compileAndLint(code)).isEmpty()
        }
    }

    @Nested
    inner class `different private functions` {

        val code = """
            class A {
                private fun f() {}
                private fun g() {}
            }
        """.trimIndent()

        @Test
        fun `finds the private function per default`() {
            assertThat(rule.compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `finds no private functions`() {
            val configuredRule = TooManyFunctions(
                TestConfig(
                    ALLOWED_FUNCTIONS_PER_CLASS to "1",
                    ALLOWED_FUNCTIONS_PER_FILE to "1",
                    IGNORE_PRIVATE to "true",
                )
            )
            assertThat(configuredRule.compileAndLint(code)).isEmpty()
        }
    }

    @Nested
    inner class `internal functions` {

        val code = """
            class A {
                internal fun f() {}
                internal fun g() {}
            }
        """.trimIndent()

        @Test
        fun `finds the internal function per default`() {
            assertThat(rule.compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `finds no internal functions`() {
            val configuredRule = TooManyFunctions(
                TestConfig(
                    ALLOWED_FUNCTIONS_PER_CLASS to "1",
                    IGNORE_INTERNAL to "true",
                )
            )
            assertThat(configuredRule.compileAndLint(code)).isEmpty()
        }

        @Test
        fun `finds private functions`() {
            val configuredRule = TooManyFunctions(
                TestConfig(
                    ALLOWED_FUNCTIONS_PER_CLASS to "1",
                    IGNORE_INTERNAL to "true",
                )
            )
            val code = """
                class A {
                    private fun f() {}
                    private fun g() {}
                }
            """.trimIndent()
            assertThat(configuredRule.compileAndLint(code)).hasSize(1)
        }
    }

    @Nested
    inner class `false negative when private and deprecated functions are ignored - #1439` {

        @Test
        fun `should not report when file has no public functions`() {
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
            val configuredRule = TooManyFunctions(
                TestConfig(
                    ALLOWED_FUNCTIONS_PER_CLASS to "1",
                    ALLOWED_FUNCTIONS_PER_FILE to "1",
                    IGNORE_PRIVATE to "true",
                    IGNORE_DEPRECATED to "true",
                    IGNORE_OVERRIDDEN to "true",
                )
            )
            assertThat(configuredRule.compileAndLint(code)).isEmpty()
        }
    }

    @Nested
    inner class `overridden functions` {

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

        @Test
        fun `should not report class with overridden functions, if ignoreOverridden is enabled`() {
            val configuredRule = TooManyFunctions(
                TestConfig(
                    ALLOWED_FUNCTIONS_PER_CLASS to "1",
                    ALLOWED_FUNCTIONS_PER_FILE to "1",
                    IGNORE_OVERRIDDEN to "true",
                )
            )
            assertThat(configuredRule.compileAndLint(code)).isEmpty()
        }

        @Test
        fun `should count overridden functions, if ignoreOverridden is disabled`() {
            val configuredRule = TooManyFunctions(
                TestConfig(
                    ALLOWED_FUNCTIONS_PER_CLASS to "1",
                    ALLOWED_FUNCTIONS_PER_FILE to "1",
                    IGNORE_OVERRIDDEN to "false",
                )
            )
            assertThat(configuredRule.compileAndLint(code)).hasSize(1)
        }
    }

    @Test
    fun `should not count functions included in ignoreAnnotatedFunctions`() {
        val rule = TooManyFunctions(
            TestConfig(
                ALLOWED_FUNCTIONS_PER_CLASS to "1",
                ALLOWED_FUNCTIONS_PER_ENUM to "1",
                ALLOWED_FUNCTIONS_PER_FILE to "1",
                ALLOWED_FUNCTIONS_PER_INTERFACE to "1",
                ALLOWED_FUNCTIONS_PER_OBJECT to "1",
                IGNORE_ANNOTATED_FUNCTIONS to listOf("Preview"),
            )
        )

        val code = """
                annotation class Preview            

                class A {
                    fun a() = Unit
                    @Preview
                    fun b() = Unit
                    @Preview
                    fun c() = Unit
                }
        """.trimIndent()

        val findings = rule.compileAndLint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `should trigger when there are too many non-annotated functions`() {
        val rule = TooManyFunctions(
            TestConfig(
                ALLOWED_FUNCTIONS_PER_CLASS to "1",
                ALLOWED_FUNCTIONS_PER_ENUM to "1",
                ALLOWED_FUNCTIONS_PER_FILE to "1",
                ALLOWED_FUNCTIONS_PER_INTERFACE to "1",
                ALLOWED_FUNCTIONS_PER_OBJECT to "1",
                IGNORE_ANNOTATED_FUNCTIONS to listOf("Preview"),
            )
        )

        val code = """
                annotation class Preview            

                class A {
                    fun a() = Unit
                    fun b() = Unit
                    @Preview
                    fun c() = Unit
                    @Preview
                    fun d() = Unit
                }
        """.trimIndent()

        val findings = rule.compileAndLint(code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `should suppress TooManyFunctionsRule on class level`() {
        val rule = TooManyFunctions(TestConfig("thresholdInClasses" to "0"))
        val code = """
            @Suppress("TooManyFunctions")
            class OneIsTooMany {
                fun f() {}
            }
        """.trimIndent()

        val findings = rule.compileAndLint(code)

        assertThat(findings).isEmpty()
    }
}
