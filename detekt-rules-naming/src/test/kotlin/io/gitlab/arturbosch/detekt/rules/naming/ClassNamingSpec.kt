package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ClassNamingSpec : Spek({

    describe("different naming conventions inside classes") {

        it("should detect no violations class with numbers") {
            val code = """
                class MyClassWithNumbers5
            """

            assertThat(ClassNaming().compileAndLint(code)).isEmpty()
        }

        it("should detect no violations") {
            val code = """
                class NamingConventions {
                }
            """

            assertThat(ClassNaming().compileAndLint(code)).isEmpty()
        }

        it("should detect no violations with class using backticks") {
            val code = """
                class `NamingConventions`
            """

            assertThat(ClassNaming().compileAndLint(code)).isEmpty()
        }

        it("should detect because it have a _") {
            val code = """
                class _NamingConventions
            """

            assertThat(ClassNaming().compileAndLint(code))
                .hasSize(1)
                .hasTextLocations(6 to 24)
        }

        it("should detect because it have starts with lowercase") {
            val code = """
                class namingConventions {}
            """

            assertThat(ClassNaming().compileAndLint(code))
                .hasSize(1)
                .hasTextLocations(6 to 23)
        }

        it("should ignore the issue by alias suppression") {
            val code = """
                @Suppress("ClassName")
                class namingConventions {}
            """
            assertThat(ClassNaming().compileAndLint(code)).isEmpty()
        }
    }
})
