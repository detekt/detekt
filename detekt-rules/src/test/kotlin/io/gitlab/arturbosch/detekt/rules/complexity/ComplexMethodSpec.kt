package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.isThresholded
import io.gitlab.arturbosch.detekt.test.lint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * @author Artur Bosch
 */
class ComplexMethodSpec : Spek({
    describe("ComplexMethod rule") {

        context("a complex method") {

            it("finds one complex method") {
                val subject = ComplexMethod()
                subject.lint(Case.ComplexClass.path())

                assertThat(subject.findings).hasSourceLocations(SourceLocation(3, 1))

                assertThat(subject.findings.first())
                        .isThresholded()
                        .withValue(20)
                        .withThreshold(10)
            }
        }

        context("several complex methods") {

            val path = Case.ComplexMethods.path()

            it("does not report complex methods with a single when expression") {
                val config = TestConfig(mapOf(
                        ComplexMethod.IGNORE_SIMPLE_WHEN_ENTRIES to "1.0",
                        ComplexMethod.IGNORE_SINGLE_WHEN_EXPRESSION to "true"))
                val subject = ComplexMethod(config, threshold = 4)

                assertThat(subject.lint(path)).hasSourceLocations(SourceLocation(42, 1))
            }

            it("reports all complex methods") {
                val config = TestConfig(mapOf(ComplexMethod.IGNORE_SIMPLE_WHEN_ENTRIES to "1.0"))
                val subject = ComplexMethod(config, threshold = 4)

                assertThat(subject.lint(path)).hasSourceLocations(
                        SourceLocation(5, 1),
                        SourceLocation(14, 1),
                        SourceLocation(24, 1),
                        SourceLocation(34, 1),
                        SourceLocation(42, 1)
                )
            }

            it("does not trip for a reasonable amount of simple when entries when ignoreSimpleWhenEntries is true") {
                val config = TestConfig(mapOf(ComplexMethod.IGNORE_SIMPLE_WHEN_ENTRIES to "true"))
                val subject = ComplexMethod(config)
                val code = """
			    	 fun f() {
                        val map = HashMap<Any, String>()
                        for ((key, value) in map) {
                            when (key) {
                                is Int -> print("int")
                                is String -> print("String")
                                is Float -> print("Float")
                                is Double -> print("Double")
                                is Byte -> print("Byte")
                                is Short -> print("Short")
                                is Long -> print("Long")
                                is Boolean -> print("Boolean")
                                else -> throw IllegalArgumentException("Unexpected type value")
                            }
                        }
                    }
			    """

                val findings = subject.compileAndLint(code)
                assertThat(findings).isEmpty()
            }
        }

        context("function containing object literal with many overridden functions") {

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

            it("should not count these overridden functions to base functions complexity") {
                assertThat(ComplexMethod().compileAndLint(code)).isEmpty()
            }
        }
    }
})
