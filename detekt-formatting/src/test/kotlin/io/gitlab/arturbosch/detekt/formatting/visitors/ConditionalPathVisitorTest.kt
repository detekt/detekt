package io.gitlab.arturbosch.detekt.formatting.visitors

import io.gitlab.arturbosch.detekt.formatting.ExpressionBodySyntax
import io.gitlab.arturbosch.detekt.test.compileContentForTest
import io.gitlab.arturbosch.detekt.test.format
import io.gitlab.arturbosch.detekt.test.resourceAsString
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * @author Artur Bosch
 */
internal class ConditionalPathVisitorTest {

	@Test
	fun test() {
		var counter = 0

		val visitor = ConditionalPathVisitor {
			counter += 1
		}

		val content = resourceAsString("cases/ConditionalReturns.kt")
		val ktFile = compileContentForTest(content)

		ktFile.accept(visitor)

		assertThat(counter).isEqualTo(5)
	}


	@Test
	fun removeAllNestedReturns() {
		assertThat(ExpressionBodySyntax().format(actual)).isEqualTo(expected)
	}
}

val actual = """
fun stuff(): Int {
	return try {
		return if (true) {
			if (false) return -1
			return 5
		} else {
			5
			return try {
				"5".toInt()
			} catch (e: IllegalArgumentException) {
				5
			} catch (e: RuntimeException) {
				3
				return 5
			}
		}
	} catch (e: Exception) {
		when(5) {
			5 -> return 1
			2 -> return 1
			else -> 5
		}
		return 7
	}
}""".trimIndent()

val expected = """
fun stuff(): Int = try {
		if (true) {
			if (false) return -1
			5
		} else {
			5
			try {
				"5".toInt()
			} catch (e: IllegalArgumentException) {
				5
			} catch (e: RuntimeException) {
				3
				5
			}
		}
	} catch (e: Exception) {
		when(5) {
			5 -> return 1
			2 -> return 1
			else -> 5
		}
		7
	}""".trimIndent()