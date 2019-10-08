package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UndocumentedPublicFunctionSpec : Spek({
    val subject by memoized { UndocumentedPublicFunction() }

    describe("UndocumentedPublicFunction rule") {

        it("reports undocumented public functions") {
            val code = """
                fun noComment1() {}
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("reports undocumented public functions in companion object") {
            val code = """
                class Test {
                    companion object {
                        fun noComment1() {}
                        public fun noComment2() {}
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(2)
        }

        it("does not report documented public function") {
            val code = """
                /**
                 * Comment
                 */
                fun commented1() {}
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report documented public function in class") {
            val code = """
				class Test {
					/**
					*
					*/
					fun commented() {}
				}
			"""
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report undocumented internal and private function") {
            val code = """
                class Test {
                    internal fun no1(){}
                    private fun no2(){}
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report undocumented nested function") {
            val code = """
                /**
                 * Comment
                 */
                fun commented() {
                    fun iDontNeedDoc() {}
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report public functions in internal class") {
            val code = """
    			internal class NoComments {
					fun nope1() {}
					public fun nope2() {}
				}
			"""
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report public functions in private class") {
            val code = """
    			private class NoComments {
					fun nope1() {}
					public fun nope2() {}
				}
			"""
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
})
