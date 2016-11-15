package io.gitlab.arturbosch.detekt.rules.formatting

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasSize
import com.natpryce.hamkrest.isEmpty
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.test.RuleTest
import io.gitlab.arturbosch.detekt.test.format
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Test

/**
 * @author Shyiko
 */
class SpacingAroundColonTest : RuleTest {

	override val rule: Rule = SpacingAroundColon(Config.empty)

	@Test
	fun testLint() {
		assertThat(rule.lint(
				"""
            class A:B
            class A2 : B2
            fun main() {
                var x:Boolean
                var y: Boolean
                call(object: DefaultConsumer(channel) { })
            }
            interface D
            interface C: D
            interface C2 : D
            """
		), hasSize(equalTo(4)))
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
		), isEmpty)
	}

	@Test
	fun classReferencesNotConsidered() {
		assertThat(rule.lint(
				"""
            fun main() {
                val x = Foo::class
            }
            """
		), isEmpty)
	}

	@Test
	fun testFormat() {
		assertThat(rule.format(
				"""
            class A:B
            fun main() {
                var x:Boolean
                var y: Boolean
            }
            interface D
            interface C: D
            """
		), equalTo(
				"""
            class A : B
            fun main() {
                var x: Boolean
                var y: Boolean
            }
            interface D
            interface C : D
            """.trimIndent()
		))
	}
}