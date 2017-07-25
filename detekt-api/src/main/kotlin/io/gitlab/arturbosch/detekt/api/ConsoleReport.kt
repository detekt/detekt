package io.gitlab.arturbosch.detekt.api

import java.io.PrintStream

/**
 * @author Artur Bosch
 */
abstract class ConsoleReport {

	open val id: String = javaClass.simpleName
	open val priority: Int = -1

	@Suppress("EmptyFunctionBlock")
	open fun init(config: Config) {
	}

	fun print(printer: PrintStream, detektion: Detektion) {
		render(detektion)?.let {
			printer.println(it)
		}
	}

	abstract fun render(detektion: Detektion): String?
}
