package io.gitlab.arturbosch.detekt.extensions

import org.assertj.core.api.Assertions.assertThat
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
		project.extensions.create("detekt", DetektExtension::class.java)

		it("should use the fallback arguments if no profile is specified") {
			val detektExtension = DetektExtension()
			val expectedArgs = listOf("--config-resource",
					"/default-detekt-config.yml",
					"--filters",
					".*/resources/.*,.*/build/.*,.*/target/.*",
					"--input",
					project.projectDir.toString())
			val args = detektExtension.resolveArguments(project)

			assertThat(args).isEqualTo(expectedArgs)
		}

	}
})
