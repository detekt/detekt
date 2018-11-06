package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class NestedWhenDepthSpec : SubjectSpek<NestedWhenDepth>({

	subject { NestedWhenDepth(threshold = 1) }

	describe("simple nested whens") {

		it("should detect too many nested whens") {
			val code = """
				fun tooDeep() {
				    when {
				        1 > 0 -> {
				            when {
				                2 > 1 -> {
				                    when {
				                        3 > 2 -> {
				                            when {
				                                4 > 3 -> {}
				                            }
				                        }
				                    }
				                }
				            }
				        }
				    }
				}"""
			assertThat(subject.lint(code)).hasSize(1)
		}

		it("should not detect nested whens at or below threshold") {
			val code = """
				fun notTooDeep() {
				    when {
				        1 > 0 -> {
				        }
				    }
				}"""
			assertThat(subject.lint(code)).isEmpty()
		}
	}

	describe("complex(ish) ifs") {

		it("should detect ifs nested in other blocks") {
			subject.lint(whensInOtherBlocks)
			assertThat(subject.findings).hasSize(1)
		}

		it("should detect multiple findings in one file") {
			subject.lint(multipleWhenDepthFindingsInOneFile)
			assertThat(subject.findings).hasSize(2)
			assertThat(subject.findings.filter { it.metricByType("NESTING DEPTH")?.value == 3 }).hasSize(1)
			assertThat(subject.findings.filter { it.metricByType("NESTING DEPTH")?.value == 2 }).hasSize(1)
		}

	}
})

const val whensInOtherBlocks = """
    fun buriedInBlocks() {
	    val nesting = 2
	    when {
	        1 > 0 -> {
	            if (2 > 1) {
					while (true) {
	                	when(nesting) {
	                    	2 -> { }
	                	}
					}
	            }
	        }
	    }
    }
"""

const val multipleWhenDepthFindingsInOneFile = """
    fun wayTooDeep() {
	    val nesting = 3
	    when {
	        1 > 0 -> {
	            }
	        else -> {
	            when (nesting) {
	                2 -> {
	                    when {
	                        5 > 4 -> {

	                        }
	                    }
	                }
	                3 -> {
	                }
	            }
	        }
	    }
    }

    fun notTooDeep() {
	    when {
	        1 > 0 -> {
	            }
	        else -> {
	        }
	    }
	}

    fun justTooDeep {
	    val nesting = 2
	    when {
	        1 > 0 -> {
	            if (2 > 1) {
	                when (nesting) {
	                    2 -> {
	                    }
	                    else -> {
	                    }
	                }
	            }
	        }
	    }
    }
"""
