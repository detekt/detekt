package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class UnusedPrivateMemberSpec : SubjectSpek<UnusedPrivateMember>({
	subject { UnusedPrivateMember() }

	given("several classes with properties") {

		it("reports an unused member") {
			val code = """
				class Test {
				    private val unused = "This is not used"

					fun use() {
						println("This is not using a property")
					}
				}
				"""
			assertThat(subject.lint(code)).hasSize(1)
		}

		it("does not report unused public members") {
			val code = """
				class Test {
				    val unused = "This is not used"

					fun use() {
						println("This is not using a property")
					}
				}
				"""
			assertThat(subject.lint(code)).hasSize(0)
		}

		it("does not report used members") {
			val code = """
				class Test {
				    private val used = "This is used"

					fun use() {
						println(used)
					}
				}
				"""
			assertThat(subject.lint(code)).hasSize(0)
		}

		it("does not report used members but reports unused members") {
			val code = """
				class Test {
				    private val used = "This is used"
				    private val unused = "This is not used"

					fun use() {
						println(used)
					}
				}
				"""
			assertThat(subject.lint(code)).hasSize(1)
		}
	}

	given("several classes with properties and local properties") {

		it("reports an unused member") {
			val code = """
				class Test {
				    private val unused = "This is not used"

					fun use() {
						val used = "This is used"
						println(used)
					}
				}
				"""
			assertThat(subject.lint(code)).hasSize(1)
		}

		it("does not report used members") {
			val code = """
				class Test {
				    private val used = "This is used"

					fun use() {
						val text = used
						println(text)
					}
				}
				"""
			assertThat(subject.lint(code)).hasSize(0)
		}

		it("reports unused local properties") {
			val code = """
				class Test {
				    private val used = "This is used"

					fun use() {
						val unused = used
						println(used)
					}
				}
				"""
			assertThat(subject.lint(code)).hasSize(1)
		}

		it("reports unused members shadowed by local properties") {
			val code = """
				class Test {
				    private val unused = "This is not used"

					fun use() {
						val unused = "This is used"
						println(unused)
					}
				}
				"""
			assertThat(subject.lint(code)).hasSize(1)
		}
	}

	given("properties used to initialize other properties") {

		it("does not report properties used by other properties") {
			val code = """
				class Test {
				    private val used = "This is used"
					private val text = used

					fun use() {
						println(text)
					}
				}
				"""
			assertThat(subject.lint(code)).hasSize(0)
		}

		it("does not report properties used by inner classes") {
			val code = """
				class Test {
					private val unused = "This is not used"

					inner class Something {
						val test = unused
					}
				}
				"""
			assertThat(subject.lint(code)).hasSize(0)
		}
	}

	given("function parameters") {
		it("reports single parameters if they are unused") {
			val code = """
			class Test {
				val value = usedMethod(1)

				private fun usedMethod(unusedParameter: Int): Int {
					return 5
				}
			}
			"""

			assertThat(subject.lint(code)).hasSize(1)
		}

		it("does not report single parameters if they used") {
			val code = """
			class Test {
				val value = usedMethod(1)

				private fun usedMethod(used: Int): Int {
					return used
				}
			}
			"""

			assertThat(subject.lint(code)).hasSize(1)
		}

		it("reports parameters that are unused") {
			val code = """
			class Test {
				val value = usedMethod(1, 2)

				private fun usedMethod(unusedParameter: Int, usedParameter: Int): Int {
					return usedParameter
				}
			}
			"""

			assertThat(subject.lint(code)).hasSize(1)
		}

	}

	given("unused private functions") {
		it("does not report used private functions") {
			val code = """
			class Test {
				val value = usedMethod()

				private fun usedMethod(): Int {
					return usedParameter
				}
			}
			"""

			assertThat(subject.lint(code)).hasSize(0)
		}

		it("reports unused private functions") {
			val code = """
			class Test {
				private fun unusedFunction(): Int {
					return 5
				}
			}
			"""

			assertThat(subject.lint(code)).hasSize(1)
		}
	}

	given("private functions only used by unused private functions") {
		it("reports unused private functions") {
			val code = """
			class Test {
				private fun unusedFunction(): Int {
					return someOtherUnusedFunction()
				}

				private fun someOtherUnusedFunction() {
					println("Never used")
				}
			}
			"""

			assertThat(subject.lint(code)).hasSize(2)
		}

		it("reports unused private functions with when branches") {
			val code = """
			class Test {
				private fun run(boolean go) {
					boolean used = true;
					if (go && used) {
						String concat = "";
						when (concat) {
							"truetrue": run2()
							else -> println("nothing")
						}
					}
				}

				private void run2() {
					val strings = listOf<String>()
					strings.forEach {
						println(it)
					}
				}
			}
			"""

			assertThat(subject.lint(code)).hasSize(2)
		}

		it("reports unused private functions with if branches") {
			val code = """
			class Test {
				private fun run(boolean go) {
					boolean used = false;
					if (used) {
						run()
					}
				}

				private void run2() {
					val strings = listOf<String>()
					strings.forEach {
						println(it)
					}
				}
			}
			"""

			assertThat(subject.lint(code)).hasSize(2)
		}
	}



})
