package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileForTest
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.regex.PatternSyntaxException
import kotlin.test.assertFailsWith

/**
 * @author Artur Bosch
 */
class EmptyCodeTest {

	val file = compileForTest(Case.Empty.path())

	private val regexTestingCode = """
			fun f() {
				try {
				} catch (foo: MyException) {
				}
			}"""

	@Test
	fun findsEmptyCatch() {
		test { EmptyCatchBlock(Config.empty) }
	}

	@Test
	fun findsEmptyNestedCatch() {
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

	@Test
	fun doesNotReportIgnoredOrExpectedException() {
		val code = """
			fun f() {
				try {
                } catch (ignore: IOException) {
				} catch (expected: Exception) {
				}
			}"""
		assertThat(EmptyCatchBlock(Config.empty).lint(code)).isEmpty()
	}

	@Test
	fun doesNotReportEmptyCatchWithConfig() {
		val code = """
			fun f() {
				try {
				} catch (foo: MyException) {
				}
			}"""
		val config = TestConfig(mapOf(EmptyCatchBlock.ALLOWED_EXCEPTION_NAME_REGEX to "foo"))
		assertThat(EmptyCatchBlock(config).lint(code)).isEmpty()
	}

	@Test
	fun findsEmptyFinally() {
		test { EmptyFinallyBlock(Config.empty) }
	}

	@Test
	fun findsEmptyIf() {
		test { EmptyIfBlock(Config.empty) }
	}

	@Test
	fun findsEmptyElse() {
		test { EmptyElseBlock(Config.empty) }
	}

	@Test
	fun findsEmptyFor() {
		test { EmptyForBlock(Config.empty) }
	}

	@Test
	fun findsEmptyWhile() {
		test { EmptyWhileBlock(Config.empty) }
	}

	@Test
	fun findsEmptyDoWhile() {
		test { EmptyDoWhileBlock(Config.empty) }
	}

	@Test
	fun findsEmptyFun() {
		test { EmptyFunctionBlock(Config.empty) }
	}

	@Test
	fun findsEmptyClass() {
		test { EmptyClassBlock(Config.empty) }
	}

	@Test
	fun findsEmptyWhen() {
		test { EmptyWhenBlock(Config.empty) }
	}

	@Test
	fun findsEmptyInit() {
		test { EmptyInitBlock(Config.empty) }
	}

	@Test
	fun findsOneEmptySecondaryConstructor() {
		test { EmptySecondaryConstructor(Config.empty) }
	}

	@Test
	fun findsEmptyDefaultConstructor() {
		val rule = EmptyDefaultConstructor(Config.empty)
		val text = compileForTest(Case.EmptyDefaultConstructorPositive.path()).text
		assertThat(rule.lint(text)).hasSize(2)
	}

	@Test
	fun doesNotFindEmptyDefaultConstructor() {
		val rule = EmptyDefaultConstructor(Config.empty)
		val text = compileForTest(Case.EmptyDefaultConstructorNegative.path()).text
		assertThat(rule.lint(text)).isEmpty()
	}

	@Test
	fun doesNotFailWithInvalidRegexWhenDisabled() {
		val configValues = mapOf("active" to "false",
				EmptyCatchBlock.ALLOWED_EXCEPTION_NAME_REGEX to "*foo")
		val config = TestConfig(configValues)
		assertThat(EmptyCatchBlock(config).lint(regexTestingCode)).isEmpty()
	}

	@Test
	fun doesFailWithInvalidRegex() {
		val configValues = mapOf(EmptyCatchBlock.ALLOWED_EXCEPTION_NAME_REGEX to "*foo")
		val config = TestConfig(configValues)
		assertFailsWith<PatternSyntaxException> {
			EmptyCatchBlock(config).lint(regexTestingCode)
		}
	}

	private fun test(block: () -> Rule) {
		val rule = block()
		rule.lint(file)
		assertThat(rule.findings).hasSize(1)
	}
}
