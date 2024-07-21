package io.gitlab.arturbosch.detekt.cli

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.assertj.core.api.Assertions.assertThatIllegalStateException
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledOnOs
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS
import kotlin.io.path.Path

class ReportPathSpec {

    @EnabledOnOs(OS.WINDOWS)
    @Nested
    inner class `a Windows path` {
        @Test
        fun `parses a valid absolute path correctly`() {
            val reportPath = ReportPath.from("test:C:\\tmp\\valid\\report")

            assertThat(reportPath.path).isEqualTo(Path("C:\\tmp\\valid\\report"))
        }

        @Test
        fun `parses a valid relative path correctly`() {
            val reportPath = ReportPath.from("test:valid\\report")

            assertThat(reportPath.path).isEqualTo(Path("valid\\report"))
        }

        @Test
        fun `fails when the path is empty`() {
            assertThatIllegalArgumentException()
                .isThrownBy { ReportPath.from("test:") }
        }

        @Test
        fun `fails when the path is malformed`() {
            assertThatIllegalArgumentException()
                .isThrownBy { ReportPath.from("test:a*a") }
        }
    }

    @DisabledOnOs(OS.WINDOWS)
    @Nested
    inner class `a POSIX path` {
        @Test
        fun `parses a valid absolute path correctly`() {
            val reportPath = ReportPath.from("test:/tmp/valid/report")

            assertThat(reportPath.path).isEqualTo(Path("/tmp/valid/report"))
        }

        @Test
        fun `parses a valid relative path correctly`() {
            val reportPath = ReportPath.from("test:valid/report")

            assertThat(reportPath.path).isEqualTo(Path("valid/report"))
        }

        @Test
        fun `fails when the path is empty`() {
            assertThatIllegalArgumentException()
                .isThrownBy { ReportPath.from("test:") }
        }

        @Test
        fun `fails when the path is malformed`() {
            assertThatIllegalArgumentException()
                .isThrownBy { ReportPath.from("test:a${0.toChar()}a") }
        }
    }

    @Nested
    inner class `_kind_ processing` {
        @Test
        fun `parses and maps the xml kind correctly`() {
            val reportPath = ReportPath.from("xml:/tmp/valid/report")

            assertThat(reportPath.kind).isEqualTo("xml")
        }

        @Test
        fun `parses and maps the html kind correctly`() {
            val reportPath = ReportPath.from("html:/tmp/valid/report")

            assertThat(reportPath.kind).isEqualTo("html")
        }

        @Test
        fun `parses a non-default kind correctly`() {
            val reportPath = ReportPath.from("test:/tmp/valid/report")

            assertThat(reportPath.kind).isEqualTo("test")
        }

        @Test
        fun `fails when the kind is empty`() {
            assertThatIllegalArgumentException()
                .isThrownBy { ReportPath.from(":/tmp/anything") }
        }

        @Test
        fun `fails when part size is illegal`() {
            assertThatIllegalStateException()
                .isThrownBy { ReportPath.from("") }
        }
    }
}
