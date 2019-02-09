package io.gitlab.arturbosch.detekt.watcher.config

import io.gitlab.arturbosch.ksh.api.Prompt
import io.gitlab.arturbosch.kutils.get

/**
 * @author Artur Bosch
 */
class DetektPrompt(
    config: DetektHome = Injekt.get()
) : Prompt {

    override val applicationName: String = APP_NAME
    override val historyFile: String = config.historyFile
    override fun message(): String = "$APP_NAME> "
}
