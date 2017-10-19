package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileContentForTest
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Java6Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertFailsWith

class MagicNumberSpec : Spek({

	given("a float of 1") {
		val ktFile = compileContentForTest("val myFloat = 1.0f")

		it("should not be reported by default") {
			val findings = MagicNumber().lint(ktFile)
			assertThat(findings).hasSize(0)
		}

		it("should be reported when ignoredNumbers is empty") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_NUMBERS to ""))).lint(ktFile)
			assertThat(findings).hasSize(1)
		}
	}

	given("a const float of 1") {
		val ktFile = compileContentForTest("const val MY_FLOAT = 1.0f")

		it("should not be reported by default") {
			val findings = MagicNumber().lint(ktFile)
			assertThat(findings).hasSize(0)
		}

		it("should not be reported when ignoredNumbers is empty") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_NUMBERS to ""))).lint(ktFile)
			assertThat(findings).hasSize(0)
		}
	}

	given("an integer of 1") {
		val ktFile = compileContentForTest("val myInt = 1")

		it("should not be reported by default") {
			val findings = MagicNumber().lint(ktFile)
			assertThat(findings).hasSize(0)
		}

		it("should be reported when ignoredNumbers is empty") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_NUMBERS to ""))).lint(ktFile)
			assertThat(findings).hasSize(1)
		}
	}

	given("a const integer of 1") {
		val ktFile = compileContentForTest("const val MY_INT = 1")

		it("should not be reported by default") {
			val findings = MagicNumber().lint(ktFile)
			assertThat(findings).hasSize(0)
		}

		it("should not be reported when ignoredNumbers is empty") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_NUMBERS to ""))).lint(ktFile)
			assertThat(findings).hasSize(0)
		}
	}

	given("a long of 1") {
		val ktFile = compileContentForTest("val myLong = 1L")

		it("should not be reported by default") {
			val findings = MagicNumber().lint(ktFile)
			assertThat(findings).hasSize(0)
		}

		it("should be reported when ignoredNumbers is empty") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_NUMBERS to ""))).lint(ktFile)
			assertThat(findings).hasSize(1)
		}
	}

	given("a long of -1") {
		val ktFile = compileContentForTest("val myLong = -1L")

		it("should not be reported by default") {
			val findings = MagicNumber().lint(ktFile)
			assertThat(findings).hasSize(0)
		}

		it("should be reported when ignoredNumbers is empty") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_NUMBERS to ""))).lint(ktFile)
			assertThat(findings).hasSize(1)
		}
	}

	given("a long of -2") {
		val ktFile = compileContentForTest("val myLong = -2L")

		it("should be reported by default") {
			val findings = MagicNumber().lint(ktFile)
			assertThat(findings).hasSize(1)
		}

		it("should be ignored when ignoredNumbers contains it verbatim") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_NUMBERS to "-2L"))).lint(ktFile)
			assertThat(findings).isEmpty()
		}

		it("should be ignored when ignoredNumbers contains it as floating point") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_NUMBERS to "-2f"))).lint(ktFile)
			assertThat(findings).isEmpty()
		}

		it("should not be ignored when ignoredNumbers contains 2 but not -2") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_NUMBERS to "1,2,3,-1,0"))).lint(ktFile)
			assertThat(findings).hasSize(1)
		}
	}

	given("a const long of 1") {
		val ktFile = compileContentForTest("const val MY_LONG = 1L")

		it("should not be reported by default") {
			val findings = MagicNumber().lint(ktFile)
			assertThat(findings).hasSize(0)
		}

		it("should not be reported when ignoredNumbers is empty") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_NUMBERS to ""))).lint(ktFile)
			assertThat(findings).hasSize(0)
		}
	}

	given("a double of 1") {
		val ktFile = compileContentForTest("val myDouble = 1.0")

		it("should not be reported by default") {
			val findings = MagicNumber().lint(ktFile)
			assertThat(findings).hasSize(0)
		}

		it("should be reported when ignoredNumbers is empty") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_NUMBERS to ""))).lint(ktFile)
			assertThat(findings).hasSize(1)
		}
	}

	given("a const double of 1") {
		val ktFile = compileContentForTest("const val MY_DOUBLE = 1.0")

		it("should not be reported by default") {
			val findings = MagicNumber().lint(ktFile)
			assertThat(findings).hasSize(0)
		}

		it("should not be reported when ignoredNumbers is empty") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_NUMBERS to ""))).lint(ktFile)
			assertThat(findings).hasSize(0)
		}
	}

	given("a hex of 1") {
		val ktFile = compileContentForTest("val myHex = 0x1")

		it("should not be reported by default") {
			val findings = MagicNumber().lint(ktFile)
			assertThat(findings).hasSize(0)
		}

		it("should be reported when ignoredNumbers is empty") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_NUMBERS to ""))).lint(ktFile)
			assertThat(findings).hasSize(1)
		}
	}

	given("a const hex of 1") {
		val ktFile = compileContentForTest("const val MY_HEX = 0x1")

		it("should not be reported by default") {
			val findings = MagicNumber().lint(ktFile)
			assertThat(findings).hasSize(0)
		}

		it("should not be reported when ignoredNumbers is empty") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_NUMBERS to ""))).lint(ktFile)
			assertThat(findings).hasSize(0)
		}
	}

	given("an integer of 300") {
		val ktFile = compileContentForTest("val myInt = 300")

		it("should not be reported when ignoredNumbers contains 300") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_NUMBERS to "300"))).lint(ktFile)
			assertThat(findings).hasSize(0)
		}

		it("should not be reported when ignoredNumbers contains a floating point 300") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_NUMBERS to "300.0"))).lint(ktFile)
			assertThat(findings).hasSize(0)
		}
	}

	given("a binary literal") {
		val ktFile = compileContentForTest("val myBinary = 0b01001")

		it("should not be reported") {
			val findings = MagicNumber().lint(ktFile)
			assertThat(findings).hasSize(1)
		}

		it("should not be reported when ignoredNumbers contains a binary literal 0b01001") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_NUMBERS to "0b01001"))).lint(ktFile)
			assertThat(findings).hasSize(0)
		}
	}

	given("an integer literal with underscores") {
		val ktFile = compileContentForTest("val myInt = 100_000")

		it("should be reported by default") {
			val findings = MagicNumber().lint(ktFile)
			assertThat(findings).hasSize(1)
		}

		it("should not be reported when ignored verbatim") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_NUMBERS to "100_000"))).lint(ktFile)
			assertThat(findings).isEmpty()
		}

		it("should not be reported when ignored with different underscores") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_NUMBERS to "10_00_00"))).lint(ktFile)
			assertThat(findings).isEmpty()
		}

		it("should not be reported when ignored without underscores") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_NUMBERS to "100000"))).lint(ktFile)
			assertThat(findings).isEmpty()
		}
	}

	given("an if statement with magic numbers") {
		val code = "val myInt = if (5 < 6) 7 else 8"

		it("should be reported") {
			val findings = MagicNumber().lint(code)
			assertThat(findings).hasSize(4)
		}
	}

	given("a when statement with magic numbers") {
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
			assertThat(findings).hasSize(6)
		}
	}

	given("a method containing variables with magic numbers") {
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

	given("a boolean value") {
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

	given("a non-numeric constant expression") {
		val code = "val surprise = true"

		it("should not be reported") {
			val findings = MagicNumber().lint(code)
			assertThat(findings).isEmpty()
		}
	}

	given("a float of 0.5") {
		val code = compileContentForTest("val test = 0.5f")

		it("should be reported by default") {
			val findings = MagicNumber().lint(code)
			assertThat(findings).hasSize(1)
		}

		it("should not be reported when ignoredNumbers contains it") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_NUMBERS to ".5"))).lint(code)
			assertThat(findings).hasSize(0)
		}
	}

	given("an invalid ignoredNumber") {

		it("throws a NumberFormatException") {
			assertFailsWith(NumberFormatException::class) {
				MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_NUMBERS to "banana")))
			}
		}
	}

	given("an empty ignoredNumber") {

		it("doesn't throw an exception") {
			MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_NUMBERS to "")))
		}
	}

	given("ignoring properties") {
		val ktFile = compileContentForTest("""
			@Magic(number = 69)
			class A {
				val boringNumber = 42
				const val BORING_CONSTANT = 93871

				fun hashCode(): Int {
					val iAmSoMagic = 7328672
				}
			}
		""")

		it("should report all without ignore flags") {
			val findings = MagicNumber().lint(ktFile)
			assertThat(findings).hasSize(3)
		}

		it("should not report number in properties") {
			val config = TestConfig(mapOf(MagicNumber.IGNORE_PROPERTY_DECLARATION to "true"))
			val findings = MagicNumber(config).lint(ktFile)
			assertThat(findings).hasSize(2)
		}

		it("should not report number in annotation") {
			val config = TestConfig(mapOf(MagicNumber.IGNORE_ANNOTATION to "true"))
			val findings = MagicNumber(config).lint(ktFile)
			assertThat(findings).hasSize(2)
		}

		it("should not report number in hashCode") {
			val config = TestConfig(mapOf(MagicNumber.IGNORE_HASH_CODE to "true"))
			val findings = MagicNumber(config).lint(ktFile)
			assertThat(findings).hasSize(2)
		}

		it("should not report any issues with all ignore flags") {
			val config = TestConfig(mapOf(
					MagicNumber.IGNORE_PROPERTY_DECLARATION to "true",
					MagicNumber.IGNORE_ANNOTATION to "true",
					MagicNumber.IGNORE_HASH_CODE to "true"))
			val findings = MagicNumber(config).lint(ktFile)
			assertThat(findings).isEmpty()
		}
	}

	given("magic numbers in companion object property assignments") {
		val ktFile = compileContentForTest("""
			class A {

				companion object {
				    val anotherBoringNumber = 43
					const val anotherBoringConstant = 93872
				}
			}
		""")

		it("should not report any issues in those assignments") {
			val findings = MagicNumber().lint(ktFile)
			assertThat(findings).isEmpty()
		}
	}

	given("a property without number number") {

		val code = "private var pair: Pair<String, Int>? = null"

		it("should not lead to a crash #276") {
			assertThat(MagicNumber().lint(code)).isEmpty()
		}
	}

	given("ignoring named arguments") {
		given("in constructor invocation") {
			fun code(numberString: String) = compileContentForTest("""
				data class Model(
						val someVal: Int,
						val other: String = "default"
				)

				var model = Model(someVal = $numberString)
			""")

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
				val rule = MagicNumber(TestConfig(mapOf("ignoreNamedArgument" to "true")))
				assertThat(rule.lint(code("53"))).isEmpty()
			}
		}

		given("in function invocation") {
			fun code(number: Number) = compileContentForTest("""
				fun tested(someVal: Int, other: String = "default")

				tested(someVal = $number)
			""")
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
		given("in enum constructor argument") {
			val ktFile = compileContentForTest("""
				enum class Bag(id: Int) {
					SMALL(1),
					EXTRA_LARGE(5)
				}
			""")
			it("should be reported by default") {
				assertThat(MagicNumber().lint(ktFile)).hasSize(1)
			}
			it("numbers when 'ignoreEnums' is set to true"){
				val rule = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_ENUMS to "true")))
				assertThat(rule.lint(ktFile)).isEmpty()
			}
		}
		given("in enum constructor as named argument"){
			val ktFile = compileContentForTest("""
				enum class Bag(id: Int) {
					SMALL(id = 1),
					EXTRA_LARGE(id = 5)
				}
			""")
			it("should be reported by default") {
				assertThat(MagicNumber().lint(ktFile)).hasSize(1)
			}
			it("numbers when 'ignoreEnums' is set to true"){
				val rule = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_ENUMS to "true")))
				assertThat(rule.lint(ktFile)).isEmpty()
			}
		}
	}

	given("functions with and without braces which return values") {

		it("does not report functions that always returns a constant value") {
			val code = """
				fun x() = 9
				fun y() { return 9 }"""
			assertThat(MagicNumber().lint(code)).isEmpty()
		}

		it("reports functions that does not return a constant value") {
			val code = """
				fun x() = 9 + 1
				fun y(): Int { return 9 + 1 }"""
			assertThat(MagicNumber().lint(code)).hasSize(2)
		}
	}
})
