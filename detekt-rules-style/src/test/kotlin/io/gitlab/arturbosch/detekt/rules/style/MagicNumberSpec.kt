package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.rules.style.MagicNumber.Companion.IGNORE_NUMBERS
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assert
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

@Suppress("LargeClass")
class MagicNumberSpec : Spek({

    describe("Magic Number rule") {

        context("a float of 1") {
            val code = "val myFloat = 1.0f"

            it("should not be reported by default") {
                val findings = MagicNumber().lint(code)
                assertThat(findings).isEmpty()
            }

            it("should be reported when ignoredNumbers is empty") {
                val findings = MagicNumber(TestConfig(mapOf(IGNORE_NUMBERS to emptyList<String>()))).lint(code)
                assertThat(findings).hasSourceLocation(1, 15)
            }
        }

        context("a const float of 1") {
            val code = "const val MY_FLOAT = 1.0f"

            it("should not be reported by default") {
                val findings = MagicNumber().lint(code)
                assertThat(findings).isEmpty()
            }

            it("should not be reported when ignoredNumbers is empty") {
                val findings = MagicNumber(TestConfig(mapOf(IGNORE_NUMBERS to emptyList<String>()))).lint(code)
                assertThat(findings).isEmpty()
            }
        }

        context("an integer of 1") {
            val code = "val myInt = 1"

            it("should not be reported by default") {
                val findings = MagicNumber().lint(code)
                assertThat(findings).isEmpty()
            }

            it("should be reported when ignoredNumbers is empty") {
                val findings = MagicNumber(TestConfig(mapOf(IGNORE_NUMBERS to emptyList<String>()))).lint(code)
                assertThat(findings).hasSourceLocation(1, 13)
            }
        }

        context("a const integer of 1") {
            val code = "const val MY_INT = 1"

            it("should not be reported by default") {
                val findings = MagicNumber().lint(code)
                assertThat(findings).isEmpty()
            }

            it("should not be reported when ignoredNumbers is empty") {
                val findings = MagicNumber(TestConfig(mapOf(IGNORE_NUMBERS to emptyList<String>()))).lint(code)
                assertThat(findings).isEmpty()
            }
        }

        context("a long of 1") {
            val code = "val myLong = 1L"

            it("should not be reported by default") {
                val findings = MagicNumber().lint(code)
                assertThat(findings).isEmpty()
            }

            it("should be reported when ignoredNumbers is empty") {
                val findings = MagicNumber(TestConfig(mapOf(IGNORE_NUMBERS to emptyList<String>()))).lint(code)
                assertThat(findings).hasSourceLocation(1, 14)
            }
        }

        context("a long of -1") {
            val code = "val myLong = -1L"

            it("should not be reported by default") {
                val findings = MagicNumber().lint(code)
                assertThat(findings).isEmpty()
            }

            it("should be reported when ignoredNumbers is empty") {
                val findings = MagicNumber(TestConfig(mapOf(IGNORE_NUMBERS to emptyList<String>()))).lint(code)
                assertThat(findings).hasSourceLocation(1, 15)
            }
        }

        context("a long of -2") {
            val code = "val myLong = -2L"

            it("should be reported by default") {
                val findings = MagicNumber().lint(code)
                assertThat(findings).hasSourceLocation(1, 15)
            }

            it("should be ignored when ignoredNumbers contains it verbatim") {
                val findings = MagicNumber(TestConfig(mapOf(IGNORE_NUMBERS to listOf("-2L")))).lint(code)
                assertThat(findings).isEmpty()
            }

            it("should be ignored when ignoredNumbers contains it as floating point") {
                val findings = MagicNumber(TestConfig(mapOf(IGNORE_NUMBERS to listOf("-2f")))).lint(code)
                assertThat(findings).isEmpty()
            }

            it("should not be ignored when ignoredNumbers contains 2 but not -2") {
                val findings = MagicNumber(TestConfig(mapOf(IGNORE_NUMBERS to listOf("1", "2", "3", "-1", "0"))))
                    .lint(code)
                assertThat(findings).hasSourceLocation(1, 15)
            }

            it("should not be ignored when ignoredNumbers contains 2 but not -2 config with string") {
                val findings = MagicNumber(TestConfig(mapOf(IGNORE_NUMBERS to "1,2,3,-1,0")))
                    .lint(code)
                assertThat(findings).hasSourceLocation(1, 15)
            }
        }

        context("a const long of 1") {
            val code = "const val MY_LONG = 1L"

            it("should not be reported by default") {
                val findings = MagicNumber().lint(code)
                assertThat(findings).isEmpty()
            }

            it("should not be reported when ignoredNumbers is empty") {
                val findings = MagicNumber(TestConfig(mapOf(IGNORE_NUMBERS to emptyList<String>()))).lint(code)
                assertThat(findings).isEmpty()
            }
        }

        context("a double of 1") {
            val code = "val myDouble = 1.0"

            it("should not be reported by default") {
                val findings = MagicNumber().lint(code)
                assertThat(findings).isEmpty()
            }

            it("should be reported when ignoredNumbers is empty") {
                val findings = MagicNumber(TestConfig(mapOf(IGNORE_NUMBERS to emptyList<String>()))).lint(code)
                assertThat(findings).hasSourceLocation(1, 16)
            }
        }

        context("a const double of 1") {
            val code = "const val MY_DOUBLE = 1.0"

            it("should not be reported by default") {
                val findings = MagicNumber().lint(code)
                assertThat(findings).isEmpty()
            }

            it("should not be reported when ignoredNumbers is empty") {
                val findings = MagicNumber(TestConfig(mapOf(IGNORE_NUMBERS to emptyList<String>()))).lint(code)
                assertThat(findings).isEmpty()
            }
        }

        context("a hex of 1") {
            val code = "val myHex = 0x1"

            it("should not be reported by default") {
                val findings = MagicNumber().lint(code)
                assertThat(findings).isEmpty()
            }

            it("should be reported when ignoredNumbers is empty") {
                val findings = MagicNumber(TestConfig(mapOf(IGNORE_NUMBERS to emptyList<String>()))).lint(code)
                assertThat(findings).hasSourceLocation(1, 13)
            }
        }

        context("a const hex of 1") {
            val code = "const val MY_HEX = 0x1"

            it("should not be reported by default") {
                val findings = MagicNumber().lint(code)
                assertThat(findings).isEmpty()
            }

            it("should not be reported when ignoredNumbers is empty") {
                val findings = MagicNumber(TestConfig(mapOf(IGNORE_NUMBERS to emptyList<String>()))).lint(code)
                assertThat(findings).isEmpty()
            }
        }

        context("an integer of 300") {
            val code = "val myInt = 300"

            it("should not be reported when ignoredNumbers contains 300") {
                val findings = MagicNumber(TestConfig(mapOf(IGNORE_NUMBERS to listOf("300")))).lint(code)
                assertThat(findings).isEmpty()
            }

            it("should not be reported when ignoredNumbers contains a floating point 300") {
                val findings = MagicNumber(TestConfig(mapOf(IGNORE_NUMBERS to listOf("300.0")))).lint(code)
                assertThat(findings).isEmpty()
            }
        }

        context("a binary literal") {
            val code = "val myBinary = 0b01001"

            it("should not be reported") {
                val findings = MagicNumber().lint(code)
                assertThat(findings).hasSize(1)
            }

            it("should not be reported when ignoredNumbers contains a binary literal 0b01001") {
                val findings = MagicNumber(TestConfig(mapOf(IGNORE_NUMBERS to listOf("0b01001")))).lint(code)
                assertThat(findings).isEmpty()
            }
        }

        context("an integer literal with underscores") {
            val code = "val myInt = 100_000"

            it("should be reported by default") {
                val findings = MagicNumber().lint(code)
                assertThat(findings).hasSourceLocation(1, 13)
            }

            it("should not be reported when ignored verbatim") {
                val findings = MagicNumber(TestConfig(mapOf(IGNORE_NUMBERS to listOf("100_000")))).lint(code)
                assertThat(findings).isEmpty()
            }

            it("should not be reported when ignored with different underscores") {
                val findings = MagicNumber(TestConfig(mapOf(IGNORE_NUMBERS to listOf("10_00_00")))).lint(code)
                assertThat(findings).isEmpty()
            }

            it("should not be reported when ignored without underscores") {
                val findings = MagicNumber(TestConfig(mapOf(IGNORE_NUMBERS to listOf("100000")))).lint(code)
                assertThat(findings).isEmpty()
            }
        }

        context("an if statement with magic numbers") {
            val code = "val myInt = if (5 < 6) 7 else 8"

            it("should be reported") {
                val findings = MagicNumber().lint(code)
                assertThat(findings)
                    .hasSourceLocations(
                        SourceLocation(1, 17),
                        SourceLocation(1, 21),
                        SourceLocation(1, 24),
                        SourceLocation(1, 31)
                    )
            }
        }

        context("a when statement with magic numbers") {
            val code = """
            fun test(x: Int) {
                when (x) {
                    5 -> return 5
                    4 -> return 4
                    3 -> return 3
                }
            }
        """

            it("should be reported") {
                val findings = MagicNumber().lint(code)
                assertThat(findings).hasSourceLocations(
                    SourceLocation(3, 9),
                    SourceLocation(3, 21),
                    SourceLocation(4, 9),
                    SourceLocation(4, 21),
                    SourceLocation(5, 9),
                    SourceLocation(5, 21)
                )
            }
        }

        context("a method containing variables with magic numbers") {
            val code = """
            fun test(x: Int) {
                val i = 5
            }
        """

            it("should be reported") {
                val findings = MagicNumber().lint(code)
                assertThat(findings).hasSize(1)
            }
        }

        context("a boolean value") {
            val code = """
            fun test() : Boolean {
                return true;
            }
        """

            it("should not be reported") {
                val findings = MagicNumber().lint(code)
                assertThat(findings).isEmpty()
            }
        }

        context("a non-numeric constant expression") {
            val code = "val surprise = true"

            it("should not be reported") {
                val findings = MagicNumber().lint(code)
                assertThat(findings).isEmpty()
            }
        }

        context("a float of 0.5") {
            val code = "val test = 0.5f"

            it("should be reported by default") {
                val findings = MagicNumber().lint(code)
                assertThat(findings).hasSourceLocation(1, 12)
            }

            it("should not be reported when ignoredNumbers contains it") {
                val findings = MagicNumber(TestConfig(mapOf(IGNORE_NUMBERS to listOf(".5")))).lint(code)
                assertThat(findings).isEmpty()
            }
        }

        context("a magic number number in a constructor call") {

            it("should report") {
                val code = "val file = Array<String?>(42) { null }"
                val findings = MagicNumber().compileAndLint(code)
                assertThat(findings).hasSize(1)
            }
        }

        context("an invalid ignoredNumber") {

            it("throws a NumberFormatException") {
                assertThatExceptionOfType(NumberFormatException::class.java).isThrownBy {
                    MagicNumber(TestConfig(mapOf(IGNORE_NUMBERS to listOf("banana"))))
                }
            }
        }

        context("an empty ignoredNumber") {

            it("doesn't throw an exception") {
                MagicNumber(TestConfig(mapOf(IGNORE_NUMBERS to emptyList<String>())))
            }
        }

        context("ignoring properties") {
            val code = """
            @Magic(number = 69)
            class A {
                val boringNumber = 42
                const val BORING_CONSTANT = 93871

                override fun hashCode(): Int {
                    val iAmSoMagic = 7328672
                }

                companion object {
                    val anotherBoringNumber = 43
                    const val anotherBoringConstant = 93872
                }
            }
        """

            it("should report all without ignore flags") {
                val config = TestConfig(
                        mapOf(
                                MagicNumber.IGNORE_PROPERTY_DECLARATION to "false",
                                MagicNumber.IGNORE_ANNOTATION to "false",
                                MagicNumber.IGNORE_HASH_CODE to "false",
                                MagicNumber.IGNORE_CONSTANT_DECLARATION to "false",
                                MagicNumber.IGNORE_COMPANION_OBJECT_PROPERTY_DECLARATION to "false"
                        )
                )

                val findings = MagicNumber(config).lint(code)
                assertThat(findings)
                    .hasSourceLocations(
                        SourceLocation(1, 17),
                        SourceLocation(3, 24),
                        SourceLocation(4, 33),
                        SourceLocation(7, 26),
                        SourceLocation(11, 35),
                        SourceLocation(12, 43)
                    )
            }

            it("should not report any issues with all ignore flags") {
                val config = TestConfig(
                        mapOf(
                                MagicNumber.IGNORE_PROPERTY_DECLARATION to "true",
                                MagicNumber.IGNORE_ANNOTATION to "true",
                                MagicNumber.IGNORE_HASH_CODE to "true",
                                MagicNumber.IGNORE_CONSTANT_DECLARATION to "true",
                                MagicNumber.IGNORE_COMPANION_OBJECT_PROPERTY_DECLARATION to "true"
                        )
                )

                val findings = MagicNumber(config).lint(code)
                assertThat(findings).isEmpty()
            }
        }

        context("magic numbers in companion object property assignments") {
            val code = """
            class A {

                companion object {
                    val anotherBoringNumber = 43
                    const val anotherBoringConstant = 93872
                }
            }
        """

            it("should not report any issues by default") {
                val findings = MagicNumber().lint(code)
                assertThat(findings).isEmpty()
            }

            it("should not report any issues when ignoring properties but not constants nor companion objects") {
                val config = TestConfig(
                        mapOf(
                                MagicNumber.IGNORE_PROPERTY_DECLARATION to "true",
                                MagicNumber.IGNORE_CONSTANT_DECLARATION to "false",
                                MagicNumber.IGNORE_COMPANION_OBJECT_PROPERTY_DECLARATION to "false"
                        )
                )

                val findings = MagicNumber(config).lint(code)
                assertThat(findings).isEmpty()
            }

            it("should not report any issues when ignoring properties and constants but not companion objects") {
                val config = TestConfig(
                        mapOf(
                                MagicNumber.IGNORE_PROPERTY_DECLARATION to "true",
                                MagicNumber.IGNORE_CONSTANT_DECLARATION to "true",
                                MagicNumber.IGNORE_COMPANION_OBJECT_PROPERTY_DECLARATION to "false"
                        )
                )

                val findings = MagicNumber(config).lint(code)
                assertThat(findings).isEmpty()
            }

            it("should not report any issues when ignoring properties, constants and companion objects") {
                val config = TestConfig(
                        mapOf(
                                MagicNumber.IGNORE_PROPERTY_DECLARATION to "true",
                                MagicNumber.IGNORE_CONSTANT_DECLARATION to "true",
                                MagicNumber.IGNORE_COMPANION_OBJECT_PROPERTY_DECLARATION to "true"
                        )
                )

                val findings = MagicNumber(config).lint(code)
                assertThat(findings).isEmpty()
            }

            it("should not report any issues when ignoring companion objects but not properties and constants") {
                val config = TestConfig(
                        mapOf(
                                MagicNumber.IGNORE_PROPERTY_DECLARATION to "false",
                                MagicNumber.IGNORE_CONSTANT_DECLARATION to "false",
                                MagicNumber.IGNORE_COMPANION_OBJECT_PROPERTY_DECLARATION to "true"
                        )
                )

                val findings = MagicNumber(config).lint(code)
                assertThat(findings).isEmpty()
            }

            it("should report property when ignoring constants but not properties and companion objects") {
                val config = TestConfig(
                        mapOf(
                                MagicNumber.IGNORE_PROPERTY_DECLARATION to "false",
                                MagicNumber.IGNORE_CONSTANT_DECLARATION to "true",
                                MagicNumber.IGNORE_COMPANION_OBJECT_PROPERTY_DECLARATION to "false"
                        )
                )

                val findings = MagicNumber(config).lint(code)
                assertThat(findings).hasSourceLocation(4, 35)
            }

            it("should report property and constant when not ignoring properties, constants nor companion objects") {
                val config = TestConfig(
                        mapOf(
                                MagicNumber.IGNORE_PROPERTY_DECLARATION to "false",
                                MagicNumber.IGNORE_CONSTANT_DECLARATION to "false",
                                MagicNumber.IGNORE_COMPANION_OBJECT_PROPERTY_DECLARATION to "false"
                        )
                )

                val findings = MagicNumber(config).lint(code)
                assertThat(findings)
                    .hasSourceLocations(
                        SourceLocation(4, 35),
                        SourceLocation(5, 43)
                    )
            }
        }

        context("a property without number") {
            val code = "private var pair: Pair<String, Int>? = null"

            it("should not lead to a crash #276") {
                val findings = MagicNumber().lint(code)
                assertThat(findings).isEmpty()
            }
        }

        context("ignoring named arguments") {
            context("in constructor invocation") {
                fun code(numberString: String) = """
                data class Model(
                        val someVal: Int,
                        val other: String = "default"
                )

                var model = Model(someVal = $numberString)
            """

                it("should not ignore int by default") {
                    assertThat(MagicNumber().lint(code("53"))).hasSize(1)
                }

                it("should not ignore float by default") {
                    assertThat(MagicNumber().lint(code("53f"))).hasSize(1)
                }

                it("should not ignore binary by default") {
                    assertThat(MagicNumber().lint(code("0b01001"))).hasSize(1)
                }

                it("should ignore integer with underscores") {
                    assertThat(MagicNumber().lint(code("101_000"))).hasSize(1)
                }

                it("should ignore numbers when 'ignoreNamedArgument' is set to true") {
                    val rule = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_NAMED_ARGUMENT to "true")))
                    assertThat(rule.lint(code("53"))).isEmpty()
                }

                it("should ignore numbers when 'ignoreNamedArgument' is set to true and value is negative") {
                    val rule = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_NAMED_ARGUMENT to "true")))
                    assertThat(rule.lint(code("-53"))).isEmpty()
                }

                it("should ignore named arguments in inheritance - #992") {
                    val code = """
                    abstract class A(n: Int)

                    object B : A(n = 5)
                """
                    val rule = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_NAMED_ARGUMENT to "true")))
                    assertThat(rule.compileAndLint(code)).isEmpty()
                }

                it("should ignore named arguments in parameter annotations - #1115") {
                    val code =
                            "@JvmStatic fun setCustomDimension(@IntRange(from = 0, to = 19) index: Int, value: String?) {}"
                    MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_NAMED_ARGUMENT to "true")))
                            .lint(code)
                            .assert()
                            .isEmpty()
                }
            }

            context("Issue#659 - false-negative reporting on unnamed argument when ignore is true") {

                fun code(numberString: String) = """
                data class Model(
                        val someVal: Int,
                        val other: String = "default"
                )

                var model = Model($numberString)
            """

                it("should detect the argument") {
                    val rule = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_NAMED_ARGUMENT to "true")))
                    assertThat(rule.lint(code("53"))).hasSize(1)
                }
            }

            context("in function invocation") {
                fun code(number: Number) = """
                fun tested(someVal: Int, other: String = "default")

                tested(someVal = $number)
            """
                it("should ignore int by default") {
                    assertThat(MagicNumber().lint(code(53))).isEmpty()
                }

                it("should ignore float by default") {
                    assertThat(MagicNumber().lint(code(53f))).isEmpty()
                }

                it("should ignore binary by default") {
                    assertThat(MagicNumber().lint(code(0b01001))).isEmpty()
                }

                it("should ignore integer with underscores") {
                    assertThat(MagicNumber().lint(code(101_000))).isEmpty()
                }
            }
            context("in enum constructor argument") {
                val code = """
                enum class Bag(id: Int) {
                    SMALL(1),
                    EXTRA_LARGE(5)
                }
            """
                it("should be reported by default") {
                    assertThat(MagicNumber().lint(code)).hasSize(1)
                }
                it("numbers when 'ignoreEnums' is set to true") {
                    val rule = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_ENUMS to "true")))
                    assertThat(rule.lint(code)).isEmpty()
                }
            }
            context("in enum constructor as named argument") {
                val code = """
                enum class Bag(id: Int) {
                    SMALL(id = 1),
                    EXTRA_LARGE(id = 5)
                }
            """
                it("should be reported by default") {
                    assertThat(MagicNumber().lint(code)).hasSize(1)
                }
                it("numbers when 'ignoreEnums' is set to true") {
                    val rule = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_ENUMS to "true")))
                    assertThat(rule.lint(code)).isEmpty()
                }
            }
        }

        context("functions with and without braces which return values") {

            it("does not report functions that always returns a constant value") {
                val code = """
                fun x() = 9
                fun y(): Int { return 9 }"""
                assertThat(MagicNumber().compileAndLint(code)).isEmpty()
            }

            it("reports functions that does not return a constant value") {
                val code = """
                fun x() = 9 + 1
                fun y(): Int { return 9 + 1 }"""
                assertThat(MagicNumber().compileAndLint(code)).hasSize(2)
            }
        }

        context("in-class declaration with default parameters") {

            it("reports no finding") {
                val code = "class SomeClassWithDefault(val defaultValue: Int = 10)"
                assertThat(MagicNumber().lint(code)).isEmpty()
            }

            it("reports no finding for an explicit declaration") {
                val code = "class SomeClassWithDefault constructor(val defaultValue: Int = 10)"
                assertThat(MagicNumber().lint(code)).isEmpty()
            }
        }

        context("default parameters in secondary constructor") {

            it("reports no finding") {
                val code = """
                class SomeClassWithDefault {
                    constructor(val defaultValue: Int = 10) { }
                }"""
                assertThat(MagicNumber().lint(code)).isEmpty()
            }
        }

        context("default parameters in function") {

            it("reports no finding") {
                val code = "fun f(p: Int = 100)"
                assertThat(MagicNumber().lint(code)).isEmpty()
            }
        }

        context("a number as part of a range") {

            listOf(
                    "val range = 1..27",
                    "val range = (1..27)",
                    "val range = 27 downTo 1",
                    "val range = 1 until 27 step 1",
                    "val inRange = 1 in 1..27",
                    "val inRange = (1 in 27 downTo 0 step 1)",
                    "val inRange = (1..27 step 1).last"
            ).forEach { codeWithMagicNumberInRange ->
                it("'$codeWithMagicNumberInRange' reports a code smell by default") {
                    val code = codeWithMagicNumberInRange
                    assertThat(MagicNumber().lint(code)).hasSize(1)
                }
                it("'$codeWithMagicNumberInRange' reports a code smell if ranges are not ignored") {
                    val code = codeWithMagicNumberInRange
                    assertThat(MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_RANGES to "false"))).lint(code))
                            .hasSize(1)
                }
                it("'$codeWithMagicNumberInRange' reports no finding if ranges are ignored") {
                    val code = codeWithMagicNumberInRange
                    assertThat(MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_RANGES to "true"))).lint(code))
                            .isEmpty()
                }
            }

            it("reports a finding for a parenthesized number if ranges are ignored") {
                val code = "val foo : Int = (127)"
                assertThat(MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_RANGES to "true"))).lint(code)).hasSize(1)
            }
            it("reports a finding for an addition if ranges are ignored") {
                val code = "val foo : Int = 1 + 27"
                assertThat(MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_RANGES to "true"))).lint(code)).hasSize(1)
            }
        }

        context("a number assigned to a local variable") {

            val code = """fun f() { val a = 3; }"""

            it("reports 3 due to the assignment to a local variable") {
                val rule = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_LOCAL_VARIABLES to "false")))
                assertThat(rule.compileAndLint(code)).hasSize(1)
            }

            it("should not report 3 due to the ignored local variable config") {
                val rule = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_LOCAL_VARIABLES to "true")))
                assertThat(rule.compileAndLint(code)).isEmpty()
            }
        }

        context("meaningful variables - #1536") {

            val rule by memoized {
                MagicNumber(TestConfig(mapOf(
                    MagicNumber.IGNORE_LOCAL_VARIABLES to "true",
                    MagicNumber.IGNORE_NAMED_ARGUMENT to "true")))
            }

            it("should report 3") {
                assertThat(rule.compileAndLint("""fun bar() { foo(3) }; fun foo(n: Int) {}""")).hasSize(1)
            }

            it("should not report named 3") {
                assertThat(rule.compileAndLint("""fun bar() { foo(param=3) }; fun foo(param: Int) {}""")).isEmpty()
            }

            it("should not report 3 due to scoped describing variable") {
                assertThat(rule.compileAndLint("""fun bar() { val a = 3; foo(a) }; fun foo(n: Int) {}""")).isEmpty()
            }
        }
    }
})
