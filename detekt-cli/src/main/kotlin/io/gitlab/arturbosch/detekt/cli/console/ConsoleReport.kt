package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.Detektion
import java.io.PrintStream

/**
 * @author Artur Bosch
 */
abstract class ConsoleReport {

	open val id: String = javaClass.simpleName
	open val priority: Int = -1

	fun print(printer: PrintStream, detektion: Detektion) {
		render(detektion)?.let {
			printer.println(it)
		}
	}

	abstract fun render(detektion: Detektion): String?

}

const val PREFIX = "\t- "

fun Any.format(prefix: String = "", suffix: String = "\n") = "$prefix$this$suffix"
