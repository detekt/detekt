package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Java6Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

class ForbiddenVoidSpec : Spek({
	given("some Void usage") {
		it("should report all Void type usage") {
			val code = """
				lateinit var c: () -> Void

				fun method(param: Void) {
					val a: Void? = null
					val b: Void = null!!
				}
			"""

			val findings = ForbiddenVoid().lint(code)
			assertThat(findings).hasSize(4)
		}

		it("should not report Void class literal") {
			val code = """
				val clazz = java.lang.Void::class
				val klass = Void::class
			"""

			val findings = ForbiddenVoid().lint(code)
			assertThat(findings).isEmpty()
		}
	}
})
