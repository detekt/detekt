package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.test.RuleTest
import io.gitlab.arturbosch.detekt.test.format
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * Adapted from KtLint.
 *
 * @author Artur Bosch
 */
class SpacingAroundOperatorsTest : RuleTest {

	override val rule: Rule = SpacingAroundOperator(Config.empty)

	@Test
	fun testLint() {
		assertThat(rule.lint(
				"""
            import a.b.*
            fun main() {
                val v = 0 - 1 * 2
                val v1 = 0-1*2
                val v2 = -0 - 1
                val v3 = v * 2
                i++
                val y = +1
                var x = 1 in 3..4
                val b = 1 < 2
                fun(a = true)
                val res = ArrayList<LintError>()
                fn(*arrayOfNulls<Any>(0 * 1))
                fun <T>List<T>.head() {}
                val a= ""
                d *= 1
                call(*v)
                open class A<T> {
                    open fun x() {}
                }
                class B<T> : A<T>() {
                    override fun x() = super<A>.x()
                }
            }
            """
		)).hasSize(3)
	}

	@Test
	fun testFormat() {
		assertThat(rule.format(
				"""
            fun main() {
                val v1 = 0-1*2
                val v2 = -0-1
                val v3 = v*2
                i++
                val y = +1
                var x = 1 in 3..4
            }
            """
		)).isEqualTo(
				"""
            fun main() {
                val v1 = 0 - 1 * 2
                val v2 = -0 - 1
                val v3 = v * 2
                i++
                val y = +1
                var x = 1 in 3..4
            }
            """.trimIndent()
		)
	}
}