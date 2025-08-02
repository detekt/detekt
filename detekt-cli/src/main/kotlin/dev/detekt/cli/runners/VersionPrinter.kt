package dev.detekt.cli.runners

import dev.detekt.tooling.api.VersionProvider

class VersionPrinter(private val outputPrinter: Appendable) : Executable {

    override fun execute() {
        outputPrinter.appendLine(VersionProvider.load().current())
    }
}
