package io.github.detekt.test.utils

import javax.script.ScriptException

/**
 * The generic exception class for the Kotlin script engine APIs.
 * Exception types thrown by the underlying scripting implementations are wrapped in instances of
 * KotlinScriptException.
 */
class KotlinScriptException(e: ScriptException) : RuntimeException("Given Kotlin code is invalid.", e)
