package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class OutdatedDocumentationSpec : Spek({
    val subject by memoized { OutdatedDocumentation() }

    describe("OutdatedDocumentation rule") {

        describe("general") {
            it("should not report when doc is missing") {
                val withoutDoc = """
                class MyClass(someParam: String, val someProp: String)
                """
                assertThat(subject.compileAndLint(withoutDoc)).isEmpty()
            }

            it("should not report when doc does not contain any property or param tags") {
                val docWithoutParamAndPropertyTags = """
                /**
                 * Some class description without referring to tags or properties
                 */
                class MyClass(someParam: String, val someProp: String)
                """
                assertThat(subject.compileAndLint(docWithoutParamAndPropertyTags)).isEmpty()
            }
        }

        describe("class") {
            it("should not report when doc match class params") {
                val correctParam = """
                /**
                 * @param someParam Description of param
                 */
                class MyClass(someParam: String)
                """
                assertThat(subject.compileAndLint(correctParam)).isEmpty()
            }

            it("should report when doc mismatch class param name") {
                val incorrectParamName = """
                /**
                 * @param someParam Description of param
                 */
                class MyClass(otherParam: String)
                """
                assertThat(subject.compileAndLint(incorrectParamName)).hasSize(1)
            }

            it("should report when doc mismatch class param list") {
                val incorrectListOfParams = """
                /**
                 * @param someParam Description of param
                 * @param someSecondParam Description of param
                 */
                class MyClass(someParam: String)
                """
                assertThat(subject.compileAndLint(incorrectListOfParams)).hasSize(1)
            }

            it("should report when doc mismatch class param list order") {
                val incorrectParamOrder = """
                /**
                 * @param someParam Description of param
                 * @param otherParam Description of param
                 */
                class MyClass(otherParam: String, someParam: String)
                """
                assertThat(subject.compileAndLint(incorrectParamOrder)).hasSize(1)
            }

            it("should not report when doc match class params and props") {
                val correctParamAndProp = """
                /**
                 * @param someParam Description of param
                 * @property someProp Description of property
                 */
                class MyClass(someParam: String, val someProp: String)
                """
                assertThat(subject.compileAndLint(correctParamAndProp)).isEmpty()
            }

            it("should report when doc match class params but mismatch props") {
                val correctParamIncorrectProp = """
                /**
                 * @param someParam Description of param
                 * @property someProp Description of property
                 */
                class MyClass(someParam: String, val otherProp: String)
                """
                assertThat(subject.compileAndLint(correctParamIncorrectProp)).hasSize(1)
            }

            it("should report when doc mismatch class params and match props") {
                val incorrectParamCorrectProp = """
                /**
                 * @param someParam Description of param
                 * @property someProp Description of property
                 */
                class MyClass(otherParam: String, val someProp: String)
                """
                assertThat(subject.compileAndLint(incorrectParamCorrectProp)).hasSize(1)
            }

            it("should report when doc for constructor is incorrect") {
                val incorrectConstructorDoc = """
                class MyClass {
                    /**
                     * @param someParam
                     */
                    constructor(otherParam: String)
                }
                """
                assertThat(subject.compileAndLint(incorrectConstructorDoc)).hasSize(1)
            }
        }

        describe("class with type params") {

            it("should not report when doc match class params") {
                val correctTypeParam = """
                /**
                 * @param T Description of type param
                 * @param someParam Description of param
                 */
                class MyClass<T>(someParam: String)
                """
                assertThat(subject.compileAndLint(correctTypeParam)).isEmpty()
            }

            it("should report when doc misses type param") {
                val missingTypeParam = """
                /**
                 * @param someParam Description of param
                 */
                class MyClass<T>(someParam: String)
                """
                assertThat(subject.compileAndLint(missingTypeParam)).hasSize(1)
            }

            it("should report when doc mismatch type param name") {
                val incorrectTypeParamName = """
                /**
                 * @param S Description of type param
                 * @param someParam Description of param
                 */
                class MyClass<T>(someParam: String)
                """
                assertThat(subject.compileAndLint(incorrectTypeParamName)).hasSize(1)
            }

            it("should report when doc mismatch type param list") {
                val incorrectTypeParamList = """
                /**
                 * @param T Description of type param
                 * @param someParam Description of param
                 */
                class MyClass<T, S>(someParam: String)
                """
                assertThat(subject.compileAndLint(incorrectTypeParamList)).hasSize(1)
            }
        }

        describe("function") {

            it("should not report when doc match function params") {
                val correctDoc = """
                /**
                 * @param someParam Description of param
                 */
                fun myFun(someParam: String) {}
                """
                assertThat(subject.compileAndLint(correctDoc)).isEmpty()
            }

            it("should report when doc mismatch function param name") {
                val incorrectParamName = """
                /**
                 * @param someParam Description of param
                 */
                fun myFun(otherParam: String) {}
                """
                assertThat(subject.compileAndLint(incorrectParamName)).hasSize(1)
            }
        }

        describe("function with type params") {

            it("should not report when doc match function params") {
                val correctTypeParam = """
                /**
                 * @param T Description of type param
                 * @param someParam Description of param
                 */
                fun <T> myFun(someParam: String) {}
                """
                assertThat(subject.compileAndLint(correctTypeParam)).isEmpty()
            }

            it("should report when doc misses type param") {
                val missingTypeParam = """
                /**
                 * @param someParam Description of param
                 */
                fun <T> myFun(someParam: String) {}
                """
                assertThat(subject.compileAndLint(missingTypeParam)).hasSize(1)
            }

            it("should report when doc mismatch type param name") {
                val incorrectTypeParamName = """
                /**
                 * @param S Description of type param
                 * @param someParam Description of param
                 */
                fun <T> myFun(someParam: String) {}
                """
                assertThat(subject.compileAndLint(incorrectTypeParamName)).hasSize(1)
            }

            it("should report when doc mismatch type param list") {
                val incorrectTypeParamList = """
                /**
                 * @param T Description of type param
                 * @param someParam Description of param
                 */
                fun <T, S> myFun(someParam: String) {}
                """
                assertThat(subject.compileAndLint(incorrectTypeParamList)).hasSize(1)
            }
        }

        describe("advanced scenarios") {

            it("should not report when doc match all signatures") {
                val correctClassWithFunction = """
                /**
                 * @param someParam Description of param
                 */
                class MyClass(someParam: String) {
                    /**
                    * @param someParam Description of param 
                    */
                    fun myFun(someParam: String) {}
                }
                """
                assertThat(subject.compileAndLint(correctClassWithFunction)).isEmpty()
            }

            it("should report for every class and function with incorrect doc") {
                val incorrectClassWithTwoIncorrectFunctions = """
                /**
                 * @param someParam Description of param
                 */
                class MyClass(val someProp: String) {
                    /**
                    * @param someParam Description of param 
                    */
                    fun myFun(someParam: String, someSecondParam: String) {}
                    /**
                    * @param someParam Description of param 
                    */
                    fun myOtherFun(otherParam: String) {}
                    /**
                    * @param someParam Description of param 
                    */
                    class MyNestedClass(otherParam: String)
                }
                """
                assertThat(subject.compileAndLint(incorrectClassWithTwoIncorrectFunctions)).hasSize(4)
            }
        }

        describe("configuration matchTypeParameters") {
            val configuredSubject by memoized {
                OutdatedDocumentation(TestConfig(mapOf("matchTypeParameters" to "false")))
            }

            it("should not report when class type parameters mismatch and configuration is off") {
                val incorrectClassTypeParams = """
                /**
                 * @param someParam Description of param
                 */
                class MyClass<T, S>(someParam: String)
                """
                assertThat(configuredSubject.compileAndLint(incorrectClassTypeParams)).isEmpty()
            }

            it("should not report when function type parameters mismatch and configuration is off") {
                val incorrectFunctionTypeParams = """
                /**
                 * @param someParam Description of param
                 */
                fun <T, S> myFun(someParam: String) {}
                """
                assertThat(configuredSubject.compileAndLint(incorrectFunctionTypeParams)).isEmpty()
            }
        }

        describe("configuration matchDeclarationsOrder") {
            val configuredSubject by memoized {
                OutdatedDocumentation(TestConfig(mapOf("matchDeclarationsOrder" to "false")))
            }

            it("should not report when declarations order mismatch and configuration is off") {
                val incorrectDeclarationsOrder = """
                /**
                 * @param someParam Description of param
                 * @param otherParam Description of param
                 */
                class MyClass(otherParam: String, someParam: String)
                """
                assertThat(configuredSubject.compileAndLint(incorrectDeclarationsOrder)).isEmpty()
            }

            it("should not report when declarations with types order mismatch and configuration is off") {
                val incorrectDeclarationsOrderWithType = """
                /**
                 * @param S Description of type param
                 * @param someParam Description of param
                 * @param otherParam Description of param
                 * @param T Description of param
                 */
                fun <T, S> myFun(otherParam: String, someParam: String) {}
                """
                assertThat(configuredSubject.compileAndLint(incorrectDeclarationsOrderWithType)).isEmpty()
            }
        }
    }
})
