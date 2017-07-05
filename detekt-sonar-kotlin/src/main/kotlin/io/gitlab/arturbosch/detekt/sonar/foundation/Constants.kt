package io.gitlab.arturbosch.detekt.sonar.foundation

import io.gitlab.arturbosch.detekt.sonar.DetektPlugin
import org.sonar.api.utils.log.Logger
import org.sonar.api.utils.log.Loggers

const val KOTLIN_KEY = "kotlin"
const val KOTLIN_NAME = "Kotlin"
const val KOTLIN_FILE_SUFFIX = ".kt"
const val KOTLIN_SCRIPT_SUFFIX = ".kts"

const val DETEKT_WAY = "Detekt way"
const val DETEKT_SENSOR = "DetektSensor"
const val DETEKT_REPOSITORY = "detekt-kotlin"
const val DETEKT_ANALYZER = "Detekt-based Kotlin Analyzer"

val LOG: Logger = Loggers.get(DetektPlugin::class.java)