package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.gitlab.arturbosch.detekt.core.FileProcessorLocator
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import io.gitlab.arturbosch.detekt.test.resource
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.reflections.Reflections
import java.lang.reflect.Modifier
import java.nio.file.Paths

/**
 * This tests the existence of all metric processors in the META-INF config file in the core package
 */
class FileProcessorLocatorTest {

	private val packageName = "io.gitlab.arturbosch.detekt.core.processors"

	@Test
	fun containsAllRuleProviders() {
		val path = Paths.get(resource(""))
		val locator = FileProcessorLocator(ProcessingSettings(path))
		val providers = locator.load()
		val classes = getClasses()

		assertThat(classes).isNotEmpty
		classes
				.map { c -> providers.firstOrNull { c == it.javaClass } }
				.forEach { assertThat(it).isNotNull() }
	}

	private fun getClasses(): List<Class<out FileProcessListener>> {
		return Reflections(packageName)
				.getSubTypesOf(FileProcessListener::class.java)
				.filter { !Modifier.isAbstract(it.modifiers) }
	}
}
