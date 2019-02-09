package io.gitlab.arturbosch.detekt.watcher

import io.gitlab.arturbosch.detekt.watcher.config.DetektHome
import io.gitlab.arturbosch.detekt.watcher.config.DetektPrompt
import io.gitlab.arturbosch.detekt.watcher.config.HOME_DIR
import io.gitlab.arturbosch.detekt.watcher.config.Injekt
import io.gitlab.arturbosch.detekt.watcher.config.USER_HOME
import io.gitlab.arturbosch.detekt.watcher.service.DetektService
import io.gitlab.arturbosch.detekt.watcher.state.State
import io.gitlab.arturbosch.ksh.bootstrap
import io.gitlab.arturbosch.kutils.addSingletonFactory
import java.nio.file.Paths

/**
 * @author Artur Bosch
 */
fun main(args: Array<String>) {
    Injekt.addSingletonFactory { DetektHome(Paths.get(System.getProperty(USER_HOME), HOME_DIR)) }
    Injekt.addSingletonFactory { DetektPrompt() }
    Injekt.addSingletonFactory { State() }
    Injekt.addSingletonFactory { DetektService() }
    bootstrap(args)
}
