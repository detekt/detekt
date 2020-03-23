package io.gitlab.arturbosch.detekt.generator.collection

import io.gitlab.arturbosch.detekt.generator.collection.exception.InvalidAliasesDeclaration
import io.gitlab.arturbosch.detekt.generator.collection.exception.InvalidCodeExampleDocumentationException
import io.gitlab.arturbosch.detekt.generator.collection.exception.InvalidDocumentationException
import io.gitlab.arturbosch.detekt.generator.collection.exception.InvalidIssueDeclaration
import io.gitlab.arturbosch.detekt.generator.util.run
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class RuleCollectorSpec : Spek({

    val subject by memoized { RuleCollector() }

    describe("a RuleCollector") {

        it("collects no rules when no class is extended") {
            val code = "class SomeRandomClass"
            val items = subject.run(code)
            assertThat(items).isEmpty()
        }

        it("collects no rules when no rule class is extended") {
            val code = "class SomeRandomClass : SomeOtherClass"
            val items = subject.run(code)
            assertThat(items).isEmpty()
        }

        it("throws when a class extends Rule but has no valid documentation") {
            val rules = listOf("Rule", "FormattingRule", "ThresholdRule", "EmptyRule")
            for (rule in rules) {
                val code = "class SomeRandomClass : $rule"
                assertThatExceptionOfType(InvalidDocumentationException::class.java).isThrownBy { subject.run(code) }
            }
        }

        it("collects the rule name") {
            val name = "SomeRandomClass"
            val code = """
                /**
                 * description
                 */
                class $name : Rule
            """
            val items = subject.run(code)
            assertThat(items[0].name).isEqualTo(name)
        }

        it("collects the rule description") {
            val description = "description"
            val code = """
                /**
                 * $description
                 */
                class SomeRandomClass : Rule
            """
            val items = subject.run(code)
            assertThat(items[0].description).isEqualTo(description)
        }

        it("has a multi paragraph description") {
            val description = "description"
            val code = """
                /**
                 * $description
                 *
                 * more...
                 */
                class SomeRandomClass : Rule
            """
            val items = subject.run(code)
            assertThat(items[0].description).startsWith(description)
            assertThat(items[0].description).contains("more...")
        }

        it("is not active") {
            val code = """
                /**
                 * description
                 */
                class SomeRandomClass : Rule
            """
            val items = subject.run(code)
            assertThat(items[0].active).isFalse()
        }

        it("is active tag present") {
            val code = """
                /**
                 * description
                 * @active
                 */
                class SomeRandomClass : Rule
            """
            val items = subject.run(code)
            assertThat(items[0].active).isTrue()
        }

        it("is auto-correctable tag is present") {
            val code = """
                /**
                 * description
                 * @autoCorrect
                 */
                class SomeRandomClass : Rule
            """
            val items = subject.run(code)
            assertThat(items[0].autoCorrect).isTrue()
        }

        it("is active if the tag is there and has a description") {
            val code = """
                /**
                 * description
                 * @active description about the active tag
                 */
                class SomeRandomClass : Rule
            """
            val items = subject.run(code)
            assertThat(items[0].active).isTrue()
        }

        it("collects the issue property") {
            val code = """
                /**
                 * description
                 */
                class SomeRandomClass : Rule {
                    override val defaultRuleIdAliases = setOf("RULE", "RULE2")
                    override val issue = Issue(javaClass.simpleName, Severity.Style, "", Debt.TEN_MINS)
                }
            """
            val items = subject.run(code)
            assertThat(items[0].severity).isEqualTo("Style")
            assertThat(items[0].debt).isEqualTo("10min")
            assertThat(items[0].aliases).isEqualTo("RULE, RULE2")
        }

        it("contains no configuration options by default") {
            val code = """
                /**
                 * description
                 */
                class SomeRandomClass : Rule
            """
            val items = subject.run(code)
            assertThat(items[0].configuration).isEmpty()
        }

        it("contains one configuration option with correct formatting") {
            val code = """
                /**
                 * description
                 * @configuration config - description (default: `'[A-Z$]'`)
                 */
                class SomeRandomClass : Rule
            """
            val items = subject.run(code)
            assertThat(items[0].configuration).hasSize(1)
            assertThat(items[0].configuration[0].name).isEqualTo("config")
            assertThat(items[0].configuration[0].description).isEqualTo("description")
            assertThat(items[0].configuration[0].defaultValue).isEqualTo("'[A-Z$]'")
        }

        it("contains multiple configuration options") {
            val code = """
                /**
                 * description
                 * @configuration config - description (default: `''`)
                 * @configuration config2 - description2 (default: `''`)
                 */
                class SomeRandomClass: Rule
            """
            val items = subject.run(code)
            assertThat(items[0].configuration).hasSize(2)
        }

        it("config option doesn't have a default value") {
            val code = """
                /**
                 * description
                 * @configuration config - description
                 */
                class SomeRandomClass : Rule
            """
            assertThatExceptionOfType(InvalidDocumentationException::class.java).isThrownBy { subject.run(code) }
        }

        it("has a blank default value") {
            val code = """
                /**
                 * description
                 * @configuration config - description (default: ``)
                 */
                class SomeRandomClass : Rule
            """
            assertThatExceptionOfType(InvalidDocumentationException::class.java).isThrownBy { subject.run(code) }
        }

        it("has an incorrectly delimited default value") {
            val code = """
                /**
                 * description
                 * @configuration config - description (default: true)
                 */
                class SomeRandomClass : Rule
            """
            assertThatExceptionOfType(InvalidDocumentationException::class.java).isThrownBy { subject.run(code) }
        }

        it("contains a misconfigured configuration option") {
            val code = """
                /**
                 * description
                 * @configuration something: description
                 */
                class SomeRandomClass : Rule
            """
            assertThatExceptionOfType(InvalidDocumentationException::class.java).isThrownBy { subject.run(code) }
        }

        it("contains compliant and noncompliant code examples") {
            val code = """
                /**
                 * description
                 *
                 * <noncompliant>
                 * val one = 2
                 * </noncompliant>
                 *
                 * <compliant>
                 * val one = 1
                 * </compliant>
                 */
                class RandomClass : Rule
            """
            val items = subject.run(code)
            assertThat(items[0].nonCompliantCodeExample).isEqualTo("val one = 2")
            assertThat(items[0].compliantCodeExample).isEqualTo("val one = 1")
        }

        it("has wrong noncompliant code example declaration") {
            val code = """
                /**
                 * description
                 *
                 * <noncompliant>
                 */
                class RandomClass : Rule
            """
            assertThatExceptionOfType(InvalidCodeExampleDocumentationException::class.java)
                .isThrownBy { subject.run(code) }
        }

        it("has wrong compliant code example declaration") {
            val code = """
                /**
                 * description
                 *
                 * <noncompliant>
                 * val one = 2
                 * </noncompliant>
                 * <compliant>
                 */
                class RandomClass : Rule
            """
            assertThatExceptionOfType(InvalidCodeExampleDocumentationException::class.java)
                .isThrownBy { subject.run(code) }
        }

        it("has wrong compliant without noncompliant code example declaration") {
            val code = """
                /**
                 * description
                 *
                 * <compliant>
                 * val one = 1
                 * </compliant>
                 */
                class RandomClass : Rule
            """
            assertThatExceptionOfType(InvalidCodeExampleDocumentationException::class.java)
                .isThrownBy { subject.run(code) }
        }

        it("has wrong issue style property") {
            val code = """
                /**
                 * description
                 */
                class SomeRandomClass : Rule {

                    val style = Severity.Style
                    override val issue = Issue(javaClass.simpleName,
                            style,
                            "",
                            debt = Debt.TEN_MINS)
                }
            """
            assertThatExceptionOfType(InvalidIssueDeclaration::class.java).isThrownBy { subject.run(code) }
        }

        it("has wrong aliases property structure") {
            val code = """
                /**
                 * description
                 */
                class SomeRandomClass : Rule {

                    val a = setOf("UNUSED_VARIABLE")
                    override val defaultRuleIdAliases = a
                    override val issue = Issue(javaClass.simpleName,
                            Severity.Style,
                            "",
                            debt = Debt.TEN_MINS)
                }
            """
            assertThatExceptionOfType(InvalidAliasesDeclaration::class.java).isThrownBy { subject.run(code) }
        }

        it("contains tabs in KDoc") {
            val description = "\tdescription"
            val code = """
                /**
                 * $description
                 */
                class SomeRandomClass : Rule
            """
            assertThatExceptionOfType(InvalidDocumentationException::class.java).isThrownBy { subject.run(code) }
        }
    }
})
