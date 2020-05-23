package io.gitlab.arturbosch.detekt.cli

import com.beust.jcommander.ParameterException
import io.github.detekt.test.utils.NullPrintStream
import io.github.detekt.test.utils.resource
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Path
import java.nio.file.Paths

internal class CliArgsSpec : Spek({

    val projectPath = Paths.get(resource("/empty.txt")).parent.parent.parent.parent.toAbsolutePath()

    describe("Parsing the input path") {

        it("the current working directory is used if parameter is not set") {
            val cli = parseArguments(emptyArray(), NullPrintStream(), NullPrintStream())
            assertThat(cli.inputPaths).hasSize(1)
            assertThat(cli.inputPaths.first()).isEqualTo(Paths.get(System.getProperty("user.dir")))
        }

        it("a single value is converted to a path") {
            val cli = parseArguments(
                arrayOf("--input", "$projectPath"),
                NullPrintStream(),
                NullPrintStream())
            assertThat(cli.inputPaths).hasSize(1)
            assertThat(cli.inputPaths.first().toAbsolutePath()).isEqualTo(projectPath)
        }

        it("multiple input paths can be separated by comma") {
            val mainPath = projectPath.resolve("src/main").toAbsolutePath()
            val testPath = projectPath.resolve("src/test").toAbsolutePath()
            val cli = parseArguments(
                arrayOf("--input", "$mainPath,$testPath"),
                NullPrintStream(),
                NullPrintStream())
            assertThat(cli.inputPaths).hasSize(2)
            assertThat(cli.inputPaths.map(Path::toAbsolutePath)).containsExactlyInAnyOrder(mainPath, testPath)
        }

        it("reports an error if the input path does not exist") {
            val pathToNonExistentDirectory = projectPath.resolve("nonExistent")
            val params = arrayOf("--input", "$pathToNonExistentDirectory")

            assertThatExceptionOfType(ParameterException::class.java)
                .isThrownBy { parseArguments(params, NullPrintStream(), NullPrintStream()).inputPaths }
                .withMessageContaining("does not exist")
        }
    }

    describe("Valid combination of options for baseline feature") {

        fun fixture(args: Array<String>) = parseArguments(args, NullPrintStream(), NullPrintStream())

        it("reports an error when using --create-baseline without a --baseline file") {
            assertThatExceptionOfType(HandledArgumentViolation::class.java)
                .isThrownBy { fixture(arrayOf("--create-baseline")) }
        }

        it("reports an error when using --baseline file does not exist") {
            val pathToNonExistentDirectory = projectPath.resolve("nonExistent").toString()
            assertThatExceptionOfType(HandledArgumentViolation::class.java)
                .isThrownBy { fixture(arrayOf("--baseline", pathToNonExistentDirectory)) }
        }

        it("reports an error when using --baseline file which is not a file") {
            val directory = Paths.get(resource("/cases")).toString()
            assertThatExceptionOfType(HandledArgumentViolation::class.java)
                .isThrownBy { fixture(arrayOf("--baseline", directory)) }
        }
    }
})
