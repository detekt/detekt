package io.gitlab.arturbosch.detekt.watcher.commands

import io.gitlab.arturbosch.detekt.watcher.config.Injekt
import io.gitlab.arturbosch.detekt.watcher.service.DetektService
import io.gitlab.arturbosch.detekt.watcher.state.State
import io.gitlab.arturbosch.ksh.api.ShellClass
import io.gitlab.arturbosch.ksh.api.ShellMethod
import io.gitlab.arturbosch.ksh.api.ShellOption
import io.gitlab.arturbosch.ksh.api.ShellOptions
import io.gitlab.arturbosch.kutils.get

/**
 * @author Artur Bosch
 */
class Analyze(
		private val state: State = Injekt.get(),
		private val detekt: DetektService = Injekt.get()
) : ShellClass {

	@ShellMethod(help = "Runs an analysis on specified project.")
	fun main(
			@ShellOption(
					value = ["", "--subpath"],
					help = "If a sub path inside the project should be analyzed.",
					defaultValue = ShellOptions.NULL_DEFAULT
			) subPath: String?
	) {
		if (subPath == null) {
			detekt.run(state.project())
		} else {
			detekt.run(state.resolveSubPath(subPath))
		}
	}
}
