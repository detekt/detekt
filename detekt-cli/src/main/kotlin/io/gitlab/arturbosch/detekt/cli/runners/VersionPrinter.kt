package io.gitlab.arturbosch.detekt.cli.runners

import io.github.detekt.tooling.api.VersionProvider

class VersionPrinter(private val outputPrinter: Appendable) : Executable {

    override fun execute() {
        outputPrinter.appendln(VersionProvider.load().current())
    }
}
