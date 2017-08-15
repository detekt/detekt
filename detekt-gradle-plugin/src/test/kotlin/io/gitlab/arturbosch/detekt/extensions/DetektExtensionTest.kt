package io.gitlab.arturbosch.detekt.extensions

import com.beust.jcommander.JCommander
import io.gitlab.arturbosch.detekt.cli.Args
import org.assertj.core.api.Assertions
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

			val main = Args().apply { JCommander(this).parse(*args.toTypedArray()) }
			val fallback = Args().apply { JCommander(this).parse(*project.fallbackArguments().toTypedArray()) }

			Assertions.assertThat(main.inputPath).isEqualTo(fallback.inputPath)
			Assertions.assertThat(main.configResource).isEqualTo(fallback.configResource)
			Assertions.assertThat(main.config).isEqualTo(fallback.config)
			Assertions.assertThat(main.baseline).isEqualTo(fallback.baseline)
			Assertions.assertThat(main.createBaseline).isEqualTo(fallback.createBaseline)
			Assertions.assertThat(main.plugins).isEqualTo(fallback.plugins)
		}

	}
})
