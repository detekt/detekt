package io.gitlab.arturbosch.detekt.cli.runners

import io.github.detekt.test.utils.StringPrintStream
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek

class VersionPrinterSpec : Spek({

    test("prints the version") {
        val printStream = StringPrintStream()

        VersionPrinter(printStream).execute()

        assertThat(printStream.toString()).contains("1.6.0")
    }
})
