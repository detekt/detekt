package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.compileForTest
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * @author Artur Bosch
 */
class EmptyCodeTest {

	val file = compileForTest(Case.Empty.path())

	@Test
	fun findsEmptyCatch() {
		test { EmptyCatchBlock(Config.empty) }
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

	private fun test(block: () -> Rule) {
		val rule = block()
		val findings = rule.lint(file.text)
		assertThat(findings).hasSize(1)
	}

}