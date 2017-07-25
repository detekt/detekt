package io.gitlab.arturbosch.detekt.api

import java.io.PrintStream

/**
 * @author Artur Bosch
 */
abstract class ConsoleReport : Extension {

	fun print(printer: PrintStream, detektion: Detektion) {
		render(detektion)?.let {
			printer.println(it)
		}
	}

	abstract fun render(detektion: Detektion): String?
}
