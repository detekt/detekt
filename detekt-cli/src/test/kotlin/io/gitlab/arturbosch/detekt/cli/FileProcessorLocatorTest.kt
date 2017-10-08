package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.gitlab.arturbosch.detekt.core.FileProcessorLocator
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import io.gitlab.arturbosch.detekt.test.resource
import org.assertj.core.api.Assertions
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
	fun containsAllProcessors() {
		val path = Paths.get(resource(""))
		val locator = FileProcessorLocator(ProcessingSettings(path))
		val processors = locator.load()
		val processorClasses = getProcessorClasses()

		assertThat(processorClasses).isNotEmpty
		processorClasses
				.filter { clazz -> processors.firstOrNull { clazz == it.javaClass } == null }
				.forEach { Assertions.fail("$it processor is not loaded by the FileProcessorLocator") }
	}

	private fun getProcessorClasses(): List<Class<out FileProcessListener>> {
		return Reflections(packageName)
				.getSubTypesOf(FileProcessListener::class.java)
				.filter { !Modifier.isAbstract(it.modifiers) }
	}
}
