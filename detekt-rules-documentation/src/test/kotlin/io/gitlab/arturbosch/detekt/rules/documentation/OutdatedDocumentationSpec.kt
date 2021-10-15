package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class OutdatedDocumentationSpec : Spek({
    val subject by memoized { OutdatedDocumentation() }

    describe("OutdatedDocumentation rule") {

        describe("class") {

            val correctParam = """
                /**
                 * @param someParam Description of param
                 */
                class MyClass(someParam: String)
                """

            it("should not report when doc match class params") {
                Assertions.assertThat(subject.compileAndLint(correctParam)).hasSize(0)
            }

            val incorrectParamName = """
                /**
                 * @param someParam Description of param
                 */
                class MyClass(otherParam: String)
                """

            it("should report when doc mismatch class param name") {
                Assertions.assertThat(subject.compileAndLint(incorrectParamName)).hasSize(1)
            }

            val incorrectListOfParams = """
                /**
                 * @param someParam Description of param
                 * @param someSecondParam Description of param
                 */
                class MyClass(someParam: String)
                """

            it("should report when doc mismatch class param list") {
                Assertions.assertThat(subject.compileAndLint(incorrectListOfParams)).hasSize(1)
            }

            val correctParamAndProp = """
                /**
                 * @param someParam Description of param
                 * @property someProp Description of property
                 */
                class MyClass(someParam: String, val someProp: String)
                """

            it("should not report when doc match class params and props") {
                Assertions.assertThat(subject.compileAndLint(correctParamAndProp)).hasSize(0)
            }

            val correctParamIncorrectProp = """
                /**
                 * @param someParam Description of param
                 * @property someProp Description of property
                 */
                class MyClass(someParam: String, val otherProp: String)
                """

            it("should report when doc match class params but mismatch props") {
                Assertions.assertThat(subject.compileAndLint(correctParamIncorrectProp)).hasSize(1)
            }

            val incorrectParamCorrectProp = """
                /**
                 * @param someParam Description of param
                 * @property someProp Description of property
                 */
                class MyClass(otherParam: String, val someProp: String)
                """

            it("should report when doc mismatch class params and match props") {
                Assertions.assertThat(subject.compileAndLint(incorrectParamCorrectProp)).hasSize(1)
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
                Assertions.assertThat(subject.compileAndLint(correctDoc)).hasSize(0)
            }

            val incorrectParamName = """
                /**
                 * @param someParam Description of param
                 */
                fun myFun(otherParam: String)
                """

            it("should report when doc mismatch function param name") {
                Assertions.assertThat(subject.compileAndLint(incorrectParamName)).hasSize(1)
            }
        }
    }
})
