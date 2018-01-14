package io.gitlab.arturbosch.detekt.generator.collection

import io.gitlab.arturbosch.detekt.generator.util.run
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class RuleCollectorSpec : SubjectSpek<RuleCollector>({

	subject { RuleCollector() }

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

		it("collects a rule when class extends Rule") {
			val code = """
				package foo

				class SomeRandomClass: Rule {
				}
			"""
			val items = subject.run(code)
			assertThat(items).hasSize(1)
		}

		it("collects a rule when class extends ThresholdRule") {
			val code = """
				package foo

				class SomeRandomClass: ThresholdRule {
				}
			"""
			val items = subject.run(code)
			assertThat(items).hasSize(1)
		}

		it("sets the class name as the rule name") {
			val name = "SomeRandomClass"
			val code = """
				package foo

				class $name: Rule {
				}
			"""
			val items = subject.run(code)
			assertThat(items[0].name).isEqualTo(name)
		}

		it("has no description") {
			val name = "SomeRandomClass"
			val code = """
				package foo

				class $name: Rule {
				}
			"""
			val items = subject.run(code)
			assertThat(items[0].description).isEmpty()
		}

		it("has a description") {
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

		it("is active if the tag is there") {
			val name = "SomeRandomClass"
			val description = "some description"
			val code = """
				package foo

				/**
				 * $description
				 * @active
				 */
				class $name: Rule {
				}
			"""
			val items = subject.run(code)
			assertThat(items[0].active).isTrue()
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
			val items = subject.run(code)
			assertThat(items[0].configuration).isEmpty()
		}

		it("contains compliant and noncompliant code examples") {
			val code = """
				package foo

				/**
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
				 * <noncompliant>
				 */
				class RandomClass : Rule {
				}
			"""
			val thrown = catchThrowable { subject.run(code) }
			assertThat(thrown).isInstanceOf(InvalidCodeExampleDocumentationException::class.java)
		}

		it("has wrong compliant code example declaration") {
			val code = """
				package foo

				/**
				 * <noncompliant>
				 * val one = 2
				 * </noncompliant>
				 * <compliant>
				 */
				class RandomClass : Rule {
				}
			"""
			val thrown = catchThrowable { subject.run(code) }
			assertThat(thrown).isInstanceOf(InvalidCodeExampleDocumentationException::class.java)
		}
	}
})
