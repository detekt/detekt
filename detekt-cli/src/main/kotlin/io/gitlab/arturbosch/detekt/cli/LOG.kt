package io.gitlab.arturbosch.detekt.cli

import java.io.PrintStream

/**
 * @author Artur Bosch
 */
object LOG {

	var printer: PrintStream = System.out
	var active: Boolean = false

	fun debug(message: String) {
		if (active) {
			printer.println(message)
		}
	}

	fun debug(message: () -> String) {
		if (active) {
			printer.println(message.invoke())
		}
	}
}
