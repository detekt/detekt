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
            val code = """
				package foo

				class SomeRandomClass {
				}
			"""
            val items = subject.run(code)
            assertThat(items).isEmpty()
        }

        it("collects no rules when no rule class is extended") {
            val code = """
				package foo

				class SomeRandomClass: SomeOtherClass {
				}
			"""
            val items = subject.run(code)
            assertThat(items).isEmpty()
        }

        it("throws when a class extends Rule but has no valid documentation") {
            val code = """
				package foo

				class SomeRandomClass: Rule {
				}
			"""
            assertThatExceptionOfType(InvalidDocumentationException::class.java)
                    .isThrownBy { subject.run(code) }
        }

        it("throws when a class extends ThresholdRule but has no valid documentation") {
            val code = """
				package foo

				class SomeRandomClass: ThresholdRule {
				}
			"""
            assertThatExceptionOfType(InvalidDocumentationException::class.java)
                    .isThrownBy { subject.run(code) }
        }

        it("throws when a class extends ThresholdRule but has no valid documentation") {
            val code = """
				package foo

				class SomeRandomClass: FormattingRule {
				}
			"""
            assertThatExceptionOfType(InvalidDocumentationException::class.java)
                    .isThrownBy { subject.run(code) }
        }

        it("collects the formatting rule name") {
            val name = "UnusedImport"
            val code = """
				package foo

				/**
				* Wonderful description
				*/
				class $name: FormattingRule {
				}
			"""
            val items = subject.run(code)
            assertThat(items[0].name).isEqualTo(name)
        }

        it("collects the rule name") {
            val name = "SomeRandomClass"
            val description = "some description"
            val code = """
				package foo

				/**
				 * $description
				 */
				class $name: Rule {
				}
			"""
            val items = subject.run(code)
            assertThat(items[0].name).isEqualTo(name)
        }

        it("collects the rule description") {
            val name = "SomeRandomClass"
            val description = "some description"
            val code = """
				package foo

				/**
				 * $description
				 */
				class $name: Rule {
				}
			"""
            val items = subject.run(code)
            assertThat(items[0].description).isEqualTo(description)
        }

        it("has a multi paragraph description") {
            val name = "SomeRandomClass"
            val description = "some description"
            val code = """
				package foo

				/**
				 * $description
				 *
				 * more...
				 */
				class $name: Rule {
				}
			"""
            val items = subject.run(code)
            assertThat(items[0].description).startsWith(description)
            assertThat(items[0].description).contains("more...")
        }

        it("does not include tags in the description") {
            val name = "SomeRandomClass"
            val description = "some description"
            val code = """
				package foo

				/**
				 * $description
				 * @author Marvin Ramin
				 */
				class $name: Rule {
				}
			"""
            val items = subject.run(code)
            assertThat(items[0].description).isEqualTo(description)
        }

        it("is not active") {
            val name = "SomeRandomClass"
            val description = "some description"
            val code = """
				package foo

				/**
				 * $description
				 * @author Marvin Ramin
				 */
				class $name: Rule {
				}
			"""
            val items = subject.run(code)
            assertThat(items[0].active).isFalse()
        }

        it("is active tag is present") {
            val code = """
				package foo

				/**
				 * some description
				 * @active
				 */
				class SomeRandomClass: Rule {
				}
			"""
            val items = subject.run(code)
            assertThat(items[0].active).isTrue()
        }

        it("is auto-correctable tag is present") {
            val code = """
				package foo

				/**
				 * some description
				 * @autoCorrect
				 */
				class SomeRandomClass: Rule {
				}
			"""
            val items = subject.run(code)
            assertThat(items[0].autoCorrect).isTrue()
        }

        it("is active if the tag is there and has a description") {
            val name = "SomeRandomClass"
            val description = "some description"
            val code = """
				package foo

				/**
				 * $description
				 * @active some description about the active tag
				 */
				class $name: Rule {
				}
			"""
            val items = subject.run(code)
            assertThat(items[0].active).isTrue()
        }

        it("collects the issue property") {
            val name = "SomeRandomClass"
            val description = "some description"
            val code = """
				package foo

				/**
				 * $description
				 */
				class $name: Rule {

					override val defaultRuleIdAliases = setOf("RULE", "RULE2")

					override val issue = Issue(javaClass.simpleName,
							Severity.Style,
							"",
							debt = Debt.TEN_MINS)
				}
			"""
            val items = subject.run(code)
            assertThat(items[0].severity).isEqualTo("Style")
            assertThat(items[0].debt).isEqualTo("10min")
            assertThat(items[0].aliases).isEqualTo("RULE, RULE2")
        }

        it("contains no configuration options by default") {
            val name = "SomeRandomClass"
            val description = "some description"
            val code = """
				package foo

				/**
				 * $description
				 */
				class $name: Rule {
				}
			"""
            val items = subject.run(code)
            assertThat(items[0].configuration).isEmpty()
        }

        it("contains one configuration option with correct formatting") {
            val name = "SomeRandomClass"
            val description = "some description"
            val code = """
				package foo

				/**
				 * $description
				 * @configuration config - description (default: '[A-Z$]')
				 */
				class $name: Rule {
				}
			"""
            val items = subject.run(code)
            assertThat(items[0].configuration).hasSize(1)
            assertThat(items[0].configuration[0].name).isEqualTo("config")
            assertThat(items[0].configuration[0].description).isEqualTo("description")
            assertThat(items[0].configuration[0].defaultValue).isEqualTo("'[A-Z$]'")
        }

        it("contains multiple configuration options") {
            val name = "SomeRandomClass"
            val description = "some description"
            val code = """
				package foo

				/**
				 * $description
				 * @configuration config - description (default: "")
				 * @configuration config2 - description2 (default: "")
				 */
				class $name: Rule {
				}
			"""
            val items = subject.run(code)
            assertThat(items[0].configuration).hasSize(2)
        }

        it("contains a misconfigured configuration option") {
            val name = "SomeRandomClass"
            val description = "some description"
            val code = """
				package foo

				/**
				 * $description
				 * @configuration sometihing: description
				 */
				class $name: Rule {
				}
			"""
            assertThatExceptionOfType(InvalidDocumentationException::class.java)
                    .isThrownBy { subject.run(code) }
        }

        it("contains compliant and noncompliant code examples") {
            val code = """
				package foo

				/**
				 * Some documentation
				 *
				 * <noncompliant>
				 * val one = 2
				 * </noncompliant>
				 *
				 * <compliant>
				 * val one = 1
				 * </compliant>
				 */
				class RandomClass : Rule {
				}
			"""
            val items = subject.run(code)
            assertThat(items[0].nonCompliantCodeExample).isEqualTo("val one = 2")
            assertThat(items[0].compliantCodeExample).isEqualTo("val one = 1")
        }

        it("has wrong noncompliant code example declaration") {
            val code = """
				package foo

				/**
				 * Some documentation
				 *
				 * <noncompliant>
				 */
				class RandomClass : Rule {
				}
			"""
            assertThatExceptionOfType(InvalidCodeExampleDocumentationException::class.java)
                    .isThrownBy { subject.run(code) }
        }

        it("has wrong compliant code example declaration") {
            val code = """
				package foo

				/**
				 * Some documentation
				 *
				 * <noncompliant>
				 * val one = 2
				 * </noncompliant>
				 * <compliant>
				 */
				class RandomClass : Rule {
				}
			"""
            assertThatExceptionOfType(InvalidCodeExampleDocumentationException::class.java)
                    .isThrownBy { subject.run(code) }
        }

        it("has wrong compliant without noncompliant code example declaration") {
            val code = """
				package foo

				/**
				 * Some documentation
				 *
				 * <compliant>
				 * val one = 1
				 * </compliant>
				 */
				class RandomClass : Rule {
				}
			"""
            assertThatExceptionOfType(InvalidCodeExampleDocumentationException::class.java)
                    .isThrownBy { subject.run(code) }
        }

        it("has wrong issue style property") {
            val name = "SomeRandomClass"
            val description = "some description"
            val code = """
				package foo

				/**
				 * $description
				 */
				class $name: Rule {

					val style = Severity.Style
					override val issue = Issue(javaClass.simpleName,
							style,
							"",
							debt = Debt.TEN_MINS)
				}
			"""
            assertThatExceptionOfType(InvalidIssueDeclaration::class.java)
                    .isThrownBy { subject.run(code) }
        }

        it("has wrong aliases property structure") {
            val name = "SomeRandomClass"
            val description = "some description"
            val code = """
				package foo

				/**
				 * $description
				 */
				class $name: Rule {

					val a = setOf("UNUSED_VARIABLE")
					override val defaultRuleIdAliases = a
					override val issue = Issue(javaClass.simpleName,
							Severity.Style,
							"",
							debt = Debt.TEN_MINS)
				}
			"""
            assertThatExceptionOfType(InvalidAliasesDeclaration::class.java)
                    .isThrownBy { subject.run(code) }
        }
    }
})
