package io.gitlab.arturbosch.detekt.cli.runners

import io.github.detekt.test.utils.StringPrintStream
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class VersionPrinterSpec {

    @Test
    fun `prints the version`() {
        val printStream = StringPrintStream()

        VersionPrinter(printStream).execute()

        assertThat(printStream.toString()).contains("1.6.0")
    }
}
