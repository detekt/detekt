package io.gitlab.arturbosch.detekt.api.internal

import io.gitlab.arturbosch.detekt.test.KtTestCompiler
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

class McCabeVisitorSpec : Spek({

	given("ignoreSimpleWhenEntries is false") {
		val subject = McCabeVisitor(ignoreSimpleWhenEntries = false)

		it("counts simple when branches as 1") {
			val code = """
				when (value) {
					is Int -> bundle.putInt(transformedKey, value)
					is String -> bundle.putString(transformedKey, value)
					is Float -> bundle.putFloat(transformedKey, value)
					is Double -> bundle.putDouble(transformedKey, value)
					is Byte -> bundle.putByte(transformedKey, value)
					is Short -> bundle.putShort(transformedKey, value)
					is Long -> bundle.putLong(transformedKey, value)
					is Boolean -> bundle.putBoolean(transformedKey, value)
					else -> {
						log("Unexpected type value")
						throw IllegalArgumentException("Unexpected type value")
					}
				}
			"""

			subject.visitFile(code.compile())

			assertThat(subject.mcc).isEqualTo(1)
		}
	}

	given("ignoreSimpleWhenEntries is true") {
		val subject = McCabeVisitor(ignoreSimpleWhenEntries = true)

		it("does not count simple when branches") {
			val code = """
				when (value) {
					is Int -> bundle.putInt(transformedKey, value)
					is String -> bundle.putString(transformedKey, value)
					is Float -> bundle.putFloat(transformedKey, value)
					is Double -> bundle.putDouble(transformedKey, value)
					is Byte -> bundle.putByte(transformedKey, value)
					is Short -> bundle.putShort(transformedKey, value)
					is Long -> {
						log("I like long integers")
						bundle.putLong(transformedKey, value)
					}
					is Boolean -> bundle.putBoolean(transformedKey, value)
					else -> {
						log("Unexpected type value")
						throw IllegalArgumentException("Unexpected type value")
					}
				}
			"""

			subject.visitFile(code.compile())

			assertThat(subject.mcc).isEqualTo(2)
		}

		it("counts a when expression with only simple entries as 1") {
			val code = """
				when (value) {
					is Int -> bundle.putInt(transformedKey, value)
					is String -> bundle.putString(transformedKey, value)
					is Float -> bundle.putFloat(transformedKey, value)
					is Double -> bundle.putDouble(transformedKey, value)
					is Byte -> bundle.putByte(transformedKey, value)
					is Short -> bundle.putShort(transformedKey, value)
					is Long -> bundle.putLong(transformedKey, value)
					is Boolean -> bundle.putBoolean(transformedKey, value)
					else -> throw IllegalArgumentException("Unexpected type value")
				}
			"""

			subject.visitFile(code.compile())

			assertThat(subject.mcc).isEqualTo(1)
		}
	}
})

private fun String.compile() = KtTestCompiler.compileFromContent(this.trimIndent())
