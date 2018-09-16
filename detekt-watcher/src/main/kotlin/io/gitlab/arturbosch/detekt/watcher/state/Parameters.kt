package io.gitlab.arturbosch.detekt.watcher.state

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.cli.CliArgs
import io.gitlab.arturbosch.detekt.cli.ExistingPathConverter
import io.gitlab.arturbosch.detekt.cli.loadConfiguration
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Artur Bosch
 */
class Parameters(
		private val input: String? = null,
		private var config: String? = null
) {

	fun extractWatchDirectory(): Path {
		return input?.let {
			ExistingPathConverter().convert(it)
		} ?: Paths.get(".")
	}

	fun extractConfig(): Config? =
			config?.let {
				CliArgs().apply { this.config = this@Parameters.config }.loadConfiguration()
			}
}
