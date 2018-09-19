package io.gitlab.arturbosch.detekt.watcher.commands

import io.gitlab.arturbosch.detekt.watcher.config.Injekt
import io.gitlab.arturbosch.detekt.watcher.state.Parameters
import io.gitlab.arturbosch.detekt.watcher.state.State
import io.gitlab.arturbosch.ksh.api.ShellClass
import io.gitlab.arturbosch.ksh.api.ShellMethod
import io.gitlab.arturbosch.ksh.api.ShellOption
import io.gitlab.arturbosch.ksh.api.ShellOptions
import io.gitlab.arturbosch.kutils.get

/**
 * @author Artur Bosch
 */
class Project(
		private val state: State = Injekt.get()
) : ShellClass {

	override val commandId: String = "project"

	@ShellMethod(help = "Allows to set project to analyze and config to use for detekt.")
	fun main(
			@ShellOption(
					value = ["", "--path"],
					help = "Path to project. Must be a directory.",
					defaultValue = ShellOptions.NULL_DEFAULT
			) path: String?,
			@ShellOption(
					value = ["-c", "--config"],
					help = "Config to use for detekt analysis.",
					defaultValue = ShellOptions.NULL_DEFAULT
			) config: String?
	) {
		state.use(Parameters(path, config))
	}
}
