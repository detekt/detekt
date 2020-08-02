package io.gitlab.arturbosch.detekt.rules.empty

import io.github.detekt.test.utils.compileForTest
import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.lint
import io.gitlab.arturbosch.detekt.test.yamlConfig
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class EmptyBlocksMultiRuleSpec : Spek({

    val subject by memoized { EmptyBlocks() }

    val file by memoized { compileForTest(resourceAsPath("Empty.kt")) }

    describe("multi rule with all empty block rules") {

        it("should report one finding per rule") {
            val findings = subject.lint(file)
            // -1 because the empty kt file rule doesn't get triggered in the 'Empty' test file
            val rulesSize = subject.rules.size - 1
            assertThat(findings).hasSize(rulesSize)
        }

        it("should not report any as all empty block rules are deactivated") {
            val config = yamlConfig("deactivated-empty-blocks.yml")
            val ruleSet = EmptyCodeProvider().instance(config)

            @Suppress("DEPRECATION")
            val findings = ruleSet.accept(file)

            assertThat(findings).isEmpty()
        }

        it("reports an empty kt file") {
            assertThat(subject.compileAndLint("")).hasSize(1)
        }

        it("reports no duplicated findings - issue #1605") {
            val findings = subject.compileAndLint("""
                class EmptyBlocks {
                    class EmptyClass {}
                    fun exceptionHandling() {
                        try {
                            println()
                        } finally {
                        }
                    }
                }
            """)
            assertThat(findings).hasSize(2)
        }
    }
})
