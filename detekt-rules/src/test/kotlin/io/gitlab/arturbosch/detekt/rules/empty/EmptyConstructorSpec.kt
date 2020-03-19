
package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class EmptyConstructorSpec : Spek({

    describe("EmptyDefaultConstructor rule") {

        it("should not report empty constructors for annotation classes with expect or actual keyword - #1362") {
            val code = """
            expect annotation class NeedsConstructor()
            actual annotation class NeedsConstructor actual constructor()

            @NeedsConstructor
            fun annotatedFunction() = Unit
        """

            val findings = EmptyDefaultConstructor(Config.empty).lint(code)

            assertThat(findings).isEmpty()
        }
    }
})
