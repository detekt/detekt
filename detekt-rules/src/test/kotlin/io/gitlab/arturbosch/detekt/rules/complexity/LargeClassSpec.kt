package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.lint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class LargeClassSpec : Spek({

    describe("nested classes are also considered") {

        it("should detect only the nested large class which exceeds threshold 70") {
            val findings = LargeClass(threshold = 70).lint(Case.NestedClasses.path())
            assertThat(findings).hasSize(1)
            assertThat(findings).hasSourceLocations(SourceLocation(12, 15))
        }
    }

    describe("files without classes should not be considered") {

        it("should not report anything in files without classes") {
            val code = """
                const val const = 1
                val prop = 0 

                fun test() {
                    println()
                }
            """
            assertThat(LargeClass().compileAndLint(code)).isEmpty()
        }
    }
})
