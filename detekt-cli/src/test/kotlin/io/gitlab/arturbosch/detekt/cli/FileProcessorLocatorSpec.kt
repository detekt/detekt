package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.gitlab.arturbosch.detekt.core.FileProcessorLocator
import io.gitlab.arturbosch.detekt.test.createProcessingSettings
import io.gitlab.arturbosch.detekt.test.resource
import io.gitlab.arturbosch.detekt.test.yamlConfig
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.reflections.Reflections
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.lang.reflect.Modifier
import java.nio.file.Paths

/**
 * This tests the existence of all metric processors in the META-INF config file in the core package
 */
class FileProcessorLocatorSpec : Spek({

    describe("file processor locator") {

        val path = Paths.get(resource(""))

        it("contains all processors") {
            val processors = createProcessingSettings(path).use { FileProcessorLocator(it).load() }
            val processorClasses = getProcessorClasses()

            assertThat(processorClasses).isNotEmpty
            processorClasses
                    .filter { clazz -> processors.firstOrNull { clazz == it.javaClass } == null }
                    .forEach { fail("$it processor is not loaded by the FileProcessorLocator") }
        }

        it("has disabled processors") {
            val config = yamlConfig("configs/disabled-processors.yml")
            val processors = createProcessingSettings(path, config).use { FileProcessorLocator(it).load() }
            assertThat(processors).isEmpty()
        }
    }
})

private fun getProcessorClasses(): List<Class<out FileProcessListener>> {
    return Reflections("io.gitlab.arturbosch.detekt.core.processors")
            .getSubTypesOf(FileProcessListener::class.java)
            .filter { !Modifier.isAbstract(it.modifiers) }
}
