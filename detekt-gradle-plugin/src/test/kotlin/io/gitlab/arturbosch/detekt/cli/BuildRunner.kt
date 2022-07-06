@file:JvmName("Main")
@file:Suppress("unused", "UNUSED_PARAMETER")

package io.gitlab.arturbosch.detekt.cli

import java.io.PrintStream

// CLI stub to test happy+error path of DefaultCliInvoker
fun buildRunner(
    args: Array<String>,
    outputPrinter: PrintStream,
    errorPrinter: PrintStream
) = object {
    fun execute() {
        throw ClassCastException("testing reflection wrapper...")
    }
}
