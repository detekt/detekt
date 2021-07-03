package io.gitlab.arturbosch.detekt.cli.runners

import io.github.detekt.test.utils.StringPrintStream
import io.github.detekt.test.utils.createTempFileForTest
import io.github.detekt.test.utils.resourceAsPath
import io.github.detekt.tooling.api.InvalidConfig
import io.github.detekt.tooling.api.MaxIssuesReached
import io.gitlab.arturbosch.detekt.cli.executeDetekt
import io.gitlab.arturbosch.detekt.cli.parseArguments
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Files
import java.nio.file.Path

class RunnerSpec : Spek({

    val inputPath = resourceAsPath("cases/Poko.kt")

    describe("executes the runner with different maxIssues configurations") {

        it("should report one issue when maxIssues=2") {
            val tmpReport = createTempFileForTest("RunnerSpec", ".txt")

            executeDetekt(
                "--input",
                inputPath.toString(),
                "--report",
                "txt:$tmpReport",
                "--config-resource",
                "/configs/max-issues-2.yml"
            )

            assertThat(Files.readAllLines(tmpReport)).hasSize(1)
        }

        it("should throw on maxIssues=0") {
            assertThatThrownBy {
                executeDetekt(
                    "--input",
                    inputPath.toString(),
                    "--config-resource",
                    "/configs/max-issues-0.yml"
                )
            }.isExactlyInstanceOf(MaxIssuesReached::class.java)
        }

        it("should never throw on maxIssues=-1") {
            val tmpReport = createTempFileForTest("RunnerSpec", ".txt")

            executeDetekt(
                "--input",
                inputPath.toString(),
                "--report",
                "txt:$tmpReport",
                "--config-resource",
                "/configs/max-issues--1.yml"
            )

            assertThat(Files.readAllLines(tmpReport)).hasSize(1)
        }

        context("with additional baseline file") {

            it("should not throw on maxIssues=0 due to baseline") {
                val tmpReport = createTempFileForTest("RunnerSpec", ".txt")

                executeDetekt(
                    "--input",
                    inputPath.toString(),
                    "--report",
                    "txt:$tmpReport",
                    "--config-resource",
                    "/configs/max-issues-0.yml",
                    "--baseline",
                    resourceAsPath("configs/baseline-with-two-excludes.xml").toString()
                )

                assertThat(Files.readAllLines(tmpReport)).isEmpty()
            }
        }
    }

    describe("executes the runner with create baseline") {

        it("should not throw on maxIssues=0") {
            val tmpReport = createTempFileForTest("RunnerSpec", ".txt")

            executeDetekt(
                "--input", inputPath.toString(),
                "--baseline", resourceAsPath("configs/baseline-empty.xml").toString(),
                "--create-baseline",
                "--report", "txt:$tmpReport",
                "--config-resource", "/configs/max-issues-0.yml"
            )

            assertThat(tmpReport).hasContent("")
        }
    }

    describe("customize output and error printers") {

        val outPrintStream by memoized { StringPrintStream() }
        val errPrintStream by memoized { StringPrintStream() }

        context("execute with default config which allows no issues") {

            val path: Path = resourceAsPath("/cases/CleanPoko.kt")

            beforeEachTest {
                val args = parseArguments(arrayOf("--input", path.toString()))

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
                val args = parseArguments(
                    arrayOf(
                        "--input",
                        inputPath.toString(),
                        "--config-resource",
                        "/configs/max-issues-0.yml"
                    )
                )

                assertThatThrownBy { Runner(args, outPrintStream, errPrintStream).execute() }
                    .isExactlyInstanceOf(MaxIssuesReached::class.java)
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
            assertThatThrownBy {
                executeDetekt(
                    "--input",
                    path.toString(),
                    "--config-resource",
                    "/configs/invalid-config.yml"
                )
            }.isExactlyInstanceOf(InvalidConfig::class.java)
                .hasMessageContaining("property")
        }

        it("should throw on invalid config properties when validation=true") {
            assertThatThrownBy {
                executeDetekt(
                    "--input",
                    path.toString(),
                    "--config-resource",
                    "/configs/invalid-configs.yml"
                )
            }.isExactlyInstanceOf(InvalidConfig::class.java)
                .hasMessageContaining("properties")
        }

        it("should not throw on invalid config property when validation=false") {
            assertThatCode {
                executeDetekt(
                    "--input",
                    path.toString(),
                    "--config-resource",
                    "/configs/invalid-config_no-validation.yml"
                )
            }.doesNotThrowAnyException()
        }

        it("should not throw on deprecation warnings") {
            assertThatCode {
                executeDetekt(
                    "--input",
                    path.toString(),
                    "--config-resource",
                    "/configs/deprecated-property.yml"
                )
            }.doesNotThrowAnyException()
        }
    }

    describe("executes the runner for a single rule") {

        it("should load and run custom rule") {
            val tmp = createTempFileForTest("SingleRuleRunnerSpec", ".txt")

            assertThatThrownBy {
                executeDetekt(
                    "--input",
                    inputPath.toString(),
                    "--report",
                    "txt:$tmp",
                    "--run-rule",
                    "test:test"
                )
            }.isExactlyInstanceOf(MaxIssuesReached::class.java)
            assertThat(Files.readAllLines(tmp)).hasSize(1)
        }

        it("should throw on non existing rule") {
            assertThatThrownBy { executeDetekt("--run-rule", "test:non_existing") }
                .isExactlyInstanceOf(IllegalArgumentException::class.java)
        }

        it("should throw on non existing rule set") {
            assertThatThrownBy { executeDetekt("--run-rule", "non_existing:test") }
                .isExactlyInstanceOf(IllegalArgumentException::class.java)
        }

        it("should throw on non existing run-rule") {
            assertThatThrownBy { executeDetekt("--run-rule", "") }
                .isExactlyInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("Pattern 'RuleSetId:RuleId' expected.")
        }
    }

    describe("runner with maxIssuePolicy") {

        it("does fail via cli flag") {
            assertThatThrownBy { executeDetekt("--input", inputPath.toString(), "--max-issues", "0") }
                .isExactlyInstanceOf(MaxIssuesReached::class.java)
                .hasMessage("Build failed with 1 weighted issues.")
        }

        it("does fail via cli flag even if config>maxIssues is specified") {
            assertThatThrownBy {
                executeDetekt(
                    "--input",
                    inputPath.toString(),
                    "--max-issues",
                    "0",
                    "--config-resource",
                    "configs/max-issues--1.yml" // allow any
                )
            }.isExactlyInstanceOf(MaxIssuesReached::class.java)
                .hasMessage("Build failed with 1 weighted issues.")
        }

        it("does not fail when cli flag is negative") {
            executeDetekt("--input", inputPath.toString(), "--max-issues", "-1")
        }

        it("does not fail when cli flag is positive") {
            executeDetekt("--input", inputPath.toString(), "--max-issues", "2")
        }
    }

    describe("runner with build-upon-default-config checking detekt-api") {

        val referencePath: Path = resourceAsPath("/cases/CleanPoko.kt")
        val path: Path = referencePath.resolve("../../../../../../detekt-api/src/main").normalize()

        it("should not throw") {
            assertThatCode {
                executeDetekt(
                    "--input",
                    path.toString(),
                    "--build-upon-default-config"
                )
            }.doesNotThrowAnyException()
        }
    }
})
