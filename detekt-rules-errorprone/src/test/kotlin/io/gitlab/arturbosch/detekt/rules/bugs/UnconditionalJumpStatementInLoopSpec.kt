package io.gitlab.arturbosch.detekt.rules.bugs

import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UnconditionalJumpStatementInLoopSpec : Spek({
    val subject by memoized { UnconditionalJumpStatementInLoop() }

    describe("UnconditionalJumpStatementInLoop rule") {

        it("reports unconditional jumps") {
            val path = resourceAsPath("UnconditionalJumpStatementInLoopPositive.kt")
            assertThat(subject.lint(path)).hasSize(8)
        }

        it("does not report conditional jumps") {
            val path = resourceAsPath("UnconditionalJumpStatementInLoopNegative.kt")
            assertThat(subject.lint(path)).isEmpty()
        }

        it("does not report an conditional elvis continue") {
            val findings = subject.lint("""
                fun main() {
                    fun compute(i: Int) = null
                    for (i in 1..5)  
                        return compute(i) ?: continue
                }
            """.trimIndent())

            assertThat(findings).isEmpty()
        }

        it("reports conditional elvis return") {
            val findings = subject.lint("""
                fun main() {
                    fun compute(i: Int) = null
                    for (i in 1..5)  
                        return compute(i) ?: return
                }
            """.trimIndent())

            assertThat(findings).hasSize(1)
        }
    }
})
