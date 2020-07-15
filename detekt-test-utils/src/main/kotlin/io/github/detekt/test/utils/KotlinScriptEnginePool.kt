package io.github.detekt.test.utils

import org.jetbrains.kotlin.cli.common.environment.setIdeaIoUseFallback
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngine
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngineFactory

/**
 * The object to manage a pool of Kotlin script engines to distribute the load for compiling code.
 * The load for compiling code is distributed over a number of engines.
 */
internal object KotlinScriptEnginePool {

    private const val NUMBER_OF_ENGINES = 8

    private val engines: Array<KotlinJsr223JvmLocalScriptEngine> by lazy {
        Array(NUMBER_OF_ENGINES) { createEngine() }
    }
    private var id = 0

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
        val engine = KotlinJsr223JvmLocalScriptEngineFactory().scriptEngine as? KotlinJsr223JvmLocalScriptEngine
        return requireNotNull(engine) { "Kotlin script engine not supported" }
    }
}
