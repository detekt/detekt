package io.github.detekt.test.utils

import org.jetbrains.kotlin.cli.common.environment.setIdeaIoUseFallback
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngine
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngineFactory
import java.util.Collections

/**
 * The object to manage a pool of Kotlin script engines to distribute the load for compiling code.
 * The load for compiling code is distributed over a number of engines.
 */
internal object KotlinScriptEnginePool {

    private val AVAILABLE_ENGINES: MutableList<PooledScriptEngine> =
        Collections.synchronizedList(mutableListOf())

    fun borrowEngine(): PooledScriptEngine = AVAILABLE_ENGINES.removeFirstOrNull() ?: createEngine()

    fun borrowNewEngine(): PooledScriptEngine = createEngine()

    fun returnEngine(engine: PooledScriptEngine) {
        AVAILABLE_ENGINES.add(engine)
    }

    private fun createEngine(): PooledScriptEngine {
        setIdeaIoUseFallback() // To avoid error on Windows
        val engine = KotlinJsr223JvmLocalScriptEngineFactory().scriptEngine as? KotlinJsr223JvmLocalScriptEngine
            ?: error("Kotlin script engine not supported")
        return PooledScriptEngine(engine)
    }
}

internal class PooledScriptEngine(private val engine: KotlinJsr223JvmLocalScriptEngine) {
    fun compile(code: String) {
        engine.compile(code)
    }
}
