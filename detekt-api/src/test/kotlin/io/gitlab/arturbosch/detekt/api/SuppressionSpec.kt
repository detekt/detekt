package io.gitlab.arturbosch.detekt.api

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileContentForTest
import io.gitlab.arturbosch.detekt.test.compileForTest
import io.gitlab.arturbosch.detekt.test.lint
import io.gitlab.arturbosch.detekt.test.yamlConfig
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.lastBlockStatementOrThis
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

internal class SuppressionSpec : Spek({

    describe("different suppression scenarios") {

        it("rule should be suppressed") {
            val ktFile = compileForTest(Case.SuppressedObject.path())
            val rule = TestRule()
            rule.visitFile(ktFile)
            assertThat(rule.expected).isNotNull()
        }

        it("findings are suppressed") {
            val ktFile = compileForTest(Case.SuppressedElements.path())
            val ruleSet = RuleSet("Test", listOf(TestLM(), TestLPL()))
            val findings = ruleSet.accept(ktFile)
            assertThat(findings.size).isZero()
        }

        it("rule should be suppressed by ALL") {
            val ktFile = compileForTest(Case.SuppressedByAllObject.path())
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

    describe("suppression's via rule set id") {

        val code = """
            fun lpl(a: Int, b: Int, c: Int, d: Int, e: Int, f: Int) = Unit
        """.trimIndent()
        val config = yamlConfig("ruleset-suppression.yml").subConfig("complexity")

        it("reports without a suppression") {
            assertThat(TestLPL(config).lint(code)).isNotEmpty()
        }

        it("reports with wrong suppression") {
            assertThat(TestLPL(config).lint("""@Suppress("wrong_name_used")$code""")).isNotEmpty()
        }

        fun assertCodeIsSuppressed(code: String) {
            val findings = TestLPL(config).lint(code)
            assertThat(findings).isEmpty()
        }

        it("suppresses by rule set id") {
            assertCodeIsSuppressed("""@Suppress("complexity")$code""")
        }

        it("suppresses by rule set id and detekt prefix") {
            assertCodeIsSuppressed("""@Suppress("detekt.complexity")$code""")
        }

        it("suppresses by rule id") {
            assertCodeIsSuppressed("""@Suppress("LongParameterList")$code""")
        }

        it("suppresses by combination of rule set and rule id") {
            assertCodeIsSuppressed("""@Suppress("complexity.LongParameterList")$code""")
        }

        it("suppresses by combination of detekt prefix, rule set and rule id") {
            assertCodeIsSuppressed("""@Suppress("detekt:complexity:LongParameterList")$code""")
        }

        context("MultiRule") {
            class ThisMultiRule : MultiRule() {
                override val rules: List<Rule> = listOf(TestLPL(config))
            }

            it("is suppressed by rule id") {
                assertThat(ThisMultiRule().lint("""@Suppress("complexity")$code""")).isEmpty()
            }

            it("is suppressed by rule id") {
                assertThat(ThisMultiRule().lint("""@Suppress("LongParameterList")$code""")).isEmpty()
            }

            it("is suppressed by combination of detekt.ruleSetId.ruleId") {
                assertThat(ThisMultiRule().lint("""@Suppress("detekt.complexity.LongParameterList")$code""")).isEmpty()
            }
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

class TestLPL(config: Config = Config.empty) : Rule(config) {
    override val issue = Issue("LongParameterList", Severity.CodeSmell, "", Debt.TWENTY_MINS)
    override fun visitNamedFunction(function: KtNamedFunction) {
        val size = function.valueParameters.size
        if (size > 5) report(CodeSmell(issue, Entity.from(function), message = ""))
    }
}
