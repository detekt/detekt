package io.gitlab.arturbosch.detekt.generator.collection

import io.gitlab.arturbosch.detekt.generator.collection.exception.InvalidDocumentationException
import io.gitlab.arturbosch.detekt.generator.util.run
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class MultiRuleCollectorSpec : Spek({

    val subject by memoized { MultiRuleCollector() }

    describe("a MultiRuleCollector") {

        it("collects no MultiRule when no class is extended") {
            val code = "class MyRule"
            assertThat(subject.run(code)).isEmpty()
        }

        it("collects no rules when no MultiRule class is extended") {
            val code = "class MyRule : Other"
            assertThat(subject.run(code)).isEmpty()
        }

        it("throws when no rules are added") {
            val code = "class MyRule : MultiRule"
            assertThatExceptionOfType(InvalidDocumentationException::class.java).isThrownBy {
                subject.run(code)
            }
        }

        it("collects all rules in fields and in the rule property") {
            val code = """
                class MyRule : MultiRule {
                    val p1 = Rule3()
                    val p2 = Rule4()

                    override val rules: List<Rule> = listOf(
                        Rule1(),
                        Rule2(),
                        p1,
                        p2
                    )
                }
            """
            val items = subject.run(code)
            assertThat(items[0].rules).hasSize(4)
            assertThat(items[0].rules).contains("Rule1", "Rule2", "Rule3", "Rule4")
        }
    }
})
