package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class ThrowsCountSpec : SubjectSpek<ThrowsCount>({
	subject { ThrowsCount(Config.empty) }

	given("several methods which throw exceptions") {

		val code = """
			fun f1(x: Int) {
				when (x) {
					1 -> throw IOException()
					2 -> throw IOException()
					3 -> throw IOException()
				}
			}

			fun f2(x: Int) {
				when (x) {
					1 -> throw IOException()
					2 -> throw IOException()
				}
			}

			override fun f3(x: Int) { // does not report overridden function
				when (x) {
					1 -> throw IOException()
					2 -> throw IOException()
					3 -> throw IOException()
				}
			}
		"""

		it("reports violation by default") {
			val findings = subject.lint(code)
			assertThat(findings).hasSize(1)
		}

		it("does not report for configuration max parameter") {
			val config = TestConfig(mapOf(ThrowsCount.MAX to "3"))
			val subject = ThrowsCount(config)
			assertThat(subject.lint(code)).hasSize(0)
		}
	}
})
