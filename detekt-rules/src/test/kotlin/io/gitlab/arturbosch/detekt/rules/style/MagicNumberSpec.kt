package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Java6Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

class MagicNumberSpec : Spek({

	given("a float of 1") {
		val code = "val myFloat = 1.0f"

		it("it should not be reported by default") {
			val findings = MagicNumber().lint(code)
			assertThat(findings).hasSize(0)
		}

		it("it should be reported when ignoreNumbers are overridden") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_NUMBERS to ""))).lint(code)
			assertThat(findings).hasSize(1)
		}
	}

	given("a const float of 1") {
		val code = "const val MY_FLOAT = 1.0f"

		it("it should not be reported by default") {
			val findings = MagicNumber().lint(code)
			assertThat(findings).hasSize(0)
		}

		it("it should not be reported when ignoreNumbers are overridden") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_NUMBERS to ""))).lint(code)
			assertThat(findings).hasSize(0)
		}
	}

	given("an integer of 1") {
		val code = "val myInt = 1"

		it("it should not be reported by default") {
			val findings = MagicNumber().lint(code)
			assertThat(findings).hasSize(0)
		}

		it("it should be reported when ignoreNumbers are overridden") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_NUMBERS to ""))).lint(code)
			assertThat(findings).hasSize(1)
		}
	}

	given("a const integer of 1") {
		val code = "const val MY_INT = 1"

		it("it should not be reported by default") {
			val findings = MagicNumber().lint(code)
			assertThat(findings).hasSize(0)
		}

		it("it should not be reported when ignoreNumbers are overridden") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_NUMBERS to ""))).lint(code)
			assertThat(findings).hasSize(0)
		}
	}

	given("a long of 1") {
		val code = "val myLong = 1L"

		it("it should not be reported by default") {
			val findings = MagicNumber().lint(code)
			assertThat(findings).hasSize(0)
		}

		it("it should be reported when ignoreNumbers are overridden") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_NUMBERS to ""))).lint(code)
			assertThat(findings).hasSize(1)
		}
	}

	given("a const long of 1") {
		val code = "const val MY_LONG = 1L"

		it("it should not be reported by default") {
			val findings = MagicNumber().lint(code)
			assertThat(findings).hasSize(0)
		}

		it("it should not be reported when ignoreNumbers are overridden") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_NUMBERS to ""))).lint(code)
			assertThat(findings).hasSize(0)
		}
	}

	given("a double of 1") {
		val code = "val myDouble = 1.0"

		it("it should not be reported by default") {
			val findings = MagicNumber().lint(code)
			assertThat(findings).hasSize(0)
		}

		it("it should be reported when ignoreNumbers are overridden") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_NUMBERS to ""))).lint(code)
			assertThat(findings).hasSize(1)
		}
	}

	given("a const double of 1") {
		val code = "const val MY_DOUBLE = 1.0"

		it("it should not be reported by default") {
			val findings = MagicNumber().lint(code)
			assertThat(findings).hasSize(0)
		}

		it("it should not be reported when ignoreNumbers are overridden") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_NUMBERS to ""))).lint(code)
			assertThat(findings).hasSize(0)
		}
	}

	given("a hex of 1") {
		val code = "val myHex = 0x0"

		it("it should not be reported by default") {
			val findings = MagicNumber().lint(code)
			assertThat(findings).hasSize(0)
		}

		it("it should be reported when ignoreNumbers are overridden") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_NUMBERS to ""))).lint(code)
			assertThat(findings).hasSize(1)
		}
	}

	given("a const hex of 1") {
		val code = "const val MY_HEX = 0x0"

		it("it should not be reported by default") {
			val findings = MagicNumber().lint(code)
			assertThat(findings).hasSize(0)
		}

		it("it should not be reported when ignoreNumbers are overridden") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_NUMBERS to ""))).lint(code)
			assertThat(findings).hasSize(0)
		}
	}

	given("an integer of 300") {
		val code = "val myInt = 300"

		it("it should not be reported when ignoreNumbers contains 300") {
			val findings = MagicNumber(TestConfig(mapOf(MagicNumber.IGNORE_NUMBERS to "300"))).lint(code)
			assertThat(findings).hasSize(0)
		}
	}

	given("an if statement with magic numbers") {
		val code = "val myInt = if (5 < 6) 7 else 8"

		it("it should be reported") {
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

		it("it should be reported") {
			val findings = MagicNumber().lint(code)
			assertThat(findings).hasSize(6)
		}
	}
})
