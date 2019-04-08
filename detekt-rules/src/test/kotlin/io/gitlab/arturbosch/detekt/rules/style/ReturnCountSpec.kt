package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ReturnCountSpec : Spek({

    describe("ReturnCount rule") {

        context("a file with 3 returns") {
            val code = """
			fun test(x: Int): Int {
				when (x) {
					5 -> return 5
					4 -> return 4
					3 -> return 3
				}
			}
		"""

            it("should get flagged by default") {
                val findings = ReturnCount().lint(code)
                assertThat(findings).hasSize(1)
            }

            it("should not get flagged when max value is 3") {
                val findings = ReturnCount(TestConfig(mapOf(ReturnCount.MAX to "3"))).lint(code)
                assertThat(findings).hasSize(0)
            }

            it("should get flagged when max value is 1") {
                val findings = ReturnCount(TestConfig(mapOf(ReturnCount.MAX to "1"))).lint(code)
                assertThat(findings).hasSize(1)
            }
        }

        context("a file with 2 returns") {
            val code = """
			fun test(x: Int): Int {
				when (x) {
					5 -> return 5
					4 -> return 4
				}
			}
		"""

            it("should not get flagged by default") {
                val findings = ReturnCount().lint(code)
                assertThat(findings).hasSize(0)
            }

            it("should not get flagged when max value is 2") {
                val findings = ReturnCount(TestConfig(mapOf(ReturnCount.MAX to "2"))).lint(code)
                assertThat(findings).hasSize(0)
            }

            it("should get flagged when max value is 1") {
                val findings = ReturnCount(TestConfig(mapOf(ReturnCount.MAX to "1"))).lint(code)
                assertThat(findings).hasSize(1)
            }
        }

        context("a function is ignored") {
            val code = """
    		fun test(x: Int): Int {
				when (x) {
					5 -> return 5
					4 -> return 4
					3 -> return 3
				}
			}
		"""

            it("should not get flagged") {
                val findings = ReturnCount(TestConfig(mapOf(
                        ReturnCount.MAX to "2",
                        ReturnCount.EXCLUDED_FUNCTIONS to "test")
                )).lint(code)
                assertThat(findings).isEmpty()
            }
        }

        context("a subset of functions are ignored") {
            val code = """
    		fun test1(x: Int): Int {
				when (x) {
					5 -> return 5
					4 -> return 4
					3 -> return 3
				}
			}

			fun test2(x: Int): Int {
				when (x) {
					5 -> return 5
					4 -> return 4
					3 -> return 3
				}
			}

			fun test3(x: Int): Int {
				when (x) {
					5 -> return 5
					4 -> return 4
					3 -> return 3
				}
			}
		"""

            it("should flag none of the ignored functions") {
                val findings = ReturnCount(TestConfig(mapOf(
                        ReturnCount.MAX to "2",
                        ReturnCount.EXCLUDED_FUNCTIONS to "test1,test2")
                )).lint(code)
                assertThat(findings).hasSize(1)
            }
        }

        context("a function with inner object") {
            val code = """
			fun test(x: Int): Int {
				val a = object {
					fun test2(x: Int): Int {
						when (x) {
							5 -> return 5
							else -> return 0
						}
					}
				}
				when (x) {
					5 -> return 5
					else -> return 0
				}
			}
    	"""

            it("should not get flag when returns is in inner object") {
                val findings = ReturnCount(TestConfig(mapOf(ReturnCount.MAX to "2"))).lint(code)
                assertThat(findings).hasSize(0)
            }
        }

        context("a function with 2 inner object") {
            val code = """
			fun test(x: Int): Int {
				val a = object {
					fun test2(x: Int): Int {
						val b = object {
							fun test3(x: Int): Int {
								when (x) {
									5 -> return 5
									else -> return 0
								}
							}
						}
						when (x) {
							5 -> return 5
							else -> return 0
						}
					}
				}
				when (x) {
					5 -> return 5
					else -> return 0
				}
			}
    	"""

            it("should not get flag when returns is in inner object") {
                val findings = ReturnCount(TestConfig(mapOf(ReturnCount.MAX to "2"))).lint(code)
                assertThat(findings).hasSize(0)
            }
        }

        context("a function with 2 inner object and exceeded max") {
            val code = """
			fun test(x: Int): Int {
				val a = object {
					fun test2(x: Int): Int {
						val b = object {
							fun test3(x: Int): Int {
								when (x) {
									5 -> return 5
									else -> return 0
								}
							}
						}
						when (x) {
							5 -> return 5
							else -> return 0
						}
					}
				}
				when (x) {
					5 -> return 5
					4 -> return 4
					3 -> return 3
					else -> return 0
				}
			}
    	"""

            it("should get flagged when returns is in inner object") {
                val findings = ReturnCount(TestConfig(mapOf(ReturnCount.MAX to "2"))).lint(code)
                assertThat(findings).hasSize(1)
            }
        }

        context("function with multiple labeled return statements") {

            val code = """
			fun readUsers(name: String): Flowable<User> {
			return userDao.read(name)
				.flatMap {
					if (it.isEmpty()) return@flatMap Flowable.empty<User>()
					return@flatMap Flowable.just(it[0])
				}
    		}
		""".trimIndent()

            it("should not count labeled returns from lambda by default") {
                val findings = ReturnCount().lint(code)
                assertThat(findings).isEmpty()
            }

            it("should count labeled returns from lambda when activated") {
                val findings = ReturnCount(
                        TestConfig(mapOf("excludeReturnFromLambda" to "false"))).lint(code)
                assertThat(findings).hasSize(1)
            }

            it("should be empty when labeled returns are de-activated") {
                val findings = ReturnCount(
                        TestConfig(mapOf(
                                "excludeLabeled" to "true",
                                "excludeReturnFromLambda" to "false"
                        ))).lint(code)
                assertThat(findings).isEmpty()
            }
        }
    }
})
