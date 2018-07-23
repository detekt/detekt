package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class MayBeConstSpec : SubjectSpek<MayBeConst>({

	subject { MayBeConst() }

	given("some valid constants") {
		it("is a valid constant") {
			val code = """const val X = 42"""
			subject.lint(code)
			assertThat(subject.findings).isEmpty()
		}

		it("is const vals in object") {
			val code = """
				object Test {
					const val TEST = "Test"
				}
				"""
			subject.lint(code)
			assertThat(subject.findings).isEmpty()
		}

		it("isconst vals in companion objects") {
			val code = """
				class Test {
					companion object {
						const val B = 1
					}
				}
				"""
			subject.lint(code)
			assertThat(subject.findings).isEmpty()
		}

		it("does not report const vals that use other const vals") {
			val code = """
				const val a = 0

				class Test {
					companion object {
						@JvmField
						const val B = a + 1
					}
				}
				"""
			subject.lint(code)
			assertThat(subject.findings).isEmpty()
		}

		it("does not report none const val candidates") {
			val code = """
				const val a = 0
				val p = Pair(a, a + a)
				val p2 = emptyList<Int>().plus(a)
				"""
			subject.lint(code)
			assertThat(subject.findings).isEmpty()
		}
	}

	given("some vals that could be constants") {
		it("is a simple val") {
			val code = """
				val x = 1
				"""
			subject.lint(code)
			assertThat(subject.findings).hasSize(1)
		}

		it("is a simple JvmField val") {
			val code = """
				@JvmField val x = 1
				"""
			subject.lint(code)
			assertThat(subject.findings).hasSize(1)
		}

		it("is a field in an object") {
			val code = """
				object Test {
    				@JvmField val test = "Test"
				}
				"""
			subject.lint(code)
			assertThat(subject.findings).hasSize(1)
		}

		it("reports vals in companion objects") {
			val code = """
				class Test {
					companion object {
						val b = 1
					}
				}
				"""
			subject.lint(code)
			assertThat(subject.findings).hasSize(1)
		}
	}

	given("vals that can be constants but detekt doesn't handle yet") {
		it("is a constant binary expression") {
			val code = """
				const val one = 1
				val two = one * 2
				"""
			subject.lint(code)
			assertThat(subject.findings).hasSize(1)
		}

		it("is a constant binary expression in a companion object") {
			val code = """
				class Test {
					companion object {
						const val one = 1
						val two = one * 2
					}
				}
				"""
			subject.lint(code)
			assertThat(subject.findings).hasSize(1)
		}

		it("is a nested constant binary expression") {
			val code = """
				const val one = 1
				val two = one * 2 + 1
				"""
			subject.lint(code)
			assertThat(subject.findings).hasSize(1)
		}

		it("is a nested constant parenthesised expression") {
			val code = """
				const val one = 1
				val two = one * (2 + 1)
				"""
			subject.lint(code)
			assertThat(subject.findings).hasSize(1)
		}

		it("reports vals that use other const vals") {
			val code = """
				const val a = 0

				class Test {
					companion object {
						@JvmField
						val b = a + 1
					}
				}
				"""
			subject.lint(code)
			assertThat(subject.findings).hasSize(1)
		}

		it("reports concatenated string vals") {
			val code = """
				private const val A = "a"
				private val B = A + "b"
				"""
			subject.lint(code)
			assertThat(subject.findings).hasSize(1)
		}
	}

	given("vals that cannot be constants") {
		it("does not report arrays") {
			val code = "val arr = arrayOf(\"a\", \"b\")"
			subject.lint(code)
			assertThat(subject.findings).isEmpty()
		}

		it("is a var") {
			val code = "var test = 1"
			subject.lint(code)
			assertThat(subject.findings).isEmpty()
		}

		it("has a getter") {
			val code = "val withGetter get() = 42"
			subject.lint(code)
			assertThat(subject.findings).isEmpty()
		}

		it("is initialized to null") {
			val code = "val test = null"
			subject.lint(code)
			assertThat(subject.findings).isEmpty()
		}

		it("is a JvmField in a class") {
			val code = """
				class Test {
					@JvmField val a = 3
				}
			""".trimMargin()
			subject.lint(code)
			assertThat(subject.findings).isEmpty()
		}

		it("has some annotation") {
			val code = """
				annotation class A

				@A val a = 55
			""".trimMargin()
			subject.lint(code)
			assertThat(subject.findings).isEmpty()
		}

		it("overrides something") {
			val code = """
				interface Base {
					val property: Int
				}

				object Derived : Base {
					override val property = 1
				}
			""".trimMargin()
			subject.lint(code)
			assertThat(subject.findings).isEmpty()
		}

		it("does not detect just a dollar as interpolation") {
			val code = """ val hasDollar = "$" """
			subject.lint(code)
			assertThat(subject.findings).hasSize(1)
		}

		it("does not report interpolated strings") {
			subject.lint(Case.MayBeConstNegative.path())
			assertThat(subject.findings).isEmpty()
		}
	}

	given("some const val candidates in nested objects") {

		it("reports the const val candidates") {
			val path = Case.ConstInObjects.path()
			subject.lint(path)
			assertThat(subject.findings).hasSize(3)
		}
	}
})
