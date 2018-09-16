package io.gitlab.arturbosch.detekt.watcher.state

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.YamlConfig
import io.gitlab.arturbosch.detekt.cli.ExistingPathConverter
import io.gitlab.arturbosch.detekt.core.PathFilter
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import io.gitlab.arturbosch.detekt.core.exists
import io.gitlab.arturbosch.detekt.watcher.config.DetektHome
import io.gitlab.arturbosch.detekt.watcher.config.Injekt
import io.gitlab.arturbosch.detekt.watcher.service.DirectoryRegisteringVisitor
import io.gitlab.arturbosch.kutils.get
import io.gitlab.arturbosch.kutils.isTrue
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.WatchService

/**
 * @author Artur Bosch
 */
class State(
		home: DetektHome = Injekt.get()
) {

	var shouldWatch: Boolean = false

	private var project: Path? = null

	private var config: Config =
			home.property("detekt.config.default")?.let {
				YamlConfig.load(ExistingPathConverter().convert(it))
			} ?: Config.empty

	fun project(): Path = project
			?: throw IllegalStateException("Please specify a root path with the 'project' command first.")

	fun isValid() = project != null

	fun use(parameters: Parameters) {
		if (shouldWatch) {
			shouldWatch = false
			println("Stopping ongoing watcher for ${project()}")
		}
		project = parameters.extractWatchDirectory()
		config = parameters.extractConfig()
	}

	fun resolveSubPath(sub: String): Path = project().resolve(sub)

	fun settings() = ProcessingSettings(
			project(),
			config,
			listOf(PathFilter(".*/resources/.*"),
					PathFilter(".*/build/.*"))
	)

	fun newWatcher(): WatchService {
		check(project != null && project?.exists().isTrue())
		val watchService = FileSystems.getDefault().newWatchService()
		Files.walkFileTree(project, DirectoryRegisteringVisitor(watchService))
		println("Starting detekt watch service for $project")
		return watchService
	}
}
