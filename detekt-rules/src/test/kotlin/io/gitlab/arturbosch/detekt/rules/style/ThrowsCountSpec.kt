package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ThrowsCountSpec : Spek({

    describe("ThrowsCount rule") {

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

        context("default config") {
            val subject = ThrowsCount(Config.empty)

            it("reports violation by default") {
                assertThat(subject.lint(code)).hasSize(1)
            }
        }

        context("max count == 3") {
            val config = TestConfig(mapOf(ThrowsCount.MAX to "3"))
            val subject = ThrowsCount(config)

            it("does not report for configuration max parameter") {
                assertThat(subject.lint(code)).hasSize(0)
            }
        }
    }
})
