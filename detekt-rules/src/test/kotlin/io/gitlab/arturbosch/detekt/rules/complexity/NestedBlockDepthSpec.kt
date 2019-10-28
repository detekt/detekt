package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class NestedBlockDepthSpec : Spek({

    val subject by memoized { NestedBlockDepth(threshold = 4) }

    describe("nested classes are also considered") {
        it("should detect only the nested large class") {
            subject.lint(Case.NestedClasses.path())
            assertThat(subject.findings).hasSize(1)
            assertThat((subject.findings[0] as ThresholdedCodeSmell).value).isEqualTo(5)
        }

        it("should detect too nested block depth") {
            val code = """
                fun f() {
                    if (true) {
                        if (true) {
                            if (true) {
                                if (true) {
                                }
                            }
                        }
                    }
                }"""
            val findings = subject.compileAndLint(code)

            assertThat(findings).hasSize(1)
            assertThat(findings).hasTextLocations(4 to 5)
        }

        it("should not detect valid nested block depth") {
            val code = """
                fun f() {
                    if (true) {
                        if (true) {
                            if (true) {
                            }
                        }
                    }
                }"""
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("should not count else if as two") {
            subject.lint(nestedBlockCode)
            assertThat(subject.findings).isEmpty()
        }
    }
})

const val nestedBlockCode = """
    override fun procedure(node: ASTNode) {
        val psi = node.psi
        if (psi.isNotPartOfEnum() && psi.isNotPartOfString()) {
            if (psi.isDoubleSemicolon()) {
                addFindings(CodeSmell(id, Entity.from(psi)))
                withAutoCorrect {
                    deleteOneOrTwoSemicolons(node as LeafPsiElement)
                }
            } else if (psi.isSemicolon()) {
                val nextLeaf = psi.nextLeaf()
                if (nextLeaf.isSemicolonOrEOF() || nextTokenHasSpaces(nextLeaf)) {
                    addFindings(CodeSmell(id, Entity.from(psi)))
                    withAutoCorrect { psi.delete() }
                }
            }
        }
    }"""
