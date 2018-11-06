package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class NestedIfDepthSpec : SubjectSpek<NestedIfDepth>({

	subject { NestedIfDepth(threshold = 2) }

	describe("simple nested ifs") {

		it("should detect too many nested ifs") {
			val code = """
				fun tooDeep() {
					if (1 > 0) {
						if (2 > 1) {
							if (3 > 2) {
								if (4 > 3) {
								}
							}
						}
					}
				}"""
			assertThat(subject.lint(code)).hasSize(1)
		}

		it("should not detect nested ifs at or below threshold") {
			val code = """
				fun notTooDeep() {
					if (1 > 0) {
						if (2 > 1) {
						}
					}
				}"""
			assertThat(subject.lint(code)).isEmpty()
		}
	}

	describe("complex(ish) ifs") {

		it("should detect ifs nested in other blocks") {
			subject.lint(ifsInOtherBlocks)
			assertThat(subject.findings).hasSize(1)
		}

		it("should detect multiple findings in one file") {
			subject.lint(multipleFindingsInOneFile)
			assertThat(subject.findings).hasSize(2)
			assertThat(subject.findings.filter { it.metricByType("NESTING DEPTH")?.value == 5 }).hasSize(1)
			assertThat(subject.findings.filter { it.metricByType("NESTING DEPTH")?.value == 3 }).hasSize(1)
		}

		it("should not count else if as two") {
			subject.lint(nestedIfCode)
			assertThat(subject.findings).isEmpty()
		}
	}
})

const val ifsInOtherBlocks = """
    fun buriedInBlocks() {
        while (true) {
            if (1 > 0) {
                (1..5).forEach {
                    if (2 > 1) {
                    } else {
                        while (true) {
                            if (3 > 2) {
                            }
                        }
                    }
                }
            }
        }
    }
"""

const val multipleFindingsInOneFile = """
    fun wayTooDeep() {
        if (1 > 0) {
            if (2 > 1) {
                if (3 > 2) {
                    if (4 > 3) {
                        if (5 > 4) {
                        }
					}
				}
			}
		}
    }

    fun notTooDeep() {
        if (1 > 0) {
            if (2 > 1) {
            }
		}
        if (3 > 2) {
            if (4 > 3) {
            }
		}
    }

    fun justTooDeep {
        if (1 > 0) {
            if (2 > 1) {
                if (3 > 2) {
    			}
    		}
    	}
    }
"""

const val nestedIfCode = """
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
			}
		} else if (psi.isNotPartOfEnum) {
            println("meaningful text")
        } else {
            println("meaningless text"
    	}
	}"""
