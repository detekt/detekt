package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.test.RuleTest
import io.gitlab.arturbosch.detekt.test.format
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

/**
 * @author Artur Bosch
 */
internal class SingleReturnExpressionSyntaxTest : RuleTest {

	override val rule: Rule = SingleReturnExpressionSyntax()

	@Test
	fun transformEasyReturnBlock() {
		Assertions.assertThat(rule.format("""
fun stuff(): Int {
	return 5
}
"""
		)).isEqualTo("fun stuff() = 5")
	}

	@Test
	fun transformComplexReturnBlock() {
		Assertions.assertThat(rule.format("""
fun stuff(): Int {
	return moreStuff().getStuff().stuffStuff()
}
"""
		)).isEqualTo("fun stuff() = moreStuff().getStuff().stuffStuff()")
	}

	@Test
	fun transformMultiLineReturnBlock() {
		Assertions.assertThat(rule.format("""
fun stuff(): Int {
	return moreStuff()
	.getStuff()
	.stuffStuff()
}
"""
		)).isEqualTo("fun stuff() = moreStuff()\n\t.getStuff()\n\t.stuffStuff()")
	}
}