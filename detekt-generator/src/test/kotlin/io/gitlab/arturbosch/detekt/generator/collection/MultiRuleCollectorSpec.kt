package io.gitlab.arturbosch.detekt.generator.collection

import io.gitlab.arturbosch.detekt.generator.collection.exception.InvalidDocumentationException
import io.gitlab.arturbosch.detekt.generator.util.run
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class MultiRuleCollectorSpec : SubjectSpek<MultiRuleCollector>({

    subject { MultiRuleCollector() }

    describe("a MultiRuleCollector") {

        it("collects no multirule when no class is extended") {
            val code = """
				package foo

				class SomeRandomClass {
				}
			"""
            val items = subject.run(code)
            assertThat(items).isEmpty()
        }

        it("collects no rules when no multirule class is extended") {
            val code = """
				package foo

				class SomeRandomClass: SomeOtherClass {
				}
			"""
            val items = subject.run(code)
            assertThat(items).isEmpty()
        }

        it("throws when no rules are added") {
            val name = "SomeRandomClass"
            val code = """
				package foo

				class $name: MultiRule {
				}
			"""
            assertThatExceptionOfType(InvalidDocumentationException::class.java).isThrownBy {
                subject.run(code)
            }
        }

        it("collects all rules in fields and in the rule property") {
            val name = "SomeRandomClass"
            val code = """
				package foo

				class $name: MultiRule {
					val propertyRuleOne = RuleOne()
					val propertyRuleTwo = RuleTwo()

					override val rules: List<Rule> = listOf(
								FirstRule(),
								SecondRule(),
								propertyRuleOne,
								propertyRuleTwo
						)
				}
			"""
            val items = subject.run(code)
            assertThat(items[0].rules).hasSize(4)
            assertThat(items[0].rules).contains("FirstRule", "SecondRule", "RuleOne", "RuleTwo")
        }
    }
})
