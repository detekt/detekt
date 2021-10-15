package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class OutdatedDocumentationSpec : Spek({
    val subject by memoized { OutdatedDocumentation() }

    describe("OutdatedDocumentation rule") {

        describe("general") {
            val withoutDoc = """
                class MyClass(someParam: String, val someProp: String)
                """
            it("should not report when doc is missing") {
                assertThat(subject.compileAndLint(withoutDoc)).isEmpty()
            }

            val docWithoutParamAndPropertyTags = """
                /**
                 * Some class description without referring to tags or properties
                 */
                class MyClass(someParam: String, val someProp: String)
                """
            it("should not report when doc does not contain any property or param tags") {
                assertThat(subject.compileAndLint(docWithoutParamAndPropertyTags)).isEmpty()
            }
        }

        describe("class") {
            val correctParam = """
                /**
                 * @param someParam Description of param
                 */
                class MyClass(someParam: String)
                """

            it("should not report when doc match class params") {
                assertThat(subject.compileAndLint(correctParam)).isEmpty()
            }

            val incorrectParamName = """
                /**
                 * @param someParam Description of param
                 */
                class MyClass(otherParam: String)
                """

            it("should report when doc mismatch class param name") {
                assertThat(subject.compileAndLint(incorrectParamName)).hasSize(1)
            }

            val incorrectListOfParams = """
                /**
                 * @param someParam Description of param
                 * @param someSecondParam Description of param
                 */
                class MyClass(someParam: String)
                """

            it("should report when doc mismatch class param list") {
                assertThat(subject.compileAndLint(incorrectListOfParams)).hasSize(1)
            }

            val incorrectParamOrder = """
                /**
                 * @param someParam Description of param
                 * @param otherParam Description of param
                 */
                class MyClass(otherParam: String, someParam: String)
                """

            it("should report when doc mismatch class param list order") {
                assertThat(subject.compileAndLint(incorrectParamOrder)).hasSize(1)
            }

            val correctParamAndProp = """
                /**
                 * @param someParam Description of param
                 * @property someProp Description of property
                 */
                class MyClass(someParam: String, val someProp: String)
                """

            it("should not report when doc match class params and props") {
                assertThat(subject.compileAndLint(correctParamAndProp)).isEmpty()
            }

            val correctParamIncorrectProp = """
                /**
                 * @param someParam Description of param
                 * @property someProp Description of property
                 */
                class MyClass(someParam: String, val otherProp: String)
                """

            it("should report when doc match class params but mismatch props") {
                assertThat(subject.compileAndLint(correctParamIncorrectProp)).hasSize(1)
            }

            val incorrectParamCorrectProp = """
                /**
                 * @param someParam Description of param
                 * @property someProp Description of property
                 */
                class MyClass(otherParam: String, val someProp: String)
                """

            it("should report when doc mismatch class params and match props") {
                assertThat(subject.compileAndLint(incorrectParamCorrectProp)).hasSize(1)
            }

            val incorrectConstructorDoc = """
                class MyClass {
                    /**
                     * @param someParam
                     */
                    constructor(otherParam: String)
                }
                """
            it("should report when doc for constructor is incorrect") {
                assertThat(subject.compileAndLint(incorrectConstructorDoc)).hasSize(1)
            }
        }

        describe("class with type params") {
            val correctTypeParam = """
                /**
                 * @param T Description of type param
                 * @param someParam Description of param
                 */
                class MyClass<T>(someParam: String)
                """

            it("should not report when doc match class params") {
                assertThat(subject.compileAndLint(correctTypeParam)).isEmpty()
            }

            val missingTypeParam = """
                /**
                 * @param someParam Description of param
                 */
                class MyClass<T>(someParam: String)
                """

            it("should report when doc misses type param") {
                assertThat(subject.compileAndLint(missingTypeParam)).hasSize(1)
            }

            val incorrectTypeParamName = """
                /**
                 * @param S
                 * @param someParam Description of param
                 */
                class MyClass<T>(someParam: String)
                """

            it("should report when doc mismatch type param name") {
                assertThat(subject.compileAndLint(incorrectTypeParamName)).hasSize(1)
            }

            val incorrectTypeParamList = """
                /**
                 * @param T
                 * @param someParam Description of param
                 */
                class MyClass<T, S>(someParam: String)
                """

            it("should report when doc mismatch type param list") {
                assertThat(subject.compileAndLint(incorrectTypeParamList)).hasSize(1)
            }
        }

        describe("function") {

            val correctDoc = """
                /**
                 * @param someParam Description of param
                 */
                fun myFun(someParam: String)
                """

            it("should not report when doc match function params") {
                assertThat(subject.compileAndLint(correctDoc)).isEmpty()
            }

            val incorrectParamName = """
                /**
                 * @param someParam Description of param
                 */
                fun myFun(otherParam: String)
                """

            it("should report when doc mismatch function param name") {
                assertThat(subject.compileAndLint(incorrectParamName)).hasSize(1)
            }
        }

        describe("function with type params") {
            val correctTypeParam = """
                /**
                 * @param T Description of type param
                 * @param someParam Description of param
                 */
                fun myFun<T>(someParam: String)
                """

            it("should not report when doc match function params") {
                assertThat(subject.compileAndLint(correctTypeParam)).isEmpty()
            }

            val missingTypeParam = """
                /**
                 * @param someParam Description of param
                 */
                fun myFun<T>(someParam: String)
                """

            it("should report when doc misses type param") {
                assertThat(subject.compileAndLint(missingTypeParam)).hasSize(1)
            }

            val incorrectTypeParamName = """
                /**
                 * @param S
                 * @param someParam Description of param
                 */
                fun myFun<T>(someParam: String)
                """

            it("should report when doc mismatch type param name") {
                assertThat(subject.compileAndLint(incorrectTypeParamName)).hasSize(1)
            }

            val incorrectTypeParamList = """
                /**
                 * @param T
                 * @param someParam Description of param
                 */
                fun myFun<T, S>(someParam: String)
                """

            it("should report when doc mismatch type param list") {
                assertThat(subject.compileAndLint(incorrectTypeParamList)).hasSize(1)
            }
        }

        describe("advanced scenarios") {

            val correctClassWithFunction = """
                /**
                 * @param someParam Description of param
                 */
                class MyClass(someParam: String) {
                    /**
                    * @param someParam Description of param 
                    */
                    fun myFun(someParam: String) {
                    }
                }
                """

            it("should not report when doc match all signatures") {
                assertThat(subject.compileAndLint(correctClassWithFunction)).isEmpty()
            }

            val incorrectClassWithTwoIncorrectFunctions = """
                /**
                 * @param someParam Description of param
                 */
                class MyClass(val someProp: String) {
                    /**
                    * @param someParam Description of param 
                    */
                    fun myFun(someParam: String, someSecondParam) {
                    }
                    /**
                    * @param someParam Description of param 
                    */
                    fun myOtherFun(otherParam: String) {
                    }
                    /**
                    * @param someParam Description of param 
                    */
                    class MyNestedClass(otherParam: String)
                }
                """

            it("should report for every class and function with incorrect doc") {
                assertThat(subject.compileAndLint(incorrectClassWithTwoIncorrectFunctions)).hasSize(4)
            }
        }
    }
})
