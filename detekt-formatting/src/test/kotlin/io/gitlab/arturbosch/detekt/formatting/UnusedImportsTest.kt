package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.test.RuleTest
import io.gitlab.arturbosch.detekt.test.format
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * @author Shyiko
 */
class UnusedImportsTest : RuleTest {

	override val rule: Rule = UnusedImports(Config.Companion.empty)

	@Test
	fun infixOperators() {
		assertThat(rule.lint(
				"""
            import tasks.success

            fun main() {
				task {
				} success {
				}
            }
            """
		)).hasSize(0)
	}

	@Test
	fun testLint() {
		assertThat(rule.lint(
				"""
            import p.a
            import p.B6
            import java.nio.file.Paths
            import p.B as B12
            import p2.B
            import p.C
            import p.a.*
            import escaped.`when`
            import escaped.`foo`

            fun main() {
                println(a())
                C.call(B())
                `when`()
            }
            """
		)).hasSize(4)
	}

	@Test
	fun testFormat() {
		assertThat(rule.format(
				"""
            import p.a
            import p.B6
            import p.B as B12
            import p2.B as B2
            import p.C
            import escaped.`when`
            import escaped.`foo`

            fun main() {
                println(a())
                C.call()
                fn(B2.NAME)
                `when`()
            }
            """
		)).isEqualTo(
				"""
            import p.a
            import p2.B as B2
            import p.C
            import escaped.`when`

            fun main() {
                println(a())
                C.call()
                fn(B2.NAME)
                `when`()
            }
            """.trimIndent()
		)
	}
}