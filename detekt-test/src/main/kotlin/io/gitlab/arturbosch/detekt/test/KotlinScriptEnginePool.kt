package io.gitlab.arturbosch.detekt.test

import org.jetbrains.kotlin.cli.common.environment.setIdeaIoUseFallback
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngine
import javax.script.ScriptEngineManager

/**
 * The object to manage a pool of Kotlin script engines to distribute the load for compiling code.
 * The load for compiling code is distributed over a number of engines.
 *
 * @author schalkms
 */
object KotlinScriptEnginePool {

    private const val NUMBER_OF_ENGINES = 8

    private val engines: Array<KotlinJsr223JvmLocalScriptEngine>
    private var id = 0

    init {
        engines = Array(NUMBER_OF_ENGINES) { createEngine() }
    }

    fun getEngine(): KotlinJsr223JvmLocalScriptEngine {
        id++
        if (id == NUMBER_OF_ENGINES) {
            id = 0
        }
        return engines[id]
    }

    fun recoverEngine() {
        engines[id] = createEngine()
    }

    private fun createEngine(): KotlinJsr223JvmLocalScriptEngine {
        setIdeaIoUseFallback() // To avoid error on Windows

        val scriptEngineManager = ScriptEngineManager()
        val engine = scriptEngineManager.getEngineByExtension("kts") as? KotlinJsr223JvmLocalScriptEngine
        requireNotNull(engine) { "Kotlin script engine not supported" }
        return engine
    }
}
