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
 *
 * @author Artur Bosch
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
                call(object: DefaultConsumer(channel) { }) // 1
            }
            interface D
            interface C: D // 1
            interface C2 : D
            """
		)).hasSize(4)
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
	fun spaceAfterTryCatchParameterColon() {
		assertThat(rule.format("fun main() { try {} catch(e:Exception) {} }"))
				.isEqualTo("fun main() { try {} catch(e: Exception) {} }")
	}

	@Test
	fun spaceAroundObjectDeclarationColon() {
		assertThat(rule.format("fun main() { call(object:DefaultConsumer(channel) { }) }"))
				.isEqualTo("fun main() { call(object : DefaultConsumer(channel) { }) }")
	}

	@Test
	fun spaceAfterFunctionParametersAndReturnType() {
		assertThat(rule.format("fun main(x:Int, y:String):Boolean {}"))
				.isEqualTo("fun main(x: Int, y: String): Boolean {}")
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
            }
            interface D
            interface C : D
            interface C2 : D
            """.trimIndent()
		)
	}
}