package io.gitlab.arturbosch.detekt.cli.runners

import io.gitlab.arturbosch.detekt.cli.BuildFailure
import io.gitlab.arturbosch.detekt.test.StringPrintStream
import io.gitlab.arturbosch.detekt.cli.config.InvalidConfig
import io.gitlab.arturbosch.detekt.cli.createCliArgs
import io.gitlab.arturbosch.detekt.test.NullPrintStream
import io.gitlab.arturbosch.detekt.test.resource
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class RunnerSpec : Spek({

    val inputPath: Path = Paths.get(resource("cases/Poko.kt"))

    describe("executes the runner with different maxIssues configurations") {

        it("should report one issue when maxIssues=2") {
            val tmpReport = Files.createTempFile("RunnerSpec", ".txt")
            val cliArgs = createCliArgs(
                "--input", inputPath.toString(),
                "--report", "txt:$tmpReport",
                "--config-resource", "/configs/max-issues-2.yml"
            )

            Runner(cliArgs, NullPrintStream(), NullPrintStream()).execute()

            assertThat(Files.readAllLines(tmpReport)).hasSize(1)
        }

        it("should throw on maxIssues=0") {
            val cliArgs = createCliArgs(
                "--input", inputPath.toString(),
                "--config-resource", "/configs/max-issues-0.yml"
            )

            assertThatThrownBy { Runner(cliArgs, NullPrintStream(), NullPrintStream()).execute() }
                .isExactlyInstanceOf(BuildFailure::class.java)
        }

        it("should throw on invalid config property when validation=true") {
            val cliArgs = createCliArgs(
                "--input", inputPath.toString(),
                "--config-resource", "/configs/invalid-config.yml"
            )

            assertThatThrownBy { Runner(cliArgs, NullPrintStream(), NullPrintStream()).execute() }
                .isExactlyInstanceOf(InvalidConfig::class.java)
                .hasMessageContaining("property")
        }

        it("should throw on invalid config properties when validation=true") {
            val cliArgs = createCliArgs(
                "--input", inputPath.toString(),
                "--config-resource", "/configs/invalid-configs.yml"
            )

            assertThatThrownBy { Runner(cliArgs, NullPrintStream(), NullPrintStream()).execute() }
                .isExactlyInstanceOf(InvalidConfig::class.java)
                .hasMessageContaining("properties")
        }

        it("should not throw on invalid config property when validation=false") {
            val cliArgs = createCliArgs(
                "--input", inputPath.toString(),
                "--config-resource", "/configs/invalid-config_no-validation.yml"
            )

            assertThatCode { Runner(cliArgs, NullPrintStream(), NullPrintStream()).execute() }
                .doesNotThrowAnyException()
        }

        it("should never throw on maxIssues=-1") {
            val tmpReport = Files.createTempFile("RunnerSpec", ".txt")
            val cliArgs = createCliArgs(
                "--input", inputPath.toString(),
                "--report", "txt:$tmpReport",
                "--config-resource", "/configs/max-issues--1.yml"
            )

            Runner(cliArgs, NullPrintStream(), NullPrintStream()).execute()

            assertThat(Files.readAllLines(tmpReport)).hasSize(1)
        }

        context("with additional baseline file") {

            it("should not throw on maxIssues=0 due to baseline blacklist") {
                val tmpReport = Files.createTempFile("RunnerSpec", ".txt")
                val cliArgs = createCliArgs(
                    "--input", inputPath.toString(),
                    "--report", "txt:$tmpReport",
                    "--config-resource", "/configs/max-issues-0.yml",
                    "--baseline", Paths.get(resource("configs/baseline-with-two-excludes.xml")).toString()
                )

                Runner(cliArgs, NullPrintStream(), NullPrintStream()).execute()

                assertThat(Files.readAllLines(tmpReport)).isEmpty()
            }
        }
    }

    describe("executes the runner with create baseline") {

        it("should not throw on maxIssues=0") {
            val tmpReport = Files.createTempFile("RunnerSpec", ".txt")
            val cliArgs = createCliArgs(
                "--input", inputPath.toString(),
                "--baseline", Paths.get(resource("configs/baseline-empty.xml")).toString(),
                "--create-baseline",
                "--report", "txt:$tmpReport",
                "--config-resource", "/configs/max-issues-0.yml"
            )

            Runner(cliArgs, NullPrintStream(), NullPrintStream()).execute()

            assertThat(tmpReport).hasContent("")
        }
    }

    describe("customize output and error printers") {

        val outPrintStream by memoized { StringPrintStream() }
        val errPrintStream by memoized { StringPrintStream() }

        context("execute with default config which allows no issues") {

            val path: Path = Paths.get(resource("/cases/CleanPoko.kt"))

            beforeEachTest {
                val args = createCliArgs("--input", path.toString())

                Runner(args, outPrintStream, errPrintStream).execute()
            }

            it("writes no build related output to output printer") {
                assertThat(outPrintStream.toString()).doesNotContain("test - [Poko]")
            }

            it("does not write anything to error printer") {
                assertThat(errPrintStream.toString()).isEmpty()
            }
        }

        context("execute with strict config") {

            beforeEachTest {
                val args = createCliArgs(
                    "--input", inputPath.toString(),
                    "--config-resource", "/configs/max-issues-0.yml")

                try {
                    Runner(args, outPrintStream, errPrintStream).execute()
                } catch (ignored: BuildFailure) {
                }
            }

            it("writes output to output printer") {
                assertThat(outPrintStream.toString()).contains("test - [Poko]")
            }

            it("does not write anything to error printer") {
                assertThat(errPrintStream.toString()).isEmpty()
            }
        }
    }
})
