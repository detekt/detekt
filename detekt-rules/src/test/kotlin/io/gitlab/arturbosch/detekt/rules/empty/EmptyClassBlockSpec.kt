package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class EmptyClassBlockSpec : Spek({

    val subject by memoized { EmptyClassBlock(Config.empty) }

    describe("EmptyClassBlock rule") {

        it("reports the empty class body") {
            val code = "class SomeClass {}"
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("does not report class with comments in the body") {
            val code = """
                class SomeClass {
                    // Some comment to explain what this class is supposed to do
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report class with multiline comments in the body") {
            val code = """
                class SomeClass {
                    /*
                    Some comment to explain what this class is supposed to do
                    */
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("reports the empty nested class body") {
            val code = """
                class SomeClass {
                    class EmptyClass {}
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("reports the empty object body") {
            val code = "object SomeObject {}"
            val findings = subject.compileAndLint(code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasTextLocations(18 to 20)
        }

        it("does not report the object if it is of an anonymous class") {
            val code = """
                open class Open

                fun f() {
                     object : Open() {}
                }
                """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
})
