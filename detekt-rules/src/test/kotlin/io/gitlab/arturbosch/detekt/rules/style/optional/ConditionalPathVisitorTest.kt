package io.gitlab.arturbosch.detekt.rules.style.optional

import io.gitlab.arturbosch.detekt.test.compileContentForTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

/**
 * @author Artur Bosch
 */
internal class ConditionalPathVisitorTest {

	@Test
	fun isState() {
		var counter = 0

		val visitor = ConditionalPathVisitor {
			counter++
		}

		val ktFile = compileContentForTest(isState)

		ktFile.accept(visitor)

		Assertions.assertThat(counter).isEqualTo(5)
	}

	@Test
	fun shouldState() {
		var counter = 0

		val visitor = ConditionalPathVisitor {
			counter++
		}

		val ktFile = compileContentForTest(shouldState)

		ktFile.accept(visitor)

		Assertions.assertThat(counter).isEqualTo(0)
	}
}

val isState = """
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
val shouldState = """
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
