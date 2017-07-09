package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.test.lint
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek
import org.assertj.core.api.Assertions.assertThat

/**
 * @author Ivan Balaksha
 */
class LabeledExpressionSpec : SubjectSpek<LabeledExpression>({
    subject { LabeledExpression() }
    describe("") {
        it("break with label") {
            val code = """
            fun breakWithLabel() {
                loop@ for (i in 1..100) {
                    for (j in 1..100) {
                        if (j == 5) break@loop
                    }
                }
            }"""
            assertThat(subject.lint(code)).hasSize(2)
        }
        it("continue with label") {
            val code = """
            fun continueWithLabel() {
                loop@ for (i in 1..100) {
                    for (j in 1..100) {
                        if (j == 5) continue@loop
                    }
                }
            }"""
            assertThat(subject.lint(code)).hasSize(2)
        }
        it("implicit return with label") {
            val code = """
            fun implicitReturnWithLabel(range: IntRange) {
                range.forEach {
                    if (it == 5) return@forEach
                    println(it)
                }
            }"""
            assertThat(subject.lint(code)).hasSize(1)
        }
        it("continue with label") {
            val code = """
            fun returnWithLabel(range: IntRange) {
                range.forEach label@ {
                    if (it == 5) return@label
                    println(it)
                }
            }"""
            assertThat(subject.lint(code)).hasSize(2)
        }
    }
})