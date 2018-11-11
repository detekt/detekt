package io.gitlab.arturbosch.detekt.test

import javax.script.ScriptException

/**
 * @author schalkms
 */
class KotlinScriptException(e: ScriptException) : RuntimeException("Given Kotlin code is invalid.", e)
