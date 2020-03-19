package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object UseIfInsteadOfWhenSpec : Spek({

    val subject by memoized { UseIfInsteadOfWhen() }

    describe("UseIfInsteadOfWhen rule") {

        it("reports when using two branches") {
            val code = """
                fun function(): Boolean? {
                    val x = null
                    when (x) {
                        null -> return true
                        else -> return false
                    }
                }
                """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("does not report when using one branch") {
            val code = """
                fun function(): Boolean? {
                    val x = null
                    when (x) {
                        else -> return false
                    }
                }
                """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report when using more than two branches") {
            val code = """
                fun function(): Boolean? {
                    val x = null
                    when (x) {
                        null -> return true
                        3 -> return null
                        else -> return false
                    }
                }
                """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report when second branch is not 'else'") {
            val code = """
                fun function(): Boolean? {
                    val x = null
                    when (x) {
                        null -> return true
                        3 -> return null
                    }
                    return false
                }
                """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
})
