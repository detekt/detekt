package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.rules.load
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * @author Artur Bosch
 */
class ExceptionsSpec {

	val root = load(Case.Exceptions)

	@Test
	fun findThrowError() {
		findOne { ThrowError() }
	}

	@Test
	fun findThrowRuntimeException() {
		findOne { ThrowRuntimeException() }
	}

	@Test
	fun findThrowNullPointerException() {
		findOne { ThrowNullPointerException() }
	}

	@Test
	fun findThrowException() {
		findOne { ThrowException() }
	}

	@Test
	fun findThrowThrowable() {
		findOne { ThrowThrowable() }
	}

	@Test
	fun findArrayIndexOOBE() {
		findOne { CatchArrayIndexOutOfBoundsException() }
	}

	@Test
	fun findError() {
		findOne { CatchError() }
	}

	@Test
	fun findException() {
		findOne { CatchException() }
	}

	@Test
	fun findIndexOOBE() {
		findOne { CatchArrayIndexOutOfBoundsException() }
	}

	@Test
	fun findNPE() {
		findOne { CatchNullPointerException() }
	}

	@Test
	fun findRuntimeException() {
		findOne { CatchRuntimeException() }
	}

	private fun findOne(block: () -> Rule) {
		val rule = block()
		rule.visit(root)
		assertThat(rule.findings).hasSize(1)
	}


}