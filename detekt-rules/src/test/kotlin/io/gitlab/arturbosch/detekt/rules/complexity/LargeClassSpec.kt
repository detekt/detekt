package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

/**
 * @author Artur Bosch
 */
class LargeClassSpec : Spek({

    describe("nested classes are also considered") {

        it("should detect only the nested large class which exceeds threshold 70") {
            assertThat(LargeClass(threshold = 70).lint(Case.NestedClasses.path())).hasSize(1)
        }
    }

    describe("files without classes should not be considered") {

        it("should not report anything in large files without classes") {
            assertThat(LargeClass().lint(Case.NoClasses.path())).isEmpty()
        }
    }
})
