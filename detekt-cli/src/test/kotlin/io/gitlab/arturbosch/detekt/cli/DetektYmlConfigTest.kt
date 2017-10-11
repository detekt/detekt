package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.YamlConfig
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Paths

class DetektYmlConfigTest {

	private val config = loadConfig()

	@Test
	fun complexitySection() {
		ConfigAssert(
				config,
				"complexity",
				"io.gitlab.arturbosch.detekt.rules.complexity"
		).assert()
	}

	@Test
	fun documentationSection() {
		ConfigAssert(
				config,
				"comments",
				"io.gitlab.arturbosch.detekt.rules.documentation"
		).assert()
	}

	@Test
	fun emptyBlocksSection() {
		ConfigAssert(
				config,
				"empty-blocks",
				"io.gitlab.arturbosch.detekt.rules.empty"
		).assert()
	}

	@Test
	fun exceptionsSection() {
		ConfigAssert(
				config,
				"exceptions",
				"io.gitlab.arturbosch.detekt.rules.exceptions"
		).assert()
	}

	@Test
	fun performanceSection() {
		ConfigAssert(
				config,
				"performance",
				"io.gitlab.arturbosch.detekt.rules.performance"
		).assert()
	}

	@Test
	fun potentialBugsSection() {
		ConfigAssert(
				config,
				"potential-bugs",
				"io.gitlab.arturbosch.detekt.rules.bugs"
		).assert()
	}

	@Test
	fun styleSection() {
		ConfigAssert(
				config,
				"style",
				"io.gitlab.arturbosch.detekt.rules.style"
		).assert()
	}

	private fun loadConfig(): Config {
		val workingDirectory = Paths.get(".").toAbsolutePath().normalize().toString()
		val file = File(workingDirectory + "/src/main/resources/$CONFIG_FILE")
		val url = file.toURI().toURL()
		return YamlConfig.loadResource(url)
	}
}

internal const val CONFIG_FILE = "default-detekt-config.yml"
