package io.gitlab.arturbosch.detekt.core

import io.github.classgraph.ClassGraph
import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.gitlab.arturbosch.detekt.test.yamlConfigFromContent
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.Test
import java.lang.reflect.Modifier

/**
 * This tests the existence of all metric processors in the META-INF config file in the core package
 */
class FileProcessorLocatorSpec {

    private val path = resourceAsPath("")

    @Test
    fun `contains all processors`() {
        val processors = createProcessingSettings(path).use { FileProcessorLocator(it).load() }
        val processorClasses = getProcessorClasses()

        assertThat(processorClasses).isNotEmpty
        processorClasses
            .filter { clazz -> processors.none { clazz == it.javaClass } }
            .forEach { fail("$it processor is not loaded by the FileProcessorLocator") }
    }

    @Test
    fun `has disabled processors`() {
        val config = yamlConfigFromContent(
            """
                processors:
                  active: false
            """.trimIndent()
        )
        val processors = createProcessingSettings(path, config).use { FileProcessorLocator(it).load() }
        assertThat(processors).isEmpty()
    }
}

private fun getProcessorClasses(): List<Class<out FileProcessListener>> =
    ClassGraph()
        .acceptPackages("io.github.detekt.metrics.processors")
        .scan()
        .use { scanResult ->
            scanResult.getClassesImplementing(FileProcessListener::class.java)
                .loadClasses(FileProcessListener::class.java)
                .filter { !Modifier.isAbstract(it.modifiers) }
        }
