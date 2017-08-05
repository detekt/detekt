package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Java6Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertFailsWith

class MagicNumberSpec : Spek({

	given("a float of 1") {
		val code = "val myFloat = 1.0f"

		it("should not be reported by default") {
			val findings = MagicNumber().lint(code)
			assertThat(findings).hasSize(0)
		}

		it("should be reported when ignoredNumbers is empty") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORED_NUMBERS to ""))).lint(code)
			assertThat(findings).hasSize(1)
		}
	}

	given("a const float of 1") {
		val code = "const val MY_FLOAT = 1.0f"

		it("should not be reported by default") {
			val findings = MagicNumber().lint(code)
			assertThat(findings).hasSize(0)
		}

		it("should not be reported when ignoredNumbers is empty") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORED_NUMBERS to ""))).lint(code)
			assertThat(findings).hasSize(0)
		}
	}

	given("an integer of 1") {
		val code = "val myInt = 1"

		it("should not be reported by default") {
			val findings = MagicNumber().lint(code)
			assertThat(findings).hasSize(0)
		}

		it("should be reported when ignoredNumbers is empty") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORED_NUMBERS to ""))).lint(code)
			assertThat(findings).hasSize(1)
		}
	}

	given("a const integer of 1") {
		val code = "const val MY_INT = 1"

		it("should not be reported by default") {
			val findings = MagicNumber().lint(code)
			assertThat(findings).hasSize(0)
		}

		it("should not be reported when ignoredNumbers is empty") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORED_NUMBERS to ""))).lint(code)
			assertThat(findings).hasSize(0)
		}
	}

	given("a long of 1") {
		val code = "val myLong = 1L"

		it("should not be reported by default") {
			val findings = MagicNumber().lint(code)
			assertThat(findings).hasSize(0)
		}

		it("should be reported when ignoredNumbers is empty") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORED_NUMBERS to ""))).lint(code)
			assertThat(findings).hasSize(1)
		}
	}

	given("a long of -1") {
		val code = "val myLong = -1L"

		it("should not be reported by default") {
			val findings = MagicNumber().lint(code)
			assertThat(findings).hasSize(0)
		}

		it("should be reported when ignoredNumbers is empty") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORED_NUMBERS to ""))).lint(code)
			assertThat(findings).hasSize(1)
		}
	}

	given("a long of -2") {
		val code = "val myLong = -2L"

		it("should be reported by default") {
			val findings = MagicNumber().lint(code)
			assertThat(findings).hasSize(1)
		}

		it("should be ignored when ignoredNumbers contains it verbatim") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORED_NUMBERS to "-2L"))).lint(code)
			assertThat(findings).isEmpty()
		}

		it("should be ignored when ignoredNumbers contains it as floating point") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORED_NUMBERS to "-2f"))).lint(code)
			assertThat(findings).isEmpty()
		}

		it("should not be ignored when ignoredNumbers contains 2 but not -2") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORED_NUMBERS to "1,2,3,-1,0"))).lint(code)
			assertThat(findings).hasSize(1)
		}
	}

	given("a const long of 1") {
		val code = "const val MY_LONG = 1L"

		it("should not be reported by default") {
			val findings = MagicNumber().lint(code)
			assertThat(findings).hasSize(0)
		}

		it("should not be reported when ignoredNumbers is empty") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORED_NUMBERS to ""))).lint(code)
			assertThat(findings).hasSize(0)
		}
	}

	given("a double of 1") {
		val code = "val myDouble = 1.0"

		it("should not be reported by default") {
			val findings = MagicNumber().lint(code)
			assertThat(findings).hasSize(0)
		}

		it("should be reported when ignoredNumbers is empty") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORED_NUMBERS to ""))).lint(code)
			assertThat(findings).hasSize(1)
		}
	}

	given("a const double of 1") {
		val code = "const val MY_DOUBLE = 1.0"

		it("should not be reported by default") {
			val findings = MagicNumber().lint(code)
			assertThat(findings).hasSize(0)
		}

		it("should not be reported when ignoredNumbers is empty") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORED_NUMBERS to ""))).lint(code)
			assertThat(findings).hasSize(0)
		}
	}

	given("a hex of 1") {
		val code = "val myHex = 0x1"

		it("should not be reported by default") {
			val findings = MagicNumber().lint(code)
			assertThat(findings).hasSize(0)
		}

		it("should be reported when ignoredNumbers is empty") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORED_NUMBERS to ""))).lint(code)
			assertThat(findings).hasSize(1)
		}
	}

	given("a const hex of 1") {
		val code = "const val MY_HEX = 0x1"

		it("should not be reported by default") {
			val findings = MagicNumber().lint(code)
			assertThat(findings).hasSize(0)
		}

		it("should not be reported when ignoredNumbers is empty") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORED_NUMBERS to ""))).lint(code)
			assertThat(findings).hasSize(0)
		}
	}

	given("an integer of 300") {
		val code = "val myInt = 300"

		it("should not be reported when ignoredNumbers contains 300") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORED_NUMBERS to "300"))).lint(code)
			assertThat(findings).hasSize(0)
		}

		it("should not be reported when ignoredNumbers contains a floating point 300") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORED_NUMBERS to "300.0"))).lint(code)
			assertThat(findings).hasSize(0)
		}
	}

	given("a binary literal") {
		val code = "val myBinary = 0b01001"

		it("should not be reported") {
			val findings = MagicNumber().lint(code)
			assertThat(findings).hasSize(0)
		}
	}

	given("an integer literal with underscores") {
		val code = "val myInt = 100_000"

		it("should be reported by default") {
			val findings = MagicNumber().lint(code)
			assertThat(findings).hasSize(1)
		}

		it("should not be reported when ignored verbatim") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORED_NUMBERS to "100_000"))).lint(code)
			assertThat(findings).isEmpty()
		}

		it("should not be reported when ignored with different underscores") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORED_NUMBERS to "10_00_00"))).lint(code)
			assertThat(findings).isEmpty()
		}

		it("should not be reported when ignored without underscores") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORED_NUMBERS to "100000"))).lint(code)
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
		val code = "val test = 0.5f"

		it("should be reported by default") {
			val findings = MagicNumber().lint(code)
			assertThat(findings).hasSize(1)
		}

		it("should not be reported when ignoredNumbers contains it") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORED_NUMBERS to ".5"))).lint(code)
			assertThat(findings).hasSize(0)
		}
	}

	given("an invalid ignoredNumber") {

		it("throws a NumberFormatException") {
			assertFailsWith(NumberFormatException::class) {
				MagicNumber(TestConfig(mapOf(MagicNumber.IGNORED_NUMBERS to "banana")))
			}
		}
	}

	given("an empty ignoredNumber") {

		it("doesn't throw an exception") {
			MagicNumber(TestConfig(mapOf(MagicNumber.IGNORED_NUMBERS to "")))
		}
	}
})
