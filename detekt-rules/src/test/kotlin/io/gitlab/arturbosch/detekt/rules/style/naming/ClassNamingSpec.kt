package io.gitlab.arturbosch.detekt.rules.style.naming

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

/**
 * @author Artur Bosch
 */
class ClassNamingSpec : Spek({

	describe("different naming conventions inside classes") {

		it("should detect no violations") {
			val findings = NamingRules().lint(
					"""
					class MyClassWithNumbers5

					class NamingConventions {

						const val serialVersionUID = 1L
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
			val findings = NamingRules().lint(
					"""
					class _NamingConventions { // invalid

						val C_lassVariable = 5 // invalid
						val CLASSVARIABLE = 5 // invalid

						fun _classmethod() {} // invalid
						fun Classmethod() {} // invalid

						companion object {
							val __bla = Any() // invalid
						}
					}
					class namingConventions {} // invalid
				"""
			)
			assertThat(findings).hasSize(7)
		}
	}
})
