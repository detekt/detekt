package io.gitlab.arturbosch.detekt.cli

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.assertj.core.api.Assertions.assertThatIllegalStateException
import org.jetbrains.kotlin.com.intellij.openapi.util.SystemInfo
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Paths

class ReportPathSpec : Spek({

    describe("report paths") {
        if (SystemInfo.isWindows) {
            context("a Windows path") {
                it("parses a valid absolute path correctly") {
                    val reportPath = ReportPath.from("test:C:\\tmp\\valid\\report")

                    assertThat(reportPath.path).isEqualTo(Paths.get("C:\\tmp\\valid\\report"))
                }

                it("parses a valid relative path correctly") {
                    val reportPath = ReportPath.from("test:valid\\report")

                    assertThat(reportPath.path).isEqualTo(Paths.get("valid\\report"))
                }

                it("fails when the path is empty") {
                    assertThatIllegalArgumentException()
                        .isThrownBy { ReportPath.from("test:") }
                }

                it("fails when the path is malformed") {
                    assertThatIllegalArgumentException()
                        .isThrownBy { ReportPath.from("test:a*a") }
                }
            }
        } else {
            context("a POSIX path") {
                it("parses a valid absolute path correctly") {
                    val reportPath = ReportPath.from("test:/tmp/valid/report")

                    assertThat(reportPath.path).isEqualTo(Paths.get("/tmp/valid/report"))
                }

                it("parses a valid relative path correctly") {
                    val reportPath = ReportPath.from("test:valid/report")

                    assertThat(reportPath.path).isEqualTo(Paths.get("valid/report"))
                }

                it("fails when the path is empty") {
                    assertThatIllegalArgumentException()
                        .isThrownBy { ReportPath.from("test:") }
                }

                it("fails when the path is malformed") {
                    assertThatIllegalArgumentException()
                        .isThrownBy { ReportPath.from("test:a${0.toChar()}a") }
                }
            }
        }
    }

    describe("`kind` processing") {
        it("parses and maps the txt kind correctly") {
            val reportPath = ReportPath.from("txt:/tmp/valid/report")

            assertThat(reportPath.kind).isEqualTo("txt")
        }

        it("parses and maps the xml kind correctly") {
            val reportPath = ReportPath.from("xml:/tmp/valid/report")

            assertThat(reportPath.kind).isEqualTo("xml")
        }

        it("parses and maps the html kind correctly") {
            val reportPath = ReportPath.from("html:/tmp/valid/report")

            assertThat(reportPath.kind).isEqualTo("html")
        }

        it("parses and maps the txt kind correctly") {
            val reportPath = ReportPath.from("txt:/tmp/valid/report")

            assertThat(reportPath.kind).isEqualTo("txt")
        }

        it("parses a non-default kind correctly") {
            val reportPath = ReportPath.from("test:/tmp/valid/report")

            assertThat(reportPath.kind).isEqualTo("test")
        }

        it("fails when the kind is empty") {
            assertThatIllegalArgumentException()
                .isThrownBy { ReportPath.from(":/tmp/anything") }
        }

        it("fails when part size is illegal") {
            assertThatIllegalStateException()
                .isThrownBy { ReportPath.from("") }
        }
    }
})
