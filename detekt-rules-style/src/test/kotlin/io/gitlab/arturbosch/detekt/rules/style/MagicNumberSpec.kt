package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

private const val IGNORE_NUMBERS = "ignoreNumbers"
private const val IGNORE_HASH_CODE = "ignoreHashCodeFunction"
private const val IGNORE_PROPERTY_DECLARATION = "ignorePropertyDeclaration"
private const val IGNORE_LOCAL_VARIABLES = "ignoreLocalVariableDeclaration"
private const val IGNORE_CONSTANT_DECLARATION = "ignoreConstantDeclaration"
private const val IGNORE_COMPANION_OBJECT_PROPERTY_DECLARATION = "ignoreCompanionObjectPropertyDeclaration"
private const val IGNORE_ANNOTATION = "ignoreAnnotation"
private const val IGNORE_NAMED_ARGUMENT = "ignoreNamedArgument"
private const val IGNORE_ENUMS = "ignoreEnums"
private const val IGNORE_RANGES = "ignoreRanges"
private const val IGNORE_EXTENSION_FUNCTIONS = "ignoreExtensionFunctions"

class MagicNumberSpec {

    @Nested
    inner class `a float of 1` {
        val code = "val myFloat = 1.0f"

        @Test
        fun `should not be reported by default`() {
            val findings = MagicNumber(Config.empty).lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should be reported when ignoredNumbers is empty`() {
            val findings = MagicNumber(TestConfig(IGNORE_NUMBERS to emptyList<String>())).lint(code)
            assertThat(findings).hasStartSourceLocation(1, 15)
        }
    }

    @Nested
    inner class `a const float of 1` {
        val code = "const val MY_FLOAT = 1.0f"

        @Test
        fun `should not be reported by default`() {
            val findings = MagicNumber(Config.empty).lint(code, compile = false)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not be reported when ignoredNumbers is empty`() {
            val findings = MagicNumber(TestConfig(IGNORE_NUMBERS to emptyList<String>())).lint(code, compile = false)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `an integer of 1` {
        val code = "val myInt = 1"

        @Test
        fun `should not be reported by default`() {
            val findings = MagicNumber(Config.empty).lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should be reported when ignoredNumbers is empty`() {
            val findings = MagicNumber(TestConfig(IGNORE_NUMBERS to emptyList<String>())).lint(code)
            assertThat(findings).hasStartSourceLocation(1, 13)
        }
    }

    @Nested
    inner class `a const integer of 1` {
        val code = "const val MY_INT = 1"

        @Test
        fun `should not be reported by default`() {
            val findings = MagicNumber(Config.empty).lint(code, compile = false)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not be reported when ignoredNumbers is empty`() {
            val findings = MagicNumber(TestConfig(IGNORE_NUMBERS to emptyList<String>())).lint(code, compile = false)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `a long of 1` {
        val code = "val myLong = 1L"

        @Test
        fun `should not be reported by default`() {
            val findings = MagicNumber(Config.empty).lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should be reported when ignoredNumbers is empty`() {
            val findings = MagicNumber(TestConfig(IGNORE_NUMBERS to emptyList<String>())).lint(code)
            assertThat(findings).hasStartSourceLocation(1, 14)
        }
    }

    @Nested
    inner class `a long of -1` {
        val code = "val myLong = -1L"

        @Test
        fun `should not be reported by default`() {
            val findings = MagicNumber(Config.empty).lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should be reported when ignoredNumbers is empty`() {
            val findings = MagicNumber(TestConfig(IGNORE_NUMBERS to emptyList<String>())).lint(code)
            assertThat(findings).hasStartSourceLocation(1, 15)
        }
    }

    @Nested
    inner class `a long of -2` {
        val code = "val myLong = -2L"

        @Test
        fun `should be reported by default`() {
            val findings = MagicNumber(Config.empty).lint(code)
            assertThat(findings).hasStartSourceLocation(1, 15)
        }

        @Test
        fun `should be ignored when ignoredNumbers contains it verbatim`() {
            val findings = MagicNumber(TestConfig(IGNORE_NUMBERS to listOf("-2L"))).lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should be ignored when ignoredNumbers contains it as floating point`() {
            val findings = MagicNumber(TestConfig(IGNORE_NUMBERS to listOf("-2f"))).lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not be ignored when ignoredNumbers contains 2 but not -2`() {
            val findings = MagicNumber(TestConfig(IGNORE_NUMBERS to listOf("1", "2", "3", "-1", "0")))
                .lint(code)
            assertThat(findings).hasStartSourceLocation(1, 15)
        }
    }

    @Nested
    inner class `a const long of 1` {
        val code = "const val MY_LONG = 1L"

        @Test
        fun `should not be reported by default`() {
            val findings = MagicNumber(Config.empty).lint(code, compile = false)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not be reported when ignoredNumbers is empty`() {
            val findings = MagicNumber(TestConfig(IGNORE_NUMBERS to emptyList<String>())).lint(code, compile = false)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `a double of 1` {
        val code = "val myDouble = 1.0"

        @Test
        fun `should not be reported by default`() {
            val findings = MagicNumber(Config.empty).lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should be reported when ignoredNumbers is empty`() {
            val findings = MagicNumber(TestConfig(IGNORE_NUMBERS to emptyList<String>())).lint(code)
            assertThat(findings).hasStartSourceLocation(1, 16)
        }
    }

    @Nested
    inner class `a const double of 1` {
        val code = "const val MY_DOUBLE = 1.0"

        @Test
        fun `should not be reported by default`() {
            val findings = MagicNumber(Config.empty).lint(code, compile = false)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not be reported when ignoredNumbers is empty`() {
            val findings = MagicNumber(TestConfig(IGNORE_NUMBERS to emptyList<String>())).lint(code, compile = false)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `a hex of 1` {
        val code = "val myHex = 0x1"

        @Test
        fun `should not be reported by default`() {
            val findings = MagicNumber(Config.empty).lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should be reported when ignoredNumbers is empty`() {
            val findings = MagicNumber(TestConfig(IGNORE_NUMBERS to emptyList<String>())).lint(code)
            assertThat(findings).hasStartSourceLocation(1, 13)
        }
    }

    @Nested
    inner class `a const hex of 1` {
        val code = "const val MY_HEX = 0x1"

        @Test
        fun `should not be reported by default`() {
            val findings = MagicNumber(Config.empty).lint(code, compile = false)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not be reported when ignoredNumbers is empty`() {
            val findings = MagicNumber(TestConfig(IGNORE_NUMBERS to emptyList<String>())).lint(code, compile = false)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `an integer of 300` {
        val code = "val myInt = 300"

        @Test
        fun `should not be reported when ignoredNumbers contains 300`() {
            val findings = MagicNumber(TestConfig(IGNORE_NUMBERS to listOf("300"))).lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not be reported when ignoredNumbers contains a floating point 300`() {
            val findings = MagicNumber(TestConfig(IGNORE_NUMBERS to listOf("300.0"))).lint(code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `a binary literal` {
        val code = "val myBinary = 0b01001"

        @Test
        fun `should not be reported`() {
            val findings = MagicNumber(Config.empty).lint(code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should not be reported when ignoredNumbers contains a binary literal 0b01001`() {
            val findings = MagicNumber(TestConfig(IGNORE_NUMBERS to listOf("0b01001"))).lint(code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `an integer literal with underscores` {
        val code = "val myInt = 100_000"

        @Test
        fun `should be reported by default`() {
            val findings = MagicNumber(Config.empty).lint(code)
            assertThat(findings).hasStartSourceLocation(1, 13)
        }

        @Test
        fun `should not be reported when ignored verbatim`() {
            val findings = MagicNumber(TestConfig(IGNORE_NUMBERS to listOf("100_000"))).lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not be reported when ignored with different underscores`() {
            val findings = MagicNumber(TestConfig(IGNORE_NUMBERS to listOf("10_00_00"))).lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not be reported when ignored without underscores`() {
            val findings = MagicNumber(TestConfig(IGNORE_NUMBERS to listOf("100000"))).lint(code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `an if statement with magic numbers` {
        val code = "val myInt = if (5 < 6) 7 else 8"

        @Test
        fun `should be reported`() {
            val findings = MagicNumber(Config.empty).lint(code)
            assertThat(findings)
                .hasStartSourceLocations(
                    SourceLocation(1, 17),
                    SourceLocation(1, 21),
                    SourceLocation(1, 24),
                    SourceLocation(1, 31)
                )
        }
    }

    @Nested
    inner class `a when statement with magic numbers` {
        val code = """
            fun test(x: Int): Int {
                when (x) {
                    5 -> return 5
                    4 -> return 4
                    3 -> return 3
                }
            }
        """.trimIndent()

        @Test
        fun `should be reported`() {
            val findings = MagicNumber(Config.empty).lint(code, compile = false)
            assertThat(findings).hasStartSourceLocations(
                SourceLocation(3, 9),
                SourceLocation(3, 21),
                SourceLocation(4, 9),
                SourceLocation(4, 21),
                SourceLocation(5, 9),
                SourceLocation(5, 21)
            )
        }
    }

    @Nested
    inner class `a method containing variables with magic numbers` {
        val code = """
            fun test(x: Int) {
                val i = 5
            }
        """.trimIndent()

        @Test
        fun `should be reported`() {
            val findings = MagicNumber(Config.empty).lint(code)
            assertThat(findings).hasSize(1)
        }
    }

    @Nested
    inner class `a boolean value` {
        val code = """
            fun test() : Boolean {
                return true;
            }
        """.trimIndent()

        @Test
        fun `should not be reported`() {
            val findings = MagicNumber(Config.empty).lint(code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `a non-numeric constant expression` {
        val code = "val surprise = true"

        @Test
        fun `should not be reported`() {
            val findings = MagicNumber(Config.empty).lint(code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    @DisplayName("a float of 0.5")
    inner class Float {
        val code = "val test = 0.5f"

        @Test
        fun `should be reported by default`() {
            val findings = MagicNumber(Config.empty).lint(code)
            assertThat(findings).hasStartSourceLocation(1, 12)
        }

        @Test
        fun `should not be reported when ignoredNumbers contains it`() {
            val findings = MagicNumber(TestConfig(IGNORE_NUMBERS to listOf(".5"))).lint(code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `a magic number number in a constructor call` {

        @Test
        fun `should report`() {
            val code = "val file = Array<String?>(42) { null }"
            val findings = MagicNumber(Config.empty).lint(code)
            assertThat(findings).hasSize(1)
        }
    }

    @Nested
    inner class `an invalid ignoredNumber` {

        @Test
        fun `throws a NumberFormatException`() {
            assertThatExceptionOfType(NumberFormatException::class.java).isThrownBy {
                MagicNumber(TestConfig(IGNORE_NUMBERS to listOf("banana"))).lint("val i = 0")
            }
        }
    }

    @Nested
    inner class `an empty ignoredNumber` {

        @Test
        fun `doesn't throw an exception`() {
            MagicNumber(TestConfig(IGNORE_NUMBERS to emptyList<String>()))
        }
    }

    @Nested
    inner class `ignoring properties` {
        val code = """
            @Magic(number = 69)
            class A {
                val boringNumber = 42
                const val BORING_CONSTANT = 93871
                val duration = Duration.seconds(10)
                val durationWithStdlibFunction = 10.toDuration(DurationUnit.MILLISECONDS)
            
                override fun hashCode(): Int {
                    val iAmSoMagic = 7328672
                }
            
                companion object {
                    val anotherBoringNumber = 43
                    const val anotherBoringConstant = 93872
                    val color = Color(0x66000000)
                    val colorWithExplicitParameter = Color(color = 0x66000000)
                }
            }
            
            data class Color(val color: Int)
        """.trimIndent()

        @Test
        fun `should report all without ignore flags`() {
            val config = TestConfig(
                IGNORE_PROPERTY_DECLARATION to "false",
                IGNORE_ANNOTATION to "false",
                IGNORE_NAMED_ARGUMENT to "false",
                IGNORE_HASH_CODE to "false",
                IGNORE_CONSTANT_DECLARATION to "false",
                IGNORE_COMPANION_OBJECT_PROPERTY_DECLARATION to "false",
            )

            val findings = MagicNumber(config).lint(code, compile = false)
            assertThat(findings)
                .hasStartSourceLocations(
                    SourceLocation(1, 17),
                    SourceLocation(3, 24),
                    SourceLocation(4, 33),
                    SourceLocation(5, 37),
                    SourceLocation(9, 26),
                    SourceLocation(13, 35),
                    SourceLocation(14, 43),
                    SourceLocation(15, 27),
                    SourceLocation(16, 56)
                )
        }

        @Test
        fun `should not report any issues with all ignore flags`() {
            val config = TestConfig(
                IGNORE_PROPERTY_DECLARATION to "true",
                IGNORE_ANNOTATION to "true",
                IGNORE_HASH_CODE to "true",
                IGNORE_CONSTANT_DECLARATION to "true",
                IGNORE_COMPANION_OBJECT_PROPERTY_DECLARATION to "true",
            )

            val findings = MagicNumber(config).lint(code, compile = false)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `magic numbers in companion object property assignments` {
        val code = """
            class A {
            
                companion object {
                    val anotherBoringNumber = 43
                    const val anotherBoringConstant = 93872
                }
            }
        """.trimIndent()

        @Test
        fun `should not report any issues by default`() {
            val findings = MagicNumber(Config.empty).lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not report any issues when ignoring properties but not constants nor companion objects`() {
            val config = TestConfig(
                IGNORE_PROPERTY_DECLARATION to "true",
                IGNORE_CONSTANT_DECLARATION to "false",
                IGNORE_COMPANION_OBJECT_PROPERTY_DECLARATION to "false",
            )

            val findings = MagicNumber(config).lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not report any issues when ignoring properties and constants but not companion objects`() {
            val config = TestConfig(
                IGNORE_PROPERTY_DECLARATION to "true",
                IGNORE_CONSTANT_DECLARATION to "true",
                IGNORE_COMPANION_OBJECT_PROPERTY_DECLARATION to "false",
            )

            val findings = MagicNumber(config).lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not report any issues when ignoring properties, constants and companion objects`() {
            val config = TestConfig(
                IGNORE_PROPERTY_DECLARATION to "true",
                IGNORE_CONSTANT_DECLARATION to "true",
                IGNORE_COMPANION_OBJECT_PROPERTY_DECLARATION to "true"
            )

            val findings = MagicNumber(config).lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not report any issues when ignoring companion objects but not properties and constants`() {
            val config = TestConfig(
                IGNORE_PROPERTY_DECLARATION to "false",
                IGNORE_CONSTANT_DECLARATION to "false",
                IGNORE_COMPANION_OBJECT_PROPERTY_DECLARATION to "true",
            )

            val findings = MagicNumber(config).lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should report property when ignoring constants but not properties and companion objects`() {
            val config = TestConfig(
                IGNORE_PROPERTY_DECLARATION to "false",
                IGNORE_CONSTANT_DECLARATION to "true",
                IGNORE_COMPANION_OBJECT_PROPERTY_DECLARATION to "false",
            )

            val findings = MagicNumber(config).lint(code)
            assertThat(findings).hasStartSourceLocation(4, 35)
        }

        @Test
        fun `should report property and constant when not ignoring properties, constants nor companion objects`() {
            val config = TestConfig(
                IGNORE_PROPERTY_DECLARATION to "false",
                IGNORE_CONSTANT_DECLARATION to "false",
                IGNORE_COMPANION_OBJECT_PROPERTY_DECLARATION to "false",
            )

            val findings = MagicNumber(config).lint(code)
            assertThat(findings)
                .hasStartSourceLocations(
                    SourceLocation(4, 35),
                    SourceLocation(5, 43)
                )
        }
    }

    @Nested
    inner class `a property without number` {
        val code = "private var pair: Pair<String, Int>? = null"

        @Test
        fun `should not lead to a crash #276`() {
            val findings = MagicNumber(Config.empty).lint(code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `ignoring named arguments` {
        @Nested
        inner class `in constructor invocation` {
            private fun code(numberString: String) = """
                data class Model(
                        val someVal: Int,
                        val other: String = "default"
                )
                
                var model = Model(someVal = $numberString)
            """.trimIndent()

            @Test
            fun `should not ignore int`() {
                val rule = MagicNumber(TestConfig(IGNORE_NAMED_ARGUMENT to "false"))
                assertThat(rule.lint(code("53"))).hasSize(1)
            }

            @Test
            fun `should not ignore float`() {
                val rule = MagicNumber(TestConfig(IGNORE_NAMED_ARGUMENT to "false"))
                assertThat(rule.lint(code("53f"), compile = false)).hasSize(1)
            }

            @Test
            fun `should not ignore binary`() {
                val rule = MagicNumber(TestConfig(IGNORE_NAMED_ARGUMENT to "false"))
                assertThat(rule.lint(code("0b01001"))).hasSize(1)
            }

            @Test
            fun `should ignore integer with underscores`() {
                val rule = MagicNumber(TestConfig(IGNORE_NAMED_ARGUMENT to "false"))
                assertThat(rule.lint(code("101_000"))).hasSize(1)
            }

            @Test
            fun `should ignore numbers by default`() {
                assertThat(MagicNumber(Config.empty).lint(code("53"))).isEmpty()
            }

            @Test
            fun `should ignore negative numbers by default`() {
                assertThat(MagicNumber(Config.empty).lint(code("-53"))).isEmpty()
            }

            @Test
            fun `should ignore named arguments in inheritance - #992`() {
                val code = """
                    abstract class A(n: Int)
                    
                    object B : A(n = 5)
                """.trimIndent()
                assertThat(MagicNumber(Config.empty).lint(code)).isEmpty()
            }

            @Test
            fun `should ignore named arguments in parameter annotations - #1115`() {
                val code =
                    "@JvmStatic fun setCustomDimension(@IntRange(from = 0, to = 19) index: Int, value: String?) {}"
                assertThat(MagicNumber(Config.empty).lint(code, compile = false)).isEmpty()
            }
        }

        @Nested
        inner class `Issue#659 - false-negative reporting on unnamed argument when ignore is true` {

            private fun code(numberString: String) = """
                data class Model(
                        val someVal: Int,
                        val other: String = "default"
                )
                
                var model = Model($numberString)
            """.trimIndent()

            @Test
            fun `should detect the argument by default`() {
                assertThat(MagicNumber(Config.empty).lint(code("53"))).hasSize(1)
            }
        }

        @Nested
        inner class `in function invocation` {
            private fun code(number: Number) = """
                fun tested(someVal: Int, other: String = "default") {}
                
                val t = tested(someVal = $number)
            """.trimIndent()

            @Test
            fun `should ignore int by default`() {
                assertThat(MagicNumber(Config.empty).lint(code(53))).isEmpty()
            }

            @Test
            fun `should ignore float by default`() {
                assertThat(MagicNumber(Config.empty).lint(code(53f), compile = false)).isEmpty()
            }

            @Test
            fun `should ignore binary by default`() {
                assertThat(MagicNumber(Config.empty).lint(code(0b01001))).isEmpty()
            }

            @Test
            fun `should ignore integer with underscores`() {
                assertThat(MagicNumber(Config.empty).lint(code(101_000))).isEmpty()
            }
        }

        @Nested
        inner class `in enum constructor argument` {
            val code = """
                enum class Bag(id: Int) {
                    SMALL(1),
                    EXTRA_LARGE(5)
                }
            """.trimIndent()

            @Test
            fun `should be reported by default`() {
                assertThat(MagicNumber(Config.empty).lint(code)).hasSize(1)
            }

            @Test
            fun `numbers when 'ignoreEnums' is set to true`() {
                val rule = MagicNumber(TestConfig(IGNORE_ENUMS to "true"))
                assertThat(rule.lint(code)).isEmpty()
            }
        }

        @Nested
        inner class `in enum constructor as named argument` {
            val code = """
                enum class Bag(id: Int) {
                    SMALL(id = 1),
                    EXTRA_LARGE(id = 5)
                }
            """.trimIndent()

            @Test
            fun `should be reported`() {
                val rule = MagicNumber(TestConfig(IGNORE_NAMED_ARGUMENT to "false"))
                assertThat(rule.lint(code)).hasSize(1)
            }

            @Test
            fun `numbers when 'ignoreEnums' is set to true`() {
                val rule = MagicNumber(
                    TestConfig(
                        IGNORE_NAMED_ARGUMENT to "false",
                        IGNORE_ENUMS to "true",
                    )
                )
                assertThat(rule.lint(code)).isEmpty()
            }
        }

        @Test
        fun `in constructor invocation with complex expression`() {
            val rule = MagicNumber(TestConfig(IGNORE_NAMED_ARGUMENT to "true"))
            val code = """
                data class Image(val size: Float)
                val a = Image(
                  size = if (true) 1f else 0.6f
                )
            """.trimIndent()

            assertThat(rule.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `functions with and without braces which return values` {

        @Test
        fun `does not report functions that always returns a constant value`() {
            val code = """
                fun x() = 9
                fun y(): Int { return 9 }
            """.trimIndent()
            assertThat(MagicNumber(Config.empty).lint(code)).isEmpty()
        }

        @Test
        fun `reports functions that does not return a constant value`() {
            val code = """
                fun x() = 9 + 1
                fun y(): Int { return 9 + 1 }
            """.trimIndent()
            assertThat(MagicNumber(Config.empty).lint(code)).hasSize(2)
        }
    }

    @Nested
    inner class `in-class declaration with default parameters` {

        @Test
        fun `reports no finding`() {
            val code = "class SomeClassWithDefault(val defaultValue: Int = 10)"
            assertThat(MagicNumber(Config.empty).lint(code)).isEmpty()
        }

        @Test
        fun `reports no finding for an explicit declaration`() {
            val code = "class SomeClassWithDefault constructor(val defaultValue: Int = 10)"
            assertThat(MagicNumber(Config.empty).lint(code)).isEmpty()
        }

        @Test
        fun `reports no finding for a function expression`() {
            val code = """
                import java.time.Duration

                class SomeClassWithDefault constructor(val defaultValue: Duration = 10.toDuration(DurationUnit.MILLISECONDS))
            """.trimIndent()
            assertThat(MagicNumber(Config.empty).lint(code, compile = false)).isEmpty()
        }
    }

    @Nested
    inner class `default parameters in secondary constructor` {

        @Test
        fun `reports no finding`() {
            val code = """
                class SomeClassWithDefault {
                    constructor(defaultValue: Int = 10) { }
                }
            """.trimIndent()
            assertThat(MagicNumber(Config.empty).lint(code)).isEmpty()
        }

        @Test
        fun `reports no finding for a function expression`() {
            val code = """
                class SomeClassWithDefault {
                    constructor(defaultValue: Duration = 10.toDuration(DurationUnit.MILLISECONDS)) { }
                }
            """.trimIndent()
            assertThat(MagicNumber(Config.empty).lint(code, compile = false)).isEmpty()
        }
    }

    @Nested
    inner class `default parameters in function` {

        @Test
        fun `reports no finding`() {
            val code = "fun f(p: Int = 100) {}"
            assertThat(MagicNumber(Config.empty).lint(code)).isEmpty()
        }

        @Test
        fun `reports no finding for a function expression`() {
            val code = "fun f(p: Duration = 10.toDuration(DurationUnit.MILLISECONDS)) {}"
            assertThat(MagicNumber(Config.empty).lint(code, compile = false)).isEmpty()
        }
    }

    @Nested
    inner class `a number as part of a range` {

        @Suppress("UnusedPrivateFunction")
        private fun cases() = listOf(
            "val range = 1..27",
            "val range = (1..27)",
            "val range = 27 downTo 1",
            "val range = 1 until 27 step 1",
            "val inRange = 1 in 1..27",
            "val inRange = (1 in 27 downTo 0 step 1)",
            "val inRange = (1..27 step 1).last"
        )

        @ParameterizedTest
        @MethodSource("cases")
        fun `reports a finding by default`(code: String) {
            assertThat(MagicNumber(Config.empty).lint(code)).hasSize(1)
        }

        @ParameterizedTest
        @MethodSource("cases")
        fun `reports a finding if ranges are not ignored`(code: String) {
            assertThat(MagicNumber(TestConfig(IGNORE_RANGES to "false")).lint(code))
                .hasSize(1)
        }

        @ParameterizedTest
        @MethodSource("cases")
        fun `reports no finding if ranges are ignored`(code: String) {
            assertThat(MagicNumber(TestConfig(IGNORE_RANGES to "true")).lint(code))
                .isEmpty()
        }

        @Test
        fun `reports a finding for a parenthesized number if ranges are ignored`() {
            val code = "val foo : Int = (127)"
            assertThat(MagicNumber(TestConfig(IGNORE_RANGES to "true")).lint(code)).hasSize(1)
        }

        @Test
        fun `reports a finding for an addition if ranges are ignored`() {
            val code = "val foo : Int = 1 + 27"
            assertThat(MagicNumber(TestConfig(IGNORE_RANGES to "true")).lint(code)).hasSize(1)
        }
    }

    @Nested
    inner class `a number assigned to a local variable` {

        val code = """fun f() { val a = 3; }"""

        @Test
        fun `reports 3 due to the assignment to a local variable`() {
            val rule = MagicNumber(TestConfig(IGNORE_LOCAL_VARIABLES to "false"))
            assertThat(rule.lint(code)).hasSize(1)
        }

        @Test
        fun `should not report 3 due to the ignored local variable config`() {
            val rule = MagicNumber(TestConfig(IGNORE_LOCAL_VARIABLES to "true"))
            assertThat(rule.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `meaningful variables - #1536` {

        private val rule = MagicNumber(
            TestConfig(
                IGNORE_LOCAL_VARIABLES to "true",
                IGNORE_NAMED_ARGUMENT to "true",
            )
        )

        @Test
        fun `should report 3`() {
            assertThat(rule.lint("""fun bar() { foo(3) }; fun foo(n: Int) {}""")).hasSize(1)
        }

        @Test
        fun `should not report named 3`() {
            assertThat(rule.lint("""fun bar() { foo(param=3) }; fun foo(param: Int) {}""")).isEmpty()
        }

        @Test
        fun `should not report 3 due to scoped describing variable`() {
            assertThat(rule.lint("""fun bar() { val a = 3; foo(a) }; fun foo(n: Int) {}""")).isEmpty()
        }
    }

    @Nested
    inner class `with extension function` {

        private val rule = MagicNumber(
            TestConfig(IGNORE_EXTENSION_FUNCTIONS to "true")
        )

        @Test
        fun `should not report when function`() {
            val code = """
                fun Int.dp() = this + 1
                
                val a = 500.dp()
            """.trimIndent()

            assertThat(rule.lint(code)).isEmpty()
        }

        @Test
        fun `should not report when property`() {
            val code = """
                val Int.dp: Int
                  get() = this + 1
                
                val a = 500.dp
            """.trimIndent()

            assertThat(rule.lint(code)).isEmpty()
        }

        @Test
        fun `should report the argument`() {
            val code = """
                fun Int.dp(a: Int) = this + a
                
                val a = 500.dp(400)
            """.trimIndent()

            assertThat(rule.lint(code)).hasSize(1)
        }
    }

    @Nested
    inner class `unsigned integer literals` {

        @Test
        fun `should report unsigned integer literal`() {
            val code = "val myUInt = 65520U"
            val findings = MagicNumber(Config.empty).lint(code)
            assertThat(findings).hasStartSourceLocation(1, 14)
        }

        @Test
        fun `should not report unsigned integer literal when ignored`() {
            val code = "val myUInt = 65520U"
            val findings = MagicNumber(TestConfig(IGNORE_NUMBERS to listOf("65520U"))).lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should report unsigned long literal`() {
            val code = "val myULong = 65520UL"
            val findings = MagicNumber(Config.empty).lint(code)
            assertThat(findings).hasStartSourceLocation(1, 15)
        }

        @Test
        fun `should not report unsigned long literal when ignored`() {
            val code = "val myULong = 65520UL"
            val findings = MagicNumber(TestConfig(IGNORE_NUMBERS to listOf("65520UL"))).lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should report unsigned hex literal`() {
            val code = "val myUHex = 0xFFF0U"
            val findings = MagicNumber(Config.empty).lint(code)
            assertThat(findings).hasStartSourceLocation(1, 14)
        }

        @Test
        fun `should not report unsigned hex literal when ignored`() {
            val code = "val myUHex = 0xFFF0U"
            val findings = MagicNumber(TestConfig(IGNORE_NUMBERS to listOf("0xFFF0U"))).lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should report unsigned hex long literal`() {
            val code = "val myUHexLong = 0xFFF0UL"
            val findings = MagicNumber(Config.empty).lint(code)
            assertThat(findings).hasStartSourceLocation(1, 18)
        }

        @Test
        fun `should report unsigned literals in function calls`() {
            val code = """
                fun someFunction(x: UInt) {}
                fun test() { someFunction(65520U) }
            """.trimIndent()
            val findings = MagicNumber(Config.empty).lint(code)
            assertThat(findings).hasStartSourceLocation(2, 27)
        }

        @Test
        fun `should report unsigned literals in array initialization`() {
            val code = "val array = arrayOf(1U, 2U, 65520U)"
            val findings = MagicNumber(Config.empty).lint(code)
            assertThat(findings).hasStartSourceLocation(1, 29)
        }

        @Test
        fun `should not report unsigned literals in property declarations by default`() {
            val code = "val myUInt = 65520U"
            val findings = MagicNumber(Config.empty).lint(code)
            assertThat(findings).hasStartSourceLocation(1, 14)
        }

        @Test
        fun `should not report unsigned literals in property declarations when ignored`() {
            val code = "val myUInt = 65520U"
            val findings = MagicNumber(TestConfig(IGNORE_PROPERTY_DECLARATION to "true")).lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should report multiple unsigned literals`() {
            val code = """
                val a = 65520U
                val b = 0xFFF0UL
                val c = 12345U
            """.trimIndent()
            val findings = MagicNumber(Config.empty).lint(code)
            assertThat(findings).hasStartSourceLocations(
                SourceLocation(1, 9),
                SourceLocation(2, 9),
                SourceLocation(3, 9)
            )
        }

        @Test
        fun `should report unsigned integer literal with lowercase u`() {
            val code = "val myUInt = 65520u"
            val findings = MagicNumber(Config.empty).lint(code)
            assertThat(findings).hasStartSourceLocation(1, 14)
        }

        @Test
        fun `should not report unsigned integer literal with lowercase u when ignored`() {
            val code = "val myUInt = 65520u"
            val findings = MagicNumber(TestConfig(IGNORE_NUMBERS to listOf("65520u"))).lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should report unsigned long literal with lowercase ul`() {
            val code = "val myULong = 65520ul"
            val findings = MagicNumber(Config.empty).lint(code)
            assertThat(findings).hasStartSourceLocation(1, 15)
        }

        @Test
        fun `should not report unsigned long literal with lowercase ul when ignored`() {
            val code = "val myULong = 65520ul"
            val findings = MagicNumber(TestConfig(IGNORE_NUMBERS to listOf("65520ul"))).lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should report unsigned hex literal with lowercase u`() {
            val code = "val myUHex = 0xFFF0u"
            val findings = MagicNumber(Config.empty).lint(code)
            assertThat(findings).hasStartSourceLocation(1, 14)
        }

        @Test
        fun `should not report unsigned hex literal with lowercase u when ignored`() {
            val code = "val myUHex = 0xFFF0u"
            val findings = MagicNumber(TestConfig(IGNORE_NUMBERS to listOf("0xFFF0u"))).lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should report unsigned hex long literal with lowercase ul`() {
            val code = "val myUHexLong = 0xFFF0ul"
            val findings = MagicNumber(Config.empty).lint(code)
            assertThat(findings).hasStartSourceLocation(1, 18)
        }

        @Test
        fun `should not report unsigned hex long literal with lowercase ul when ignored`() {
            val code = "val myUHexLong = 0xFFF0ul"
            val findings = MagicNumber(TestConfig(IGNORE_NUMBERS to listOf("0xFFF0ul"))).lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should report lowercase unsigned literals in function calls`() {
            val code = """
                fun someFunction(x: UInt) {}
                fun test() { someFunction(65520u) }
            """.trimIndent()
            val findings = MagicNumber(Config.empty).lint(code)
            assertThat(findings).hasStartSourceLocation(2, 27)
        }

        @Test
        fun `should report lowercase unsigned literals in array initialization`() {
            val code = "val array = arrayOf(1u, 2u, 65520u)"
            val findings = MagicNumber(Config.empty).lint(code)
            assertThat(findings).hasStartSourceLocation(1, 29)
        }

        @Test
        fun `should report mixed case unsigned literals`() {
            val code = """
                val a = 65520u
                val b = 0xFFF0ul
                val c = 12345U
                val d = 0xABCDUL
            """.trimIndent()
            val findings = MagicNumber(Config.empty).lint(code)
            assertThat(findings).hasStartSourceLocations(
                SourceLocation(1, 9),
                SourceLocation(2, 9),
                SourceLocation(3, 9),
                SourceLocation(4, 9)
            )
        }

        @Test
        fun `should handle binary literals with lowercase unsigned suffixes`() {
            val code = """
                val binary1 = 0b1010u
                val binary2 = 0b1111ul
            """.trimIndent()
            val findings = MagicNumber(Config.empty).lint(code)
            assertThat(findings).hasStartSourceLocations(
                SourceLocation(1, 15),
                SourceLocation(2, 15)
            )
        }

        @Test
        fun `should not report lowercase unsigned literals when ignoring property declarations`() {
            val code = """
                val myUInt = 65520u
                val myULong = 0xFFF0ul
            """.trimIndent()
            val findings = MagicNumber(TestConfig(IGNORE_PROPERTY_DECLARATION to "true")).lint(code)
            assertThat(findings).isEmpty()
        }
    }
}
