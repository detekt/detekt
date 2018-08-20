package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Java6Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

class ForbiddenVoidSpec : Spek({
	given("some Void usage") {
		val code = """
			lateinit var c: () -> Void // 1

			fun method(param: Void) { // 2
				val a: Void? = null // 3
				val b: Void = null!! // 4

				val clazz = java.lang.Void::class
				val klass = Void::class
			}
		"""

		it("should report all Void usage except class literals") {
			val findings = ForbiddenVoid().lint(code)
			assertThat(findings).hasSize(4)
		}
	}
})
