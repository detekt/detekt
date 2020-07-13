package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ClassNamingSpec : Spek({

    describe("different naming conventions inside classes") {

        it("should detect no violations") {
            val findings = ClassNaming().compileAndLint(
                    """
                    class MyClassWithNumbers5

                    class NamingConventions {
                    }
                """
            )
            assertThat(findings).isEmpty()
        }

        it("should find two violations") {
            val findings = ClassNaming().compileAndLint(
                    """
                    class _NamingConventions

                    class namingConventions {}
                """
            )
            assertThat(findings).hasSize(2)
            assertThat(findings).hasTextLocations(6 to 24, 32 to 49)
        }
    }
})
