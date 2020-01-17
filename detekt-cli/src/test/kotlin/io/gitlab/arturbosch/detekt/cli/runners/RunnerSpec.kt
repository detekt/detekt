package io.gitlab.arturbosch.detekt.cli.runners

import io.gitlab.arturbosch.detekt.cli.BuildFailure
import io.gitlab.arturbosch.detekt.cli.CliArgs
import io.gitlab.arturbosch.detekt.cli.InvalidConfig
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

    describe("printers") {

        val outputPrinterBuffer by memoized { ByteArrayOutputStream() }
        val outputPrinter by memoized { PrintStream(outputPrinterBuffer) }

        val errorPrinterBuffer by memoized { ByteArrayOutputStream() }
        val errorPrinter by memoized { PrintStream(errorPrinterBuffer) }

        context("execute with default config") {

            beforeEachTest {
                val args = CliArgs.parse(arrayOf("--input", inputPath.toString()))

                Runner(args, outputPrinter, errorPrinter).execute()

                outputPrinter.flush()
                outputPrinter.close()

                errorPrinter.flush()
                errorPrinter.close()
            }

            it("does not write any output when errors do not exist") {
                assertThat(outputPrinterBuffer.toString(Charset.defaultCharset().name())).isEmpty()
            }

            it("does not write anything to error printer") {
                assertThat(errorPrinterBuffer.toString(Charset.defaultCharset().name())).isEmpty()
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
                assertThat(outputPrinterBuffer.toString(Charset.defaultCharset().name())).contains("Build failed")
            }

            it("does not write anything to error printer") {
                assertThat(errorPrinterBuffer.toString(Charset.defaultCharset().name())).isEmpty()
            }
        }
    }

    describe("executes the runner with verbose logs and no errors") {

        val outputPrinterBuffer by memoized { ByteArrayOutputStream() }
        val outputPrinter by memoized { PrintStream(outputPrinterBuffer) }

        val errorPrinterBuffer by memoized { ByteArrayOutputStream() }
        val errorPrinter by memoized { PrintStream(errorPrinterBuffer) }

        context("execute with verbose logs") {

            beforeEachTest {
                val args = CliArgs.parse(arrayOf("--input", inputPath.toString(), "--verbose"))

                Runner(args, outputPrinter, errorPrinter).execute()

                outputPrinter.flush()
                outputPrinter.close()

                errorPrinter.flush()
                errorPrinter.close()
            }

            it("writes output to output printer, asserts logs present") {
                assertThat(outputPrinterBuffer.toString(Charset.defaultCharset().name())).contains("Build succeeded")
            }

            it("does not write anything to error printer") {
                assertThat(errorPrinterBuffer.toString(Charset.defaultCharset().name())).isEmpty()
            }
        }
    }
})
