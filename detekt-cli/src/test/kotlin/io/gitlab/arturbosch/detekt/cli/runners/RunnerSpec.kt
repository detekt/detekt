package io.gitlab.arturbosch.detekt.cli.runners

import io.github.detekt.test.utils.StringPrintStream
import io.github.detekt.test.utils.createTempFileForTest
import io.github.detekt.test.utils.resource
import io.github.detekt.test.utils.resourceAsPath
import io.github.detekt.tooling.api.InvalidConfig
import io.github.detekt.tooling.api.MaxIssuesReached
import io.gitlab.arturbosch.detekt.cli.createCliArgs
import io.gitlab.arturbosch.detekt.cli.createRunner
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class RunnerSpec : Spek({

    val inputPath: Path = resourceAsPath("cases/Poko.kt")

    describe("executes the runner with different maxIssues configurations") {

        it("should report one issue when maxIssues=2") {
            val tmpReport = createTempFileForTest("RunnerSpec", ".txt")
            val cliArgs = createCliArgs(
                "--input", inputPath.toString(),
                "--report", "txt:$tmpReport",
                "--config-resource", "/configs/max-issues-2.yml"
            )

            createRunner(cliArgs).execute()

            assertThat(Files.readAllLines(tmpReport)).hasSize(1)
        }

        it("should throw on maxIssues=0") {
            val cliArgs = createCliArgs(
                "--input", inputPath.toString(),
                "--config-resource", "/configs/max-issues-0.yml"
            )

            assertThatThrownBy { createRunner(cliArgs).execute() }
                .isExactlyInstanceOf(MaxIssuesReached::class.java)
        }

        it("should never throw on maxIssues=-1") {
            val tmpReport = createTempFileForTest("RunnerSpec", ".txt")
            val cliArgs = createCliArgs(
                "--input", inputPath.toString(),
                "--report", "txt:$tmpReport",
                "--config-resource", "/configs/max-issues--1.yml"
            )

            createRunner(cliArgs).execute()

            assertThat(Files.readAllLines(tmpReport)).hasSize(1)
        }

        context("with additional baseline file") {

            it("should not throw on maxIssues=0 due to baseline") {
                val tmpReport = createTempFileForTest("RunnerSpec", ".txt")
                val cliArgs = createCliArgs(
                    "--input", inputPath.toString(),
                    "--report", "txt:$tmpReport",
                    "--config-resource", "/configs/max-issues-0.yml",
                    "--baseline", resourceAsPath("configs/baseline-with-two-excludes.xml").toString()
                )

                createRunner(cliArgs).execute()

                assertThat(Files.readAllLines(tmpReport)).isEmpty()
            }
        }
    }

    describe("executes the runner with create baseline") {

        it("should not throw on maxIssues=0") {
            val tmpReport = createTempFileForTest("RunnerSpec", ".txt")
            val cliArgs = createCliArgs(
                "--input", inputPath.toString(),
                "--baseline", Paths.get(resource("configs/baseline-empty.xml")).toString(),
                "--create-baseline",
                "--report", "txt:$tmpReport",
                "--config-resource", "/configs/max-issues-0.yml"
            )

            createRunner(cliArgs).execute()

            assertThat(tmpReport).hasContent("")
        }
    }

    describe("customize output and error printers") {

        val outPrintStream by memoized { StringPrintStream() }
        val errPrintStream by memoized { StringPrintStream() }

        context("execute with default config which allows no issues") {

            val path: Path = resourceAsPath("/cases/CleanPoko.kt")

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
                } catch (ignored: MaxIssuesReached) {
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

    describe("with config validation") {

        val path: Path = resourceAsPath("/cases/CleanPoko.kt")

        it("should throw on invalid config property when validation=true") {
            val cliArgs = createCliArgs(
                "--input", path.toString(),
                "--config-resource", "/configs/invalid-config.yml"
            )

            assertThatThrownBy { createRunner(cliArgs).execute() }
                .isExactlyInstanceOf(InvalidConfig::class.java)
                .hasMessageContaining("property")
        }

        it("should throw on invalid config properties when validation=true") {
            val cliArgs = createCliArgs(
                "--input", path.toString(),
                "--config-resource", "/configs/invalid-configs.yml"
            )

            assertThatThrownBy { createRunner(cliArgs).execute() }
                .isExactlyInstanceOf(InvalidConfig::class.java)
                .hasMessageContaining("properties")
        }

        it("should not throw on invalid config property when validation=false") {
            val cliArgs = createCliArgs(
                "--input", path.toString(),
                "--config-resource", "/configs/invalid-config_no-validation.yml"
            )

            assertThatCode { createRunner(cliArgs).execute() }.doesNotThrowAnyException()
        }

        it("should not throw on deprecation warnings") {
            val cliArgs = createCliArgs(
                "--input", path.toString(),
                "--config-resource", "/configs/deprecated-property.yml"
            )

            assertThatCode { createRunner(cliArgs).execute() }.doesNotThrowAnyException()
        }
    }
})
