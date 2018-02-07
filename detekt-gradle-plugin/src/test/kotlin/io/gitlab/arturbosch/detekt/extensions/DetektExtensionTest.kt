package io.gitlab.arturbosch.detekt.extensions

import com.beust.jcommander.JCommander
import io.gitlab.arturbosch.detekt.cli.Args
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.nio.file.Paths

/**
 * @author Artur Bosch
 */
internal class DetektExtensionTest : Spek({

	describe("parsing profiles") {

		val project = ProjectBuilder.builder().build()
		project.extensions.create("detekt", DetektExtension::class.java)

		it("should use the fallback arguments if no profile is specified") {
			val detektExtension = DetektExtension()
			val args = detektExtension.resolveArguments(project)

			val main = Args().apply { JCommander(this).parse(*args.toTypedArray()) }

			assertThat(main.inputPath).isEqualTo(listOf(Paths.get(project.projectDir.toString())))
			assertThat(main.configResource).isEqualTo(DEFAULT_DETEKT_CONFIG_RESOURCE)
			assertThat(main.config).isEqualTo(null)
			assertThat(main.baseline).isEqualTo(null)
			assertThat(main.createBaseline).isEqualTo(false)
			assertThat(main.plugins).isEqualTo(null)
		}

	}
})
