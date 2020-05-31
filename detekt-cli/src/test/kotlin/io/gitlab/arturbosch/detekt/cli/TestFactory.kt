package io.gitlab.arturbosch.detekt.cli

import io.github.detekt.test.utils.NullPrintStream
import io.github.detekt.test.utils.resource
import io.gitlab.arturbosch.detekt.core.ModificationNotification
import java.nio.file.Paths

fun createNotification() = ModificationNotification(Paths.get(resource("empty.txt")))

/**
 * Creates an instance of [CliArgs]. Verification if the settings are sound
 * must be made by the caller.
 */
fun createCliArgs(vararg args: String): CliArgs {
    return parseArguments(args, NullPrintStream(), NullPrintStream())
}
