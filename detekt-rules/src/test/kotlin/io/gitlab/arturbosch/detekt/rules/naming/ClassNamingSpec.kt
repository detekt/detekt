package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * @author Artur Bosch
 * @author schalkms
 */
class ClassNamingSpec : Spek({

    describe("different naming conventions inside classes") {

        it("should detect no violations") {
            val findings = NamingRules().compileAndLint(
                    """
					class MyClassWithNumbers5

					class NamingConventions {

						private val _classVariable = 5
						val classVariable = 5

						fun classMethod() {}

						fun underscoreTestMethod() {
							val (_, status) = Pair(1, 2) // valid: _
						}

						companion object {
							const val stUff = "stuff"
							val SSS = "stuff"
							val ooo = Any()
							val OOO = Any()
						}
					}
				"""
            )
            assertThat(findings).isEmpty()
        }

        it("should find seven violations") {
            val findings = NamingRules().compileAndLint(
                    """
					class _NamingConventions {

						val C_lassVariable = 5
						val CLASSVARIABLE = 5

						fun _classmethod() {}
						fun Classmethod() {}

						companion object {
							val __bla = Any()
						}
					}
					class namingConventions {}
				"""
            )
            assertThat(findings).hasSize(7)
        }
    }
})
