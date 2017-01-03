package io.gitlab.arturbosch.detekt.formatting

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasSize
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.test.RuleTest
import io.gitlab.arturbosch.detekt.test.format
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Test

/**
 * @author Shyiko
 */
class SpacingAfterKeywordTest : RuleTest {

	override val rule: Rule = SpacingAfterKeyword(Config.Companion.empty)

	@Test
	fun testLint() {
		assertThat(rule.lint(
				"""
            fun main() {
                if(true) {}
                while(true) {}
                do {} while(true)
            }
            """
		), hasSize(equalTo(3)))
	}

	@Test
	fun testFormat() {
		assertThat(rule.format(
				"""
            fun main() {
                if(true) {}
                if (true) {}
                while(true) {}
                do {} while(true)
                try{}catch(){}
            }
            """
		), equalTo(
				"""
            fun main() {
                if (true) {}
                if (true) {}
                while (true) {}
                do {} while (true)
                try {} catch () {}
            }
            """.trimIndent()
		))
	}
}