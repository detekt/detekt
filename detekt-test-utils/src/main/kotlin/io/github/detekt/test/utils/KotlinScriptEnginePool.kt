package io.github.detekt.test.utils

import org.jetbrains.kotlin.cli.common.environment.setIdeaIoUseFallback
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngine
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngineFactory
import java.util.Collections

/**
 * The object to manage a pool of Kotlin script engines to distribute the load for compiling code.
 * Creating script engines is expensive so they are reused when compiling snippets. The pool is able to grow
 * dynamically so whenever there is no script engine available, a new one is created. Access to the pooled engines is
 * thread safe.
 */
internal object KotlinScriptEnginePool {

    private val AVAILABLE_ENGINES: MutableList<PooledScriptEngine> =
        Collections.synchronizedList(mutableListOf())

    /**
     * Retrieves an engine from the pool. If none is available, a new one is created. The method is thread safe.
     *
     * When the caller is done using the engine, it should be returned to the pool by calling [returnEngine].
     */
    fun borrowEngine(): PooledScriptEngine = AVAILABLE_ENGINES.removeFirstOrNull() ?: createEngine()

    /**
     * Creates a new engine.
     *
     * When the caller is done using the engine, it should be returned to the pool by calling [returnEngine].
     */
    fun borrowNewEngine(): PooledScriptEngine = createEngine()

    /**
     * Returns a borrowed engine to the pool. This method is thread safe.
     */
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
