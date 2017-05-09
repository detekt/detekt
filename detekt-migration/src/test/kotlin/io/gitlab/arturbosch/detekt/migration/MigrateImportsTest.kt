package io.gitlab.arturbosch.detekt.migration

import io.gitlab.arturbosch.detekt.test.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

/**
 * @author Artur Bosch
 */
internal class MigrateImportsTest {

	@Disabled
	@Test
	fun migrate() {
		val ktFile = compileContentForTest(
				"""
package hello

import io.gitlab.arturbosch.detekt.migration
import hello.hello

fun main(args: Array<String>) {}
				"""
		)

		val expected = """
package hello

import io.gitlab.arturbosch.detekt.migration
import bye.bye

fun main(args: Array<String>) {}
				"""
		MigrateImports("hello.hello", "bye.bye").visit(ktFile)

		assertThat(ktFile.text).isEqualTo(expected)
	}
}