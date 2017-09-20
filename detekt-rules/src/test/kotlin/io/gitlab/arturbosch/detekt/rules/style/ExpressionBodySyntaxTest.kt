package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.test.RuleTest
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * @author Artur Bosch
 */
internal class ExpressionBodySyntaxTest : RuleTest {

	override val rule: Rule = ExpressionBodySyntax()

	@Test
	fun returnStmtWithConstant() {
		assertThat(rule.lint("""
			fun stuff(): Int {
				return 5
			}
		"""
		)).hasSize(1)
	}

	@Test
	fun returnStmtWithMethodChain() {
		assertThat(rule.lint("""
			fun stuff(): Int {
				return moreStuff().getStuff().stuffStuff()
			}
		"""
		)).hasSize(1)
	}

	@Test
	fun returnStmtWithMultilineChain() {
		assertThat(rule.lint("""
			fun stuff(): Int {
				return moreStuff()
				.getStuff()
				.stuffStuff()
			}
		""")).hasSize(1)
	}

	@Test
	fun returnStmtWithConditionalStmt() {
		assertThat(rule.lint("""
			fun stuff(): Int {
				return if (true) return 5 else return 3
			}
			fun stuff(): Int {
				return try { return 5 } catch (e: Exception) { return 3 }
			}
		""")).hasSize(2)
	}
}
