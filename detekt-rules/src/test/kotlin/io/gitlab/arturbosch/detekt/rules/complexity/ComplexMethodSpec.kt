package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

/**
 * @author Artur Bosch
 */
class ComplexMethodSpec : Spek({

	given("a complex method") {

		it("finds one complex method") {
			val subject = ComplexMethod()
			subject.lint(Case.ComplexClass.path())
			assertThat(subject.findings).hasSize(1)
			assertThat((subject.findings[0] as ThresholdedCodeSmell).value).isEqualTo(20)
			assertThat((subject.findings[0] as ThresholdedCodeSmell).threshold).isEqualTo(10)
		}
	}

	given("several complex methods") {

		val path = Case.ComplexMethods.path()

		it("does not report complex methods with a single when expression") {
			val config = TestConfig(mapOf(ComplexMethod.IGNORE_SINGLE_WHEN_EXPRESSION to "true"))
			val subject = ComplexMethod(config, threshold = 4)
			assertThat(subject.lint(path)).hasSize(1)
		}

		it("reports all complex methods") {
			val subject = ComplexMethod(threshold = 4)
			assertThat(subject.lint(path)).hasSize(5)
		}
	}

	given("a method returning a complex abstract class implementation") {
		val subject = ComplexMethod()
		val code = """
			fun f(): List<Any> {
				return object : List<Any> {
					override val size: Int get() = TODO("not implemented")

					override fun contains(element: Any): Boolean {
						TODO("not implemented")
					}

					override fun containsAll(elements: Collection<Any>): Boolean {
						TODO("not implemented")
					}

					override fun get(index: Int): Any {
						TODO("not implemented")
					}

					override fun indexOf(element: Any): Int {
						TODO("not implemented")
					}

					override fun isEmpty(): Boolean {
						TODO("not implemented")
					}

					override fun iterator(): Iterator<Any> {
						TODO("not implemented")
					}

					override fun lastIndexOf(element: Any): Int {
						TODO("not implemented")
					}

					override fun listIterator(): ListIterator<Any> {
						TODO("not implemented")
					}

					override fun listIterator(index: Int): ListIterator<Any> {
						TODO("not implemented")
					}

					override fun subList(fromIndex: Int, toIndex: Int): List<Any> {
						TODO("not implemented")
					}
				}
			}
			""".trimIndent()
		it("does not report it") {
			subject.lint(code)
			assertThat(subject.findings).isEmpty()
		}
	}
})
