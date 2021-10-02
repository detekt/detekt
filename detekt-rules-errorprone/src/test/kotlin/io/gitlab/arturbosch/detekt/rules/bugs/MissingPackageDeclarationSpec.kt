package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

internal class MissingPackageDeclarationSpec : Spek({

    describe("MissingPackageDeclaration rule") {

        it("should pass if package declaration is declared") {
            val code = """
                package foo.bar

                class C
            """
            val findings = MissingPackageDeclaration().compileAndLint(code)

            assertThat(findings).isEmpty()
        }

        it("should report if package declaration is missing") {
            val code = "class C"

            val findings = MissingPackageDeclaration().compileAndLint(code)

            assertThat(findings).hasSize(1)
        }
    }
})
