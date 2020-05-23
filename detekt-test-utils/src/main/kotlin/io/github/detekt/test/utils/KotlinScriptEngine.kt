package io.github.detekt.test.utils

import org.intellij.lang.annotations.Language
import javax.script.ScriptException

/**
 * The object to use the Kotlin script engine for code compilation.
 */
object KotlinScriptEngine {

    /**
     * Compiles a given code string with the Jsr223 script engine.
     * If a compilation error occurs the script engine is recovered.
     * Afterwards this method throws a [KotlinScriptException].
     */
    fun compile(@Language("kotlin") code: String) {
        try {
            KotlinScriptEnginePool.getEngine().compile(code)
        } catch (e: ScriptException) {
            KotlinScriptEnginePool.recoverEngine()
            throw KotlinScriptException(e)
        }
    }
}
