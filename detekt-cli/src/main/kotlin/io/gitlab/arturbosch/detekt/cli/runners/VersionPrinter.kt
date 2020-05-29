package io.gitlab.arturbosch.detekt.cli.runners

import io.gitlab.arturbosch.detekt.api.internal.whichDetekt
import java.io.PrintStream

class VersionPrinter(private val outputPrinter: PrintStream) : Executable {

    override fun execute() {
        val version = whichDetekt()
        if (version != null) {
            outputPrinter.println(version)
        } else {
            error("Can't find the detekt version")
        }
    }
}
