package io.gitlab.arturbosch.detekt.rules.empty

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasSize
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.rules.load
import org.junit.Test

/**
 * @author Artur Bosch
 */
class EmptyCodeSpec {

	val root = load(Case.Empty)

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

	private fun test(block: () -> Rule) {
		val rule = block()
		rule.visit(root)
		assertThat(rule.findings, hasSize(equalTo(1)))
	}


}