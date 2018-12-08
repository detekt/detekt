package io.gitlab.arturbosch.detekt.api

import java.io.PrintStream

/**
 * Extension point which describes how findings should be printed on the console.
 *
 * Additional [ConsoleReport]'s can be made available through the [java.util.ServiceLoader] pattern.
 * If the default reporting mechanism should be turned off, exclude the entry 'FindingsReport'
 * in the 'console-reports' property of a detekt yaml config.
 *
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
