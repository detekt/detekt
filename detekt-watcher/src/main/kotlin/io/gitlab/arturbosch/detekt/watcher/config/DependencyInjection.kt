package io.gitlab.arturbosch.detekt.watcher.config

import io.gitlab.arturbosch.kutils.DefaultInjektor
import io.gitlab.arturbosch.kutils.Injektor

/**
 * @author Artur Bosch
 */
internal class DependencyInjection : DefaultInjektor()

val Injekt: Injektor = DependencyInjection()

const val USER_HOME = "user.home"
const val APP_NAME = "detekt"
const val HOME_DIR = ".$APP_NAME"
const val CONFIG_FILE = "config.properties"
const val HISTORY_FILE = "/$HOME_DIR/history"
