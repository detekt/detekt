package io.gitlab.arturbosch.detekt.watcher.config

import io.gitlab.arturbosch.kutils.ApplicationHomeFolder
import java.nio.file.Path

/**
 * @author Artur Bosch
 */
class DetektHome(home: Path) : ApplicationHomeFolder(home) {

    internal val configFile: Path = resolveFile(CONFIG_FILE, shouldCreate = true)
    internal val historyFile: String = System.getProperty(USER_HOME) + HISTORY_FILE

    init {
        addPropertiesFromFile(configFile)
    }
}
