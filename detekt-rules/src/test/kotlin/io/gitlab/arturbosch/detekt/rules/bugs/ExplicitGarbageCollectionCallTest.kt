package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class ExplicitGarbageCollectionCallSpec : SubjectSpek<ExplicitGarbageCollectionCall>({
	subject { ExplicitGarbageCollectionCall(Config.empty) }

	given("several garbage collector calls") {

		it("reports garbage collector calls") {
			val code = """
				fun f() {
					System.gc()
					Runtime.getRuntime().gc()
					System.runFinalization()
				}"""
			assertThat(subject.lint(code)).hasSize(3)
		}
	}
})
