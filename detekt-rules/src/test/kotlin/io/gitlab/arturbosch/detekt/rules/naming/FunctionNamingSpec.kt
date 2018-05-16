package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

/**
 * @author Artur Bosch
 */
class FunctionNamingSpec : SubjectSpek<FunctionNaming>({

	subject { FunctionNaming() }

	it("allows FunctionName as alias for suppressing") {
		val code = """
			@Suppress("FunctionName")
			fun MY_FUN() = TODO()
		"""
		assertThat(subject.lint(code)).isEmpty()
	}
})
