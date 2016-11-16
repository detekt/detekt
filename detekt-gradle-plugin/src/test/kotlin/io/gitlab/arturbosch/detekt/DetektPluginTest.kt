package io.gitlab.arturbosch.detekt

import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

/**
 * @author Artur Bosch
 */
class DetektPluginTest {

	@Test
	fun test() {
		val project = ProjectBuilder.builder().build()
		project.pluginManager.apply("io.gitlab.arturbosch.detekt")
		assertTrue(project.tasks.getAt("detekt") is DetektTask)
	}
}