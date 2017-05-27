package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.test.RuleTest
import io.gitlab.arturbosch.detekt.test.format
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * Adapted from KtLint formatting project.
 */
class SpacingAroundColonTest : RuleTest {

	override val rule: Rule = SpacingAroundColon(Config.empty)

	@Test
	fun testLint() {
		assertThat(rule.lint(
				"""
            class A:B // 1
            class A2 : B2
            fun main() {
                var x:Boolean // 1
                var y: Boolean
                call(object: DefaultConsumer(channel) { })
            }
            interface D
            interface C: D // 1
            interface C2 : D
            """
		)).hasSize(3)
	}

	@Test
	fun annotationsAreNotConsidered() {
		assertThat(rule.lint(
				"""
            @file:JvmName("Foo")
            class Example(@field:Ann val foo: String, @get:Ann val bar: String)
            class Example {
                @set:[Inject VisibleForTesting]
                public var collaborator: Collaborator
            }
            fun @receiver:Fancy String.myExtension() { }
            """
		)).isEmpty()
	}

	@Test
	fun classReferencesNotConsidered() {
		assertThat(rule.lint(
				"""
            fun main() {
                val x = Foo::class
            }
            """
		)).isEmpty()
	}

	@Test
	fun testFormat() {
		assertThat(rule.format(
				"""
            class A:B
            class C :D
            fun main() {
                var x:Boolean
                var y: Boolean
                call(object:DefaultConsumer(channel) { })
            }
            interface D
            interface C: D
            interface C2 : D
            """
		)).isEqualTo(
				"""
            class A : B
            class C : D
            fun main() {
                var x: Boolean
                var y: Boolean
                call(object: DefaultConsumer(channel) { })
            }
            interface D
            interface C : D
            interface C2 : D
            """.trimIndent()
		)
	}
}