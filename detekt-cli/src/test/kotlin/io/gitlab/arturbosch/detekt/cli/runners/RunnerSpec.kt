package io.gitlab.arturbosch.detekt.cli.runners

import io.gitlab.arturbosch.detekt.cli.BuildFailure
import io.gitlab.arturbosch.detekt.cli.CliArgs
import io.gitlab.arturbosch.detekt.cli.InvalidConfig
import io.gitlab.arturbosch.detekt.test.resource
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class RunnerSpec : Spek({

    val inputPath: Path = Paths.get(resource("/cases/Poko.kt"))

    describe("executes the runner with different maxIssues configurations") {

        it("should report one issue when maxIssues=2") {
            val tmpReport = Files.createTempFile("RunnerSpec", ".txt")
            val cliArgs = CliArgs.parse(arrayOf(
                "--input", inputPath.toString(),
                "--report", "txt:$tmpReport",
                "--config-resource", "/configs/max-issues-2.yml"
            ))

            Runner(cliArgs).execute()

            assertThat(Files.readAllLines(tmpReport)).hasSize(1)
        }

        it("should throw on maxIssues=0") {
            val cliArgs = CliArgs.parse(arrayOf(
                "--input", inputPath.toString(),
                "--config-resource", "/configs/max-issues-0.yml"
            ))

            assertThatThrownBy { Runner(cliArgs).execute() }.isExactlyInstanceOf(BuildFailure::class.java)
        }

        it("should throw on invalid config property") {
            val cliArgs = CliArgs.parse(arrayOf(
                "--input", inputPath.toString(),
                "--config-resource", "/configs/invalid-config.yml"
            ))

            assertThatThrownBy { Runner(cliArgs).execute() }.isExactlyInstanceOf(InvalidConfig::class.java)
        }

        it("should never throw on maxIssues=-1") {
            val tmpReport = Files.createTempFile("RunnerSpec", ".txt")
            val cliArgs = CliArgs.parse(arrayOf(
                "--input", inputPath.toString(),
                "--report", "txt:$tmpReport",
                "--config-resource", "/configs/max-issues--1.yml"
            ))

            Runner(cliArgs).execute()

            assertThat(Files.readAllLines(tmpReport)).hasSize(1)
        }
    }

    describe("executes the runner with create baseline") {

        it("should not throw on maxIssues=0") {
            val tmpReport = Files.createTempFile("RunnerSpec", ".txt")
            val cliArgs = CliArgs.parse(arrayOf(
                "--input", inputPath.toString(),
                "--create-baseline",
                "--report", "txt:$tmpReport",
                "--config-resource", "/configs/max-issues-0.yml"
            ))

            Runner(cliArgs).execute()

            assertThat(Files.readAllLines(tmpReport)).hasSize(1)
        }
    }
})
