package io.gitlab.arturbosch.detekt.api

import io.gitlab.arturbosch.detekt.test.compileContentForTest
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
internal class SuppressionSpec: Spek({

	it("rule should be suppressed") {
		val ktFile = compilerFor("SuppressedObject.kt")
		val rule = TestRule()
		rule.visitFile(ktFile)
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
		rule.visitFile(ktFile)
		assertNotNull(rule.expected)
	}

	it("rule should be suppressed by detekt prefix in uppercase with dot separator") {
		val ktFile = compileContentForTest("""
			@file:Suppress("Detekt.ALL")
			object SuppressedWithDetektPrefix {

				fun stuff() {
					println("FAILED TEST")
				}
			}
			""")
		val rule = TestRule()
		rule.visitFile(ktFile)
		assertNotNull(rule.expected)
	}

	it("rule should be suppressed by detekt prefix in lowercase with colon separator") {
		val ktFile = compileContentForTest("""
			@file:Suppress("detekt:ALL")
			object SuppressedWithDetektPrefix {

				fun stuff() {
					println("FAILED TEST")
				}
			}
			""")
		val rule = TestRule()
		rule.visitFile(ktFile)
		assertNotNull(rule.expected)
	}

	it("rule should be suppressed by detekt prefix in all caps with colon separator") {
		val ktFile = compileContentForTest("""
			@file:Suppress("DETEKT:ALL")
			object SuppressedWithDetektPrefix {

				fun stuff() {
					println("FAILED TEST")
				}
			}
			""")
		val rule = TestRule()
		rule.visitFile(ktFile)
		assertNotNull(rule.expected)
	}

})

class TestRule : Rule() {
	override val issue = Issue("Test", Severity.CodeSmell, "", Debt.TWENTY_MINS)
	var expected: String? = "Test"
	override fun visitClassOrObject(classOrObject: KtClassOrObject) {
		expected = null
	}
}

class TestLM : Rule() {
	override val issue = Issue("LongMethod", Severity.CodeSmell, "", Debt.TWENTY_MINS)
	override fun visitNamedFunction(function: KtNamedFunction) {
		val start = Location.startLineAndColumn(function.funKeyword!!).line
		val end = Location.startLineAndColumn(function.lastBlockStatementOrThis()).line
		val offset = end - start
		if (offset > 10) report(CodeSmell(issue, Entity.from(function), message = ""))
	}
}

class TestLPL : Rule() {
	override val issue = Issue("LongParameterList", Severity.CodeSmell, "", Debt.TWENTY_MINS)
	override fun visitNamedFunction(function: KtNamedFunction) {
		val size = function.valueParameters.size
		if (size > 5) report(CodeSmell(issue, Entity.from(function), message = ""))
	}
}
