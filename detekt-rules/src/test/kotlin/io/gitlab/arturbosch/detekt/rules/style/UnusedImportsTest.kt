package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.test.RuleTest
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * @author Shyiko
 * @author Artur Bosch
 * @author Mauin
 */
class UnusedImportsTest : RuleTest {

	override val rule: Rule = UnusedImports(Config.empty)

	@Test
	fun infixOperators() {
		assertThat(rule.lint(
				"""
            import tasks.success

            fun main() {
				task {
				} success {
				}
            }
            """
		)).hasSize(0)
	}

	@Test
	fun shouldTakeIntoAccountDocumentation() {
		assertThat(rule.lint(
				"""
            import tasks.success
            import tasks.failure
            import tasks.undefined

            /**
            *  Reference to [failure]
            */
            class Test{
              /** Reference to [undefined]*/
              fun main() {
                task {
                } success {
                }
              }
            }
            """
		)).isEmpty()
	}

	@Test
	internal fun shouldIgnoreLabelForLink() {
		assertThat(rule.lint(
				"""
            import tasks.success
            import tasks.failure
            import tasks.undefined

            /**
            * Reference [undefined][failure]
            */
            fun main() {
            task {
            } success {
            }
}
            """
		)).hasSize(1)
	}

	@Test
	fun considerKdocReferencesWithMethodCalls() {
		val code = """
			package com.example

			import android.text.TextWatcher

			class Test {
				/**
				 * [TextWatcher.beforeTextChanged]
				 */
				fun test() {
					TODO()
				}
			}
		"""

		assertThat(rule.lint(code)).hasSize(0)
	}


	@Test
	fun differentCases() {
		assertThat(rule.lint(
				"""
            import p.a
            import p.B6 // positive
            import p.B as B12 // positive
            import p2.B as B2
            import p.C
            import escaped.`when`
            import escaped.`foo` // positive
            import p.D

            /** reference to [D] */
            fun main() {
                println(a())
                C.call()
                fn(B2.NAME)
                `when`()
            }
            """
		)).hasSize(3)
	}
}
