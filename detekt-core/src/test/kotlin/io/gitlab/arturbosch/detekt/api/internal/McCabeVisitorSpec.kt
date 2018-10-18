package io.gitlab.arturbosch.detekt.api.internal

import io.gitlab.arturbosch.detekt.test.KtTestCompiler
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

private const val FUN_MCC = 1

class McCabeVisitorSpec : Spek({

	given("ignoreSimpleWhenEntries is false") {

		it("counts simple when branches as 1") {
			val code = """
				fun test() {
					when (System.currentTimeMillis()) {
						0 -> println("Epoch!")
						1 -> println("1 past epoch.")
						else -> println("Meh")
					}
				}
			"""
			val subject = McCabeVisitor(ignoreSimpleWhenEntries = false)

			subject.visitFile(code.compile())

			assertThat(subject.mcc).isEqualTo(FUN_MCC + 3)
		}

		it("counts block when branches as 1") {
			val code = """
				fun test() {
					when (System.currentTimeMillis()) {
						0 -> {
							println("Epoch!")
						}
						1 -> println("1 past epoch.")
						else -> println("Meh")
					}
				}
			"""
			val subject = McCabeVisitor(ignoreSimpleWhenEntries = false)

			subject.visitFile(code.compile())

			assertThat(subject.mcc).isEqualTo(FUN_MCC + 3)
		}
	}

	given("ignoreSimpleWhenEntries is true") {

		it("counts a when with only simple branches as 1") {
			val code = """
				fun test() {
					when (System.currentTimeMillis()) {
						0 -> println("Epoch!")
						1 -> println("1 past epoch.")
						else -> println("Meh")
					}
				}
			"""
			val subject = McCabeVisitor(ignoreSimpleWhenEntries = true)

			subject.visitFile(code.compile())

			assertThat(subject.mcc).isEqualTo(FUN_MCC + 1)
		}

		it("does not count simple when branches") {
			val code = """
				fun test() {
					when (System.currentTimeMillis()) {
						0 -> {
							println("Epoch!")
							println("yay")
						}
						1 -> {
							println("1 past epoch!")
						}
						else -> println("Meh")
					}
				}
			"""
			val subject = McCabeVisitor(ignoreSimpleWhenEntries = true)

			subject.visitFile(code.compile())

			assertThat(subject.mcc).isEqualTo(FUN_MCC + 2)
		}

		it("counts block when branches as 1") {
			val subject = McCabeVisitor(ignoreSimpleWhenEntries = true)
			val code = """
				fun test() {
					when (System.currentTimeMillis()) {
						0 -> {
							println("Epoch!")
							println("yay!")
						}
						1 -> {
							println("1 past epoch.")
							println("yay?")
						}
						2 -> println("shrug")
						else -> println("Meh")
					}
				}
			"""

			subject.visitFile(code.compile())

			assertThat(subject.mcc).isEqualTo(FUN_MCC + 2)
		}
	}
})

private fun String.compile() = KtTestCompiler.compileFromContent(this.trimIndent())
