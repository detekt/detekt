package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.YamlConfig
import io.gitlab.arturbosch.detekt.test.RuleTest
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import io.gitlab.arturbosch.detekt.test.resource
import io.gitlab.arturbosch.detekt.test.resourceAsString
import io.gitlab.arturbosch.detekt.test.resourcePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * Adapted from KtLint.
 *
 * @author Artur Bosch
 */
class IndentationTest : RuleTest {

	override val rule: Rule = Indentation(Config.empty)

	@Test
	fun declarationOfBHasWrongIndentation() {
		assertThat(rule.lint(
				"""
            /**
             * _
             */
            fun main() {
                val a = 0
                    val b = 0
                if (a == 0) {
                    println(a)
                }
                val b = builder().setX().setY()
                    .build()
               val c = builder("long_string" +
                    "")
            }

            class A {
                var x: String
                    get() = ""
                    set(v: String) { x = v }
            }
            """
		)).hasSize(1)
	}

	@Test
	fun verticallyAlignedParametersDoNotTriggerAnError() {
		assertThat(rule.lint(
				"""
            data class D(val a: Any,
                         @Test val b: Any,
                         val c: Any = 0) {
            }

            data class D2(
                val a: Any,
                val b: Any,
                val c: Any
            ) {
            }

            fun f(val a: Any,
                  val b: Any,
                  val c: Any) {
            }

            fun f2(
                val a: Any,
                val b: Any,
                val c: Any
            ) {
            }
            """.trimIndent()
		)).isEmpty()
		assertThat(rule.lint(
				"""
            class A(
               //
            ) {}
            """.trimIndent()
		)).hasSize(1)
	}

	@Test
	fun customIndentSizeOfTwo() {
		assertThat(Indentation(TestConfig(mapOf("indentSize" to "2"))).lint(
				"""
            /**
             * _
             */
            fun main() {
                val v = ""
                println(v)
            }

            class A {
              var x: String
                get() = ""
                set(v: String) { x = v }
            }
            """.trimIndent()
		)).isEmpty()
	}

	@Test
	fun defaultIndentSizeNoClassCastException() {
		assertThat(Indentation(Config.empty).lint(
				"""
            class A {
                var x: String
                get() = ""
                set(v: String) { x = v }
            }
            """.trimIndent()
		)).isEmpty()
	}

	@Test
	fun loadedIndentSizeNoClassCastException() {
		val config = YamlConfig.loadResource(resource("indent.yml"))
		val config2 = resource("indent.yml")
		val config3 = resourcePath("indent.yml")
		val config4 = resourceAsString("indent.yml")
		println(config2)
		println(config3)
		println(config4)
		assertThat(Indentation(config).lint(
				"""
            class A {
                var x: String
                get() = ""
                set(v: String) { x = v }
            }
            """.trimIndent()
		)).isEmpty()
	}
}