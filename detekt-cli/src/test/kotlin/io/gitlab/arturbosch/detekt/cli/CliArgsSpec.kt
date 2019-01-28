package io.gitlab.arturbosch.detekt.cli

import com.beust.jcommander.ParameterException
import io.gitlab.arturbosch.detekt.test.resource
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.nio.file.Path
import java.nio.file.Paths

internal class CliArgsSpec : Spek({

	val projectPath = Paths.get(resource("/empty.txt")).parent.parent.parent.parent.toAbsolutePath()

	describe("Parsing the input path") {

		it("the current working directory is used if parameter is not set") {
			val (cli, _) = parseArguments<CliArgs>(emptyArray())
			assertThat(cli.inputPaths).hasSize(1)
			assertThat(cli.inputPaths.first()).isEqualTo(Paths.get(System.getProperty("user.dir")))
		}

		it("a single value is converted to a path") {
			val (cli, _) = parseArguments<CliArgs>(arrayOf("--input", "$projectPath"))
			assertThat(cli.inputPaths).hasSize(1)
			assertThat(cli.inputPaths.first().toAbsolutePath()).isEqualTo(projectPath)
		}

		it("multiple imput paths can be separated by comma") {
			val mainPath = projectPath.resolve("src/main").toAbsolutePath()
			val testPath = projectPath.resolve("src/test").toAbsolutePath()
			val (cli, _) = parseArguments<CliArgs>(arrayOf(
					"--input", "$mainPath,$testPath")
			)
			assertThat(cli.inputPaths).hasSize(2)
			assertThat(cli.inputPaths.map(Path::toAbsolutePath)).containsExactlyInAnyOrder(mainPath, testPath)
		}

		it("reports an error if the input path does not exist") {
			val pathToNonExistentDirectory = projectPath.resolve("nonExistent")
			val params = arrayOf("--input", "$pathToNonExistentDirectory")

			assertThatExceptionOfType(ParameterException::class.java)
					.isThrownBy { parseArguments<CliArgs>(params).first.inputPaths }
					.withMessageContaining("does not exist")
		}
	}
})
