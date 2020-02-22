package io.gitlab.arturbosch.detekt.cli.runners

import io.gitlab.arturbosch.detekt.cli.BuildFailure
import io.gitlab.arturbosch.detekt.cli.CliArgs
import io.gitlab.arturbosch.detekt.cli.config.InvalidConfig
import io.gitlab.arturbosch.detekt.test.resource
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class RunnerSpec : Spek({

    val inputPath: Path = Paths.get(resource("cases/Poko.kt"))
    val charSetName = Charset.defaultCharset().name()

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

        context("with additional baseline file") {

            it("should not throw on maxIssues=0 due to baseline blacklist") {
                val tmpReport = Files.createTempFile("RunnerSpec", ".txt")
                val cliArgs = CliArgs.parse(arrayOf(
                    "--input", inputPath.toString(),
                    "--report", "txt:$tmpReport",
                    "--config-resource", "/configs/max-issues-0.yml",
                    "--baseline", Paths.get(resource("configs/baseline-with-two-excludes.xml")).toString()
                ))

                Runner(cliArgs).execute()

                assertThat(Files.readAllLines(tmpReport)).isEmpty()
            }
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

    describe("customize output and error printers") {

        val outputPrinterBuffer by memoized { ByteArrayOutputStream() }
        val outputPrinter by memoized { PrintStream(outputPrinterBuffer) }

        val errorPrinterBuffer by memoized { ByteArrayOutputStream() }
        val errorPrinter by memoized { PrintStream(errorPrinterBuffer) }

        context("execute with default config which allows no issues") {

            val path: Path = Paths.get(resource("/cases/CleanPoko.kt"))

            beforeEachTest {
                val args = CliArgs.parse(arrayOf("--input", path.toString()))

                Runner(args, outputPrinter, errorPrinter).execute()

                outputPrinter.flush()
                outputPrinter.close()

                errorPrinter.flush()
                errorPrinter.close()
            }

            it("writes no build related output to output printer") {
                assertThat(outputPrinterBuffer.toString(charSetName)).doesNotContain("test - [Poko]")
            }

            it("does not write anything to error printer") {
                assertThat(errorPrinterBuffer.toString(charSetName)).isEmpty()
            }
        }

        context("execute with strict config") {

            beforeEachTest {
                val args = CliArgs.parse(
                    arrayOf("--input", inputPath.toString(), "--config-resource", "/configs/max-issues-0.yml")
                )

                try {
                    Runner(args, outputPrinter, errorPrinter).execute()
                } catch (ignored: BuildFailure) {
                }

                outputPrinter.flush()
                outputPrinter.close()

                errorPrinter.flush()
                errorPrinter.close()
            }

            it("writes output to output printer") {
                assertThat(outputPrinterBuffer.toString(charSetName)).contains("test - [Poko]")
            }

            it("does not write anything to error printer") {
                assertThat(errorPrinterBuffer.toString(charSetName)).isEmpty()
            }
        }
    }
})
