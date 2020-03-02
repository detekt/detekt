package io.gitlab.arturbosch.detekt.cli.runners

import io.gitlab.arturbosch.detekt.core.whichDetekt

class VersionPrinter : Executable {

    override fun execute() {
        val version = whichDetekt()
        println(version)
    }
}
