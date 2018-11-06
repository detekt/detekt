package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class NestedTryDepthSpec : SubjectSpek<NestedTryDepth>({

	subject { NestedTryDepth(threshold = 2) }

	describe("simple nested tries") {

		it("should detect too many nested tries") {
			val code = """
				fun tooDeep() {
	            	try {
	            		try {
	            			try {
	        				} catch (e: Exception) {
	        				}
	        			} catch (e: Exception) {
	        			}
	        		} catch (e: Exception) {
	        		}
				}"""
			assertThat(subject.lint(code)).hasSize(1)
		}

		it("should not detect nested tries at or below threshold") {
			val code = """
				fun notTooDeep() {
        			try {
        				try {
        				} catch (e: Exception) {
        				}
        			} catch (e: Exception) {
        			}
				}"""
			assertThat(subject.lint(code)).isEmpty()
		}
	}

	describe("complex(ish) tries") {

		it("should detect tries nested in other blocks") {
			subject.lint(triesInOtherBlocks)
			assertThat(subject.findings).hasSize(1)
		}

		it("should detect multiple findings in one file") {
			subject.lint(multipleTryDepthFindingsInOneFile)
			assertThat(subject.findings).hasSize(2)
			assertThat(subject.findings.filter { it.metricByType("NESTING DEPTH")?.value == 5 }).hasSize(1)
			assertThat(subject.findings.filter { it.metricByType("NESTING DEPTH")?.value == 3 }).hasSize(1)
		}
	}
})

const val triesInOtherBlocks = """
    fun buriedInBlocks() {
        while (true) {
            try {
                (1..5).forEach {
                    try {
                        while (true) {
                            try {
                            } catch (e: Exception) {
                            }
                        }
                    } catch (e: Exception) {
					}
                }
            } catch (e: Exception) {
			}
        }
    }
"""

const val multipleTryDepthFindingsInOneFile = """
    fun wayTooDeep() {
        try {
            try {
            	try {
            		try {
            			try {
        				} catch (e: Exception) {
        				}
        			} catch (e: Exception) {
        			}
        		} catch (e: Exception) {
        		}
        	} catch (e: Exception) {
        	}
        } catch (e: Exception) {
        }
    }

    fun notTooDeep() {
        try {
        	try {
        	} catch (e: Exception) {
        	}
        } catch (e: Exception) {
        }

        try {
        } catch (e: Exception) {
        }
    }

    fun justTooDeep {
        try {
			try {
				try {
				} catch (e: Exception) {
				}
			} catch (e: Exception) {
		}
		catch (e: Exception) {
    }
"""
