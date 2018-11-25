package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileForTest
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import java.util.regex.PatternSyntaxException

/**
 * @author Artur Bosch
 */
class EmptyCodeSpec : Spek({

	val regexTestingCode = """
			fun f() {
				try {
				} catch (foo: MyException) {
				}
			}"""

	it("findsEmptyCatch") {
		test { EmptyCatchBlock(Config.empty) }
	}

	it("findsEmptyNestedCatch") {
		val code = """
			fun f() {
				try {
                } catch (ignore: IOException) {
					try {
					} catch (e: IOException) {
					}
				}
			}"""
		assertThat(EmptyCatchBlock(Config.empty).lint(code)).hasSize(1)
	}

	it("doesNotReportIgnoredOrExpectedException") {
		val code = """
			fun f() {
				try {
                } catch (ignore: IOException) {
				} catch (expected: Exception) {
				}
			}"""
		assertThat(EmptyCatchBlock(Config.empty).lint(code)).isEmpty()
	}

	it("doesNotReportEmptyCatchWithConfig") {
		val code = """
			fun f() {
				try {
				} catch (foo: MyException) {
				}
			}"""
		val config = TestConfig(mapOf(EmptyCatchBlock.ALLOWED_EXCEPTION_NAME_REGEX to "foo"))
		assertThat(EmptyCatchBlock(config).lint(code)).isEmpty()
	}

	it("findsEmptyFinally") {
		test { EmptyFinallyBlock(Config.empty) }
	}

	it("findsEmptyIf") {
		test { EmptyIfBlock(Config.empty) }
	}

	it("findsEmptyElse") {
		test { EmptyElseBlock(Config.empty) }
	}

	it("findsEmptyFor") {
		test { EmptyForBlock(Config.empty) }
	}

	it("findsEmptyWhile") {
		test { EmptyWhileBlock(Config.empty) }
	}

	it("findsEmptyDoWhile") {
		test { EmptyDoWhileBlock(Config.empty) }
	}

	it("findsEmptyFun") {
		test { EmptyFunctionBlock(Config.empty) }
	}

	it("findsEmptyClass") {
		test { EmptyClassBlock(Config.empty) }
	}

	it("findsEmptyWhen") {
		test { EmptyWhenBlock(Config.empty) }
	}

	it("findsEmptyInit") {
		test { EmptyInitBlock(Config.empty) }
	}

	it("findsOneEmptySecondaryConstructor") {
		test { EmptySecondaryConstructor(Config.empty) }
	}

	it("findsEmptyDefaultConstructor") {
		val rule = EmptyDefaultConstructor(Config.empty)
		val text = compileForTest(Case.EmptyDefaultConstructorPositive.path()).text
		assertThat(rule.lint(text)).hasSize(2)
	}

	it("doesNotFindEmptyDefaultConstructor") {
		val rule = EmptyDefaultConstructor(Config.empty)
		val text = compileForTest(Case.EmptyDefaultConstructorNegative.path()).text
		assertThat(rule.lint(text)).isEmpty()
	}

	it("doesNotFailWithInvalidRegexWhenDisabled") {
		val configValues = mapOf("active" to "false",
				EmptyCatchBlock.ALLOWED_EXCEPTION_NAME_REGEX to "*foo")
		val config = TestConfig(configValues)
		assertThat(EmptyCatchBlock(config).lint(regexTestingCode)).isEmpty()
	}

	it("doesFailWithInvalidRegex") {
		val configValues = mapOf(EmptyCatchBlock.ALLOWED_EXCEPTION_NAME_REGEX to "*foo")
		val config = TestConfig(configValues)
		assertThatExceptionOfType(PatternSyntaxException::class.java).isThrownBy {
			EmptyCatchBlock(config).lint(regexTestingCode)
		}
	}
})

private fun test(block: () -> Rule) {
	val rule = block()
	rule.lint(compileForTest(Case.Empty.path()))
	assertThat(rule.findings).hasSize(1)
}
