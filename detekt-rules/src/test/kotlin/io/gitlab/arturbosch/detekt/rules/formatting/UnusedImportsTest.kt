package io.gitlab.arturbosch.detekt.rules.formatting

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasSize
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.rules.format
import io.gitlab.arturbosch.detekt.rules.lint
import org.junit.jupiter.api.Test

/**
 * @author Shyiko
 */
class UnusedImportsTest : RuleTest {

	override val rule: Rule = UnusedImports(Config.empty)

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
		), hasSize(equalTo(4)))
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
		), equalTo(
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
		))
	}
}