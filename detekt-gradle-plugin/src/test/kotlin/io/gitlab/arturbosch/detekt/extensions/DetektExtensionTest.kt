package io.gitlab.arturbosch.detekt.extensions

import com.beust.jcommander.JCommander
import io.gitlab.arturbosch.detekt.cli.Main
import org.assertj.core.api.Assertions
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

/**
 * @author Artur Bosch
 */
internal class DetektExtensionTest : Spek({

	describe("parsing profiles") {

		val project = ProjectBuilder.builder().build()
		project.extensions.create("detekt", DetektExtension::class.java, *arrayOf())

		it("should use the fallback arguments if no profile is specified") {
			val detektExtension = DetektExtension()
			val args = detektExtension.profileArgumentsOrDefault(project)

			val main = Main().apply { JCommander(this).parse(*args.toTypedArray()) }
			val fallback = Main().apply { JCommander(this).parse(*project.fallbackArguments().toTypedArray()) }

			Assertions.assertThat(main.projectPath).isEqualTo(fallback.projectPath)
			Assertions.assertThat(main.configResource).isEqualTo(fallback.configResource)
			Assertions.assertThat(main.config).isEqualTo(fallback.config)
			Assertions.assertThat(main.baseline).isEqualTo(fallback.baseline)
			Assertions.assertThat(main.createBaseline).isEqualTo(fallback.createBaseline)
			Assertions.assertThat(main.rules).isEqualTo(fallback.rules)
		}

	}
})

// TODO remove after https://youtrack.jetbrains.com/issue/KT-16497
internal fun Project.fallbackArguments() = listOf(
		PROJECT_PARAMETER, projectDir.absolutePath,
		CONFIG_RESOURCE_PARAMETER, DEFAULT_DETEKT_CONFIG_RESOURCE,
		FILTERS_PARAMETER, DEFAULT_PATH_EXCLUDES)
