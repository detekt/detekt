package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

private const val REPORT_UNDOCUMENTED_PARAMETER = "reportUndocumentedParameter"
private const val REPORT_UNDOCUMENTED_RECEIVER = "reportUndocumentedReceiver"

class UndocumentedPublicFunctionSpec : Spek({
    val subject by memoized { UndocumentedPublicFunction() }

    describe("UndocumentedPublicFunction rule") {

        it("reports undocumented public functions") {
            val code = """
                fun noComment1() {}
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("reports undocumented public function in object") {
            val code = """
                object Test {
                    fun noComment1() {}
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("reports undocumented public function in nested object") {
            val code = """
                class Test {
                    object Test2 {
                        fun noComment1() {}
                    }
                }
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

        it("reports undocumented public function in an interface") {
            val code = """
                interface Test {
                    fun noComment1()
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
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

        it("does not report public functions in private object") {
            val code = """
                private object Test {
                    fun noComment1() {}
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        context("nested class") {
            it("does not report public functions in internal interface") {
                val code = """
                    internal interface Foo {
                        interface Bar {
                            fun f() {
                            }
                        }
                    }
                """
                assertThat(subject.compileAndLint(code)).isEmpty()
            }

            it("does not report public functions in private class") {
                val code = """
                    class Foo {
                        private class Bar {
                            class Baz {
                                fun f() {
                                }
                            }
                        }
                    }
                """
                assertThat(subject.compileAndLint(code)).isEmpty()
            }

            it("does not report public functions in private object") {
                val code = """
                    private object Foo {
                        class Bar {
                            fun f() {
                            }
                        }
                    }
                """
                assertThat(subject.compileAndLint(code)).isEmpty()
            }
        }

        context("undocumented parameter") {
            val subjectWithParamReport by memoized {
                UndocumentedPublicFunction(TestConfig(mapOf(REPORT_UNDOCUMENTED_PARAMETER to true)))
            }

            context("value parameter") {
                it("reports undocumented parameter in undocumented function") {
                    val code = """
                        fun iAmVeryInvalid(whereAreMyDocs: Int) {}
                    """
                    assertThat(subjectWithParamReport.compileAndLint(code)).hasSize(2)
                }

                it("reports undocumented parameter in documented function") {
                    val code = """
                        /**
                         * Look, my parameter is not commented!
                         */
                        fun iAmVeryInvalid(whereAreMyDocs: Int) {}
                    """
                    assertThat(subjectWithParamReport.compileAndLint(code)).hasSize(1)
                }

                it("does not report when parameter is documented directly on documented function") {
                    val code = """
                        /**
                         * Look, my parameter is documented!
                         */
                        fun iAmVeryValid(
                            /**
                             * I am documented!
                             */
                            ohLookSomeDocs: Int
                        ) {}
                    """
                    assertThat(subjectWithParamReport.compileAndLint(code)).isEmpty()
                }

                it("does not report when parameter is documented via tag on documented function") {
                    val code = """
                        /**
                         * Look, my parameter is documented!
                         *
                         * @param ohLookSomeDocs I am documented!        
                         */
                        fun iAmVeryValid(ohLookSomeDocs: Int) {}
                    """
                    assertThat(subjectWithParamReport.compileAndLint(code)).isEmpty()
                }
            }

            context("type parameter") {
                it("reports undocumented parameter in undocumented function") {
                    val code = """
                        fun <T> iAmVeryInvalid() {}
                    """
                    assertThat(subjectWithParamReport.compileAndLint(code)).hasSize(2)
                }

                it("reports undocumented parameter in documented function") {
                    val code = """
                        /**
                         * Look, my parameter is not commented!
                         */
                        fun <T> iAmVeryInvalid() {}
                    """
                    assertThat(subjectWithParamReport.compileAndLint(code)).hasSize(1)
                }

                it("does not report when parameter is documented via tag on documented function") {
                    val code = """
                        /**
                         * Look, my parameter is documented!
                         *
                         * @param T I am documented!        
                         */
                        fun <T> iAmVeryValid() {}
                    """
                    assertThat(subjectWithParamReport.compileAndLint(code)).isEmpty()
                }
            }
        }

        context("undocumented receiver") {
            val subjectWithReceiverReport by memoized {
                UndocumentedPublicFunction(TestConfig(mapOf(REPORT_UNDOCUMENTED_RECEIVER to true)))
            }

            it("reports undocumented receiver in undocumented function") {
                val code = """
                    fun String.iAmVeryInvalid() {}
                """
                assertThat(subjectWithReceiverReport.compileAndLint(code)).hasSize(2)
            }

            it("reports undocumented parameter in documented function") {
                val code = """
                    /**
                     * Look, my receiver is not documented!
                     */
                    fun String.iAmVeryInvalid() {}
                """
                assertThat(subjectWithReceiverReport.compileAndLint(code)).hasSize(1)
            }

            it("does not report when parameter is documented via tag on documented function") {
                val code = """
                    /**
                     * Look, my receiver is documented!
                     *
                     * @receiver I am documented!        
                     */
                    fun String.iAmVeryValid() {}
                """
                assertThat(subjectWithReceiverReport.compileAndLint(code)).isEmpty()
            }
        }
    }
})
