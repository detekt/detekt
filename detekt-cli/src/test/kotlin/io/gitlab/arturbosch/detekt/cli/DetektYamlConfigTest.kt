package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.YamlConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.reflections.Reflections
import java.io.File
import java.lang.reflect.Modifier
import java.nio.file.Paths

class DetektYamlConfigTest {

	private val config = loadConfig()

	@Test
	fun documentationSection() {
		ConfigAssert(
				"comments",
				"io.gitlab.arturbosch.detekt.rules.documentation",
				Rule::class.java
		).assert()
	}

	@Test
	fun complexitySection() {
		ConfigAssert(
				"complexity",
				"io.gitlab.arturbosch.detekt.rules.complexity",
				Rule::class.java
		).assert()
	}

	@Test
	fun exceptionsSection() {
		ConfigAssert(
				"exceptions",
				"io.gitlab.arturbosch.detekt.rules.exceptions",
				Rule::class.java
		).assert()
	}

	@Test
	fun performanceSection() {
		ConfigAssert(
				"performance",
				"io.gitlab.arturbosch.detekt.rules.performance",
				Rule::class.java
		).assert()
	}

	@Test
	fun potentialBugsSection() {
		ConfigAssert(
				"potential-bugs",
				"io.gitlab.arturbosch.detekt.rules.bugs",
				Rule::class.java
		).assert()
	}

	@Test
	fun styleSection() {
		ConfigAssert(
				"style",
				"io.gitlab.arturbosch.detekt.rules.style",
				Rule::class.java
		).assert()
	}

	private fun loadConfig(): Config {
		val workingDirectory = Paths.get(".").toAbsolutePath().normalize().toString()
		val file = File(workingDirectory + "/src/main/resources/default-detekt-config.yml")
		val url = file.toURI().toURL()
		return YamlConfig.loadResource(url)
	}

	private inner class ConfigAssert<T>
				(private val name: String,
				 private val packageName: String,
				 private val clazz: Class<T>) {

		fun assert() {
			val yamlDeclarations = getRuleConfig().properties.filter { it.key != "active" }
			assertThat(yamlDeclarations).isNotEmpty
			val classes = getClasses()
			assertThat(classes).isNotEmpty

			classes.map { c -> yamlDeclarations.keys.singleOrNull { it == c.simpleName } }
					.forEach { assertThat(it).isNotNull() }
		}

		private fun getRuleConfig() = config.subConfig(name) as YamlConfig

		private fun getClasses(): List<Class<out T>> {
			return Reflections(packageName)
					.getSubTypesOf(clazz)
					.filter { !Modifier.isAbstract(it.modifiers) }
		}
	}
}
