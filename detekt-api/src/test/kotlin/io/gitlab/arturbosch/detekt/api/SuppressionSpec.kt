package io.gitlab.arturbosch.detekt.api

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.lastBlockStatementOrThis
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

/**
 * @author Artur Bosch
 */
internal class SuppressionSpec : Spek({

	describe("different suppression scenarios") {

		it("rule should be suppressed") {
			val ktFile = compilerFor("SuppressedObject.kt")
			val rule = TestRule()
			rule.visitFile(ktFile)
			assertThat(rule.expected).isNotNull()
		}

		it("findings are suppressed") {
			val ktFile = compilerFor("SuppressedElements.kt")
			val ruleSet = RuleSet("Test", listOf(TestLM(), TestLPL()))
			val findings = ruleSet.accept(ktFile)
			assertThat(findings.size).isZero()
		}

		it("rule should be suppressed by ALL") {
			val ktFile = compilerFor("SuppressedByAllObject.kt")
			val rule = TestRule()
			rule.visitFile(ktFile)
			assertThat(rule.expected).isNotNull()
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
			assertThat(rule.expected).isNotNull()
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
			assertThat(rule.expected).isNotNull()
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
			assertThat(rule.expected).isNotNull()
		}
	}

	describe("suppression based on aliases from config property") {

		it("allows to declare") {
			val ktFile = compileContentForTest("""
			@file:Suppress("detekt:MyTest")
			object SuppressedWithDetektPrefixAndCustomConfigBasedPrefix {

				fun stuff() {
					println("FAILED TEST")
				}
			}
			""")
			val rule = TestRule(TestConfig(mutableMapOf("aliases" to "[MyTest]")))
			rule.visitFile(ktFile)
			assertThat(rule.expected).isNotNull()
		}
	}
})

class TestRule(config: Config = Config.empty) : Rule(config) {
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
