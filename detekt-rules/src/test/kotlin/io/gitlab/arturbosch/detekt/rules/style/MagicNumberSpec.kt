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

	given("a named parameter in a constructor") {
		val code = "class HtlExpressionInsertHandler : HtlTextInsertHandler(offset = 4)"

		it("should be reported") {
			val findings = MagicNumber().lint(code)
			assertThat(findings).hasSize(1)
		}

		it("should not be reported when named parameters are ignored") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_NAMED_PARAMETERS to "true"))).lint(code)
			assertThat(findings).isEmpty()
		}
	}

	given("an expression passed as a named parameter in a constructor") {
		val code = "class HtlExpressionInsertHandler : HtlTextInsertHandler(offset = 4 + 10)"

		it("should be reported") {
			val findings = MagicNumber().lint(code)
			assertThat(findings).hasSize(2)
		}

		it("should still be reported when named parameters are ignored") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_NAMED_PARAMETERS to "true"))).lint(code)
			assertThat(findings).hasSize(2)
		}
	}

	given("a named parameter in a method call") {
		val code = "val number = calculateNumber(four = 4)"

		it("should be reported") {
			val findings = MagicNumber().lint(code)
			assertThat(findings).hasSize(1)
		}

		it("should not be reported when named parameters are ignored") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_NAMED_PARAMETERS to "true"))).lint(code)
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
				const val boringConstant = 93871

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

	given("a property without number number") {

		val code = "private var pair: Pair<String, Int>? = null"

		it("should not lead to a crash #276") {
			assertThat(MagicNumber().lint(code)).isEmpty()
		}
	}
})
