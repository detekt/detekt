package io.gitlab.arturbosch.detekt.rules.empty

import io.github.detekt.test.utils.compileForTest
import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.lint
import io.gitlab.arturbosch.detekt.test.yamlConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class EmptyBlocksMultiRuleSpec {

    private val file = compileForTest(resourceAsPath("Empty.kt"))

    private lateinit var subject: EmptyBlocks

    @BeforeEach
    fun createSubject() {
        subject = EmptyBlocks()
    }

    @Nested
    inner class `multi rule with all empty block rules` {

        @Test
        fun `should report one finding per rule`() {
            val findings = subject.lint(file)
            // -1 because the empty kt file rule doesn't get triggered in the 'Empty' test file
            val rulesSize = subject.rules.size - 1
            assertThat(findings).hasSize(rulesSize)
        }

        @Test
        fun `should not report any as all empty block rules are deactivated`() {
            val config = yamlConfig("deactivated-empty-blocks.yml")
            val ruleSet = EmptyCodeProvider().instance(config)

            @Suppress("DEPRECATION")
            val findings = ruleSet.accept(file)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `reports an empty kt file`() {
            assertThat(subject.compileAndLint("")).hasSize(1)
        }

        @Test
        fun `reports no duplicated findings - issue #1605`() {
            val findings = subject.compileAndLint(
                """
                class EmptyBlocks {
                    class EmptyClass {}
                    fun exceptionHandling() {
                        try {
                            println()
                        } finally {
                        }
                    }
                }
            """
            )
            assertThat(findings).hasSize(2)
        }
    }
}
