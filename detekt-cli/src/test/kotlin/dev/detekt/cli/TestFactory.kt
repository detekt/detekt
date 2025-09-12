package dev.detekt.cli

import dev.detekt.test.utils.NullPrintStream

fun CliArgs.toSpec() = createSpec(NullPrintStream(), NullPrintStream())
