package io.gitlab.arturbosch.detekt.cli.runners

import io.github.detekt.tooling.api.VersionProvider
import java.io.PrintStream

class VersionPrinter(private val outputPrinter: PrintStream) : Executable {

    override fun execute() {
        outputPrinter.println(VersionProvider.load().current())
    }
}
