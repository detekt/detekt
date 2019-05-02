package io.gitlab.arturbosch.detekt.test

import javax.script.ScriptException

/**
 * The object to use the Kotlin script engine for code compilation.
 *
 * @author schalkms
 */
object KotlinScriptEngine {

    /**
     * Compiles a given code string with the Jsr223 script engine.
     * If a compilation error occurs the script engine is recovered.
     *
     * @param code the String to compile
     * @throws ScriptException
     */
    fun compile(code: String) {
        try {
            KotlinScriptEnginePool.getEngine().compile(code)
        } catch (e: ScriptException) {
            KotlinScriptEnginePool.recoverEngine()
            throw KotlinScriptException(e)
        }
    }
}
