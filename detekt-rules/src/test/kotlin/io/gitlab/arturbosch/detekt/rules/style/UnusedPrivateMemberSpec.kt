package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek
import java.util.regex.PatternSyntaxException
import kotlin.test.assertFailsWith

class UnusedPrivateMemberSpec : SubjectSpek<UnusedPrivateMember>({

	subject { UnusedPrivateMember() }

	val regexTestingCode = """
				class Test {
					private val used = "This is used"
					private val unused = "This is not used"

					fun use() {
						println(used)
					}
				}
				"""

	given("cases file with different findings") {

		it("positive cases file") {
			assertThat(subject.lint(Case.UnusedPrivateMemberPositive.path())).hasSize(13)
		}

		it("negative cases file") {
			assertThat(subject.lint(Case.UnusedPrivateMemberNegative.path())).isEmpty()
		}
	}

	given("interface functions") {

		it("should not report parameters in interface functions") {
			val code = """
				interface UserPlugin {
					fun plug(application: Application)
					fun unplug()
				}
			"""
			assertThat(subject.lint(code)).isEmpty()
		}
	}

	given("overridden functions") {

		it("should not report parameters in not private functions") {
			val code = """
				override fun funA() {
					objectA.resolve(valA, object : MyCallback {
						override fun onResolveFailed(throwable: Throwable) {
							errorMessage.visibility = View.VISIBLE
						}
					})
				}
			"""
			assertThat(subject.lint(code)).isEmpty()
		}
	}

	given("classes accessing constants from companion objects") {

		it("should not report used constants") {
			val code = """
				class A {
					companion object {
						private const val MY_CONST = 42
					}

					fun a() {
						Completable.timer(MY_CONST.toLong(), TimeUnit.MILLISECONDS)
								.subscribe()
					}
				}
			"""
			assertThat(subject.lint(code)).isEmpty()
		}
	}

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
			assertThat(subject.lint(code)).isEmpty()
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
			assertThat(subject.lint(code)).isEmpty()
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

		it("does not fail when disabled with invalid regex") {
			val configRules = mapOf(
					"active" to "false",
					UnusedPrivateMember.ALLOWED_NAMES_PATTERN to "*foo"
			)
			val config = TestConfig(configRules)
			assertThat(UnusedPrivateMember(config).lint(regexTestingCode)).isEmpty()
		}

		it("does fail when enabled with invalid regex") {
			val configRules = mapOf(UnusedPrivateMember.ALLOWED_NAMES_PATTERN to "*foo")
			val config = TestConfig(configRules)
			assertFailsWith<PatternSyntaxException> {
				UnusedPrivateMember(config).lint(regexTestingCode)
			}
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
			assertThat(subject.lint(code)).isEmpty()
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
	}

	given("loop iterators") {

		it("should not depend on evaluation order of functions or properties") {
			val code = """
				fun RuleSetProvider.provided() = ruleSetId in defaultRuleSetIds

				val defaultRuleSetIds = listOf("comments", "complexity", "empty-blocks",
						"exceptions", "potential-bugs", "performance", "style")
			"""
			assertThat(subject.lint(code)).isEmpty()
		}

		it("doesn't report loop properties") {
			val code = """
				class Test {
					fun use() {
						for (i in 0 until 10) {
							println(i)
						}
					}
				}
				"""
			assertThat(subject.lint(code)).isEmpty()
		}

		it("reports unused loop property") {
			val code = """
				class Test {
					fun use() {
						for (i in 0 until 10) {
						}
					}
				}
				"""
			assertThat(subject.lint(code)).hasSize(1)
		}

		it("reports unused loop property in indexed array") {
			val code = """
				class Test {
					fun use() {
						val array = intArrayOf(1, 2, 3)
						for ((index, value) in array.withIndex()) {
							println(index)
						}
					}
				}
				"""
			assertThat(subject.lint(code)).hasSize(1)
		}

		it("reports all unused loop properties in indexed array") {
			val code = """
				class Test {
					fun use() {
						val array = intArrayOf(1, 2, 3)
						for ((index, value) in array.withIndex()) {
						}
					}
				}
				"""
			assertThat(subject.lint(code)).hasSize(2)
		}

		it("does not report used loop properties in indexed array") {
			val code = """
				class Test {
					fun use() {
						val array = intArrayOf(1, 2, 3)
						for ((index, value) in array.withIndex()) {
							println(index)
							println(value)
						}
					}
				}
				"""
			assertThat(subject.lint(code)).isEmpty()
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
			assertThat(subject.lint(code)).isEmpty()
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
			assertThat(subject.lint(code)).isEmpty()
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

		it("does not report single parameters if they used in return statement") {
			val code = """
			class Test {
				val value = usedMethod(1)

				private fun usedMethod(used: Int): Int {
					return used
				}
			}
			"""

			assertThat(subject.lint(code)).isEmpty()
		}

		it("does not report single parameters if they used in function") {
			val code = """
			class Test {
				val value = usedMethod(1)

				private fun usedMethod(used: Int) {
					println(used)
				}
			}
			"""

			assertThat(subject.lint(code)).isEmpty()
		}

		it("reports parameters that are unused in return statement") {
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

		it("reports parameters that are unused in function") {
			val code = """
			class Test {
				val value = usedMethod(1, 2)

				private fun usedMethod(unusedParameter: Int, usedParameter: Int) {
					println(usedParameter)
				}
			}
			"""

			assertThat(subject.lint(code)).hasSize(1)
		}
	}

	given("top level function parameters") {
		it("reports single parameters if they are unused") {
			val code = """
			fun function(unusedParameter: Int): Int {
				return 5
			}
			"""

			assertThat(subject.lint(code)).hasSize(1)
		}

		it("does not report single parameters if they used in return statement") {
			val code = """
			fun function(used: Int): Int {
				return used
			}
			"""

			assertThat(subject.lint(code)).isEmpty()
		}

		it("does not report single parameters if they used in function") {
			val code = """
			fun function(used: Int) {
				println(used)
			}
			"""

			assertThat(subject.lint(code)).isEmpty()
		}

		it("reports parameters that are unused in return statement") {
			val code = """
			fun function(unusedParameter: Int, usedParameter: Int): Int {
				return usedParameter
			}
			"""

			assertThat(subject.lint(code)).hasSize(1)
		}

		it("reports parameters that are unused in function") {
			val code = """
			fun function(unusedParameter: Int, usedParameter: Int) {
				println(usedParameter)
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
					return 5
				}
			}
			"""

			assertThat(subject.lint(code)).isEmpty()
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

		it("reports the non called private function") {
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

			assertThat(subject.lint(code)).hasSize(1)
		}
	}

	given("unused class declarations which are allowed") {

		it("does not report the unused private property") {
			val code = """
				class Test {
					private val ignored = ""
				}"""
			assertThat(subject.lint(code)).isEmpty()
		}

		it("does not report the unused private function and parameter") {
			val code = """
				class Test {
					private fun ignored(ignored: Int) {}
				}"""
			assertThat(subject.lint(code)).isEmpty()
		}
	}

	given("nested class declarations") {

		it("reports unused nested private property") {
			val code = """
				class Test {
					class Inner {
						private val unused = 1
					}
				}"""
			assertThat(subject.lint(code)).hasSize(1)
		}

		it("does not report used nested private property") {
			val code = """
				class Test {
					class Inner {
						private val used = 1
						fun someFunction() = used
					}
				}"""
			assertThat(subject.lint(code)).isEmpty()
		}
	}

	given("parameters in primary constructors") {
		it("reports unused private property") {
			val code = """
				class Test(private val unused: Any)
				"""
			assertThat(subject.lint(code)).hasSize(1)
		}

		it("reports unused parameter") {
			val code = """
				class Test(unused: Any)
				"""
			assertThat(subject.lint(code)).hasSize(1)
		}

		it("does not report used parameter for calling super") {
			val code = """
    			class Parent(val ignored: Any)
				class Test(used: Any) : Parent(used)
				"""
			assertThat(subject.lint(code)).isEmpty()
		}

		it("does not report used parameter in init block") {
			val code = """
				class Test(used: Any) {
					init {
						used.toString()
					}
				}
				"""
			assertThat(subject.lint(code)).isEmpty()
		}

		it("does not report used parameter to initialize property") {
			val code = """
				class Test(used: Any) {
					val usedString = used.toString()
				}
				"""
			assertThat(subject.lint(code)).isEmpty()
		}

		it("does not report public property") {
			val code = """
				class Test(val unused: Any)
				"""
			assertThat(subject.lint(code)).isEmpty()
		}

		it("does not report private property used in init block") {
			val code = """
				class Test(private val used: Any) {
					init { used.toString() }
				}
				"""
			assertThat(subject.lint(code)).isEmpty()
		}

		it("does not report private property used in function") {
			val code = """
				class Test(private val used: Any) {
					fun something() {
						used.toString()
					}
				}
				"""
			assertThat(subject.lint(code)).isEmpty()
		}
	}
})
