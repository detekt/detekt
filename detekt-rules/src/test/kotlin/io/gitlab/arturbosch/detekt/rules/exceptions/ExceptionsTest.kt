package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.compileForTest
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * @author Artur Bosch
 */
class ExceptionsTest {

	val file = compileForTest(Case.Exceptions.path())

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
	fun findArrayIndexOutOfBoundsException() {
		findOne { CatchArrayIndexOutOfBoundsException() }
	}

	@Test
	fun findIndexOutOfBoundsException() {
		findOne { CatchIndexOutOfBoundsException() }
	}

	@Test
	fun findIllegalMonitorStateException() {
		findOne { CatchIllegalMonitorStateException() }
	}

	@Test
	fun findNPE() {
		findOne { CatchNullPointerException() }
	}

	@Test
	fun findRuntimeException() {
		findOne { CatchRuntimeException() }
	}

	@Test
	fun findThrowable() {
		findOne { CatchThrowable() }
	}

	private fun findOne(block: () -> Rule) {
		val rule = block()
		rule.lint(file.text)
		assertThat(rule.findings).hasSize(1)
	}

}
