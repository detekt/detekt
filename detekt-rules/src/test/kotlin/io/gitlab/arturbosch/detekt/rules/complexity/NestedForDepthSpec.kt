package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class NestedForDepthSpec : SubjectSpek<NestedForDepth>({

	subject { NestedForDepth(threshold = 2) }

	describe("simple nested fors") {

		it("should detect too many nested fors") {
			val code = """
				fun tooDeep() {
					for (i in 1..3) {
						for (j in 1..3) {
							for (k in 1..3) {
								for (l in 1..3) {
								}
							}
						}
					}
				}"""
			assertThat(subject.lint(code)).hasSize(1)
		}

		it("should not detect nested fors at or below threshold") {
			val code = """
				fun notTooDeep() {
					for (i in 1..3) {
						for (j in 1..3) {
						}
					}
				}"""
			assertThat(subject.lint(code)).isEmpty()
		}
	}

	describe("complex fors") {

		it("should detect ifs nested in other blocks") {
			subject.lint(forsInOtherBlocks)
			assertThat(subject.findings).hasSize(1)
		}

		it("should detect multiple findings in one file") {
			subject.lint(multipleForFindingsInOneFile)
			assertThat(subject.findings).hasSize(2)
			assertThat(subject.findings.filter { it.metricByType("NESTING DEPTH")?.value == 5 }).hasSize(1)
			assertThat(subject.findings.filter { it.metricByType("NESTING DEPTH")?.value == 3 }).hasSize(1)
		}
	}
})

const val forsInOtherBlocks = """
    fun buriedInBlocks() {
        while (true) {
            for (i in 1..3) {
                (1..5).forEach {
                    if (2 > 1) {
                        for (j in 1..3) {
                            for (k in 1..3) {
                            }
                        }
                    }
                }
            }
        }
    }
"""

const val multipleForFindingsInOneFile = """
    fun wayTooDeep() {
        for (i in 1..3) {
            for (j in 1..3) {
                for (k in 1..3) {
                    for (l in 1..3) {
                        for (m in 1..3) {
                        }
					}
				}
			}
		}
    }

    fun notTooDeep() {
        for (i in 1..3) {
            for (j in 1..3) {
            }
		}
    }

    fun justTooDeep {
        for (i in 1..3) {
            for (j in 1..3) {
                for (k in 1..3) {
    			}
    		}
    	}
    }
"""

