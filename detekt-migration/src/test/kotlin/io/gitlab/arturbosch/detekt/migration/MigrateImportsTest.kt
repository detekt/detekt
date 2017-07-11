package io.gitlab.arturbosch.detekt.migration

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * @author Artur Bosch
 */
internal class MigrateImportsTest {

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

		MigrateImportsRule(MigrationTestConfig).visit(ktFile)

		assertThat(ktFile.text).isEqualTo(expected)
	}
}

object MigrationTestConfig : Config {

	private val map = hashMapOf("imports" to hashMapOf("hello.hello" to "bye.bye"))

	override fun subConfig(key: String): Config {
		if (key == "migration") return this else return Config.empty
	}

	override fun <T : Any> valueOrDefault(key: String, default: T): T {
		@Suppress("UNCHECKED_CAST")
		return map[key] as T? ?: default
	}

}
