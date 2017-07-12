package io.gitlab.arturbosch.detekt.api

import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.lastBlockStatementOrThis
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * @author Artur Bosch
 */
internal class SuppressionTest : Spek({

	it("rule should be suppressed") {
		val ktFile = compilerFor("SuppressedObject.kt")
		val rule = TestRule()
		rule.visit(ktFile)
		assertNotNull(rule.expected)
	}

	it("findings are suppressed") {
		val ktFile = compilerFor("SuppressedElements.kt")
		val ruleSet = RuleSet("Test", listOf(TestLM(), TestLPL()))
		val findings = ruleSet.accept(ktFile)
		assertEquals(0, findings.size)
	}

	it("rule should be suppressed by ALL") {
		val ktFile = compilerFor("SuppressedByAllObject.kt")
		val rule = TestRule()
		rule.visit(ktFile)
		assertNotNull(rule.expected)
	}

})

class TestRule : Rule() {
	override val issue = Issue("Test", Severity.CodeSmell, "")
	var expected: String? = "Test"
	override fun visitClassOrObject(classOrObject: KtClassOrObject) {
		expected = null
	}
}

class TestLM : Rule() {
	override val issue = Issue("LongMethod", Severity.CodeSmell, "")
	override fun visitNamedFunction(function: KtNamedFunction) {
		val start = Location.startLineAndColumn(function.funKeyword!!).line
		val end = Location.startLineAndColumn(function.lastBlockStatementOrThis()).line
		val offset = end - start
		if (offset > 10) report(CodeSmell(issue, Entity.from(function)))
	}
}

class TestLPL : Rule() {
	override val issue = Issue("LongParameterList", Severity.CodeSmell, "")
	override fun visitNamedFunction(function: KtNamedFunction) {
		val size = function.valueParameters.size
		if (size > 5) report(CodeSmell(issue, Entity.from(function)))
	}
}
