package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.test.RuleTest
import io.gitlab.arturbosch.detekt.test.format
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * @author Artur Bosch
 */
internal class ExpressionBodySyntaxTest : RuleTest {

	override val rule: Rule = ExpressionBodySyntax()

	@Test
	fun transformEasyReturnBlock() {
		assertThat(rule.format("""
fun stuff(): Int {
	return 5
}
"""
		)).isEqualTo("fun stuff(): Int = 5")
	}

	@Test
	fun transformComplexReturnBlock() {
		assertThat(rule.format("""
fun stuff(): Int {
	return moreStuff().getStuff().stuffStuff()
}
"""
		)).isEqualTo("fun stuff(): Int = moreStuff().getStuff().stuffStuff()")
	}

	@Test
	fun transformMultiLineReturnBlock() {
		val content = """
fun stuff(): Int {
	return moreStuff()
	.getStuff()
	.stuffStuff()
}
"""
		assertThat(rule.format(content))
				.isEqualTo("fun stuff(): Int = moreStuff()\n\t.getStuff()\n\t.stuffStuff()")
	}

	@Test
	fun removeTrailingReturnStatements() {
		val content = """
fun stuff(): Int {
    return if (true) return 5 else return 3
}
fun stuff(): Int {
    return try { return 5 } catch (e: Exception) { return 3 }
}
"""
		assertThat(rule.format(content))
				.isEqualTo("fun stuff(): Int = if (true) 5 else 3" +
						"\nfun stuff(): Int = try { 5 } catch (e: Exception) { 3 }")
	}

}