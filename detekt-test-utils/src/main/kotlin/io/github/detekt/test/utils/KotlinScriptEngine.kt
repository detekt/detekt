package io.github.detekt.test.utils

import io.github.detekt.test.utils.KotlinScriptEnginePool.borrowEngine
import io.github.detekt.test.utils.KotlinScriptEnginePool.borrowNewEngine
import io.github.detekt.test.utils.KotlinScriptEnginePool.returnEngine
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.util.KotlinFrontEndException
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
        borrowEngine().compileWithRetryOnNameClash(code)
    }

    @Suppress("ForbiddenMethodCall")
    private fun PooledScriptEngine.compileWithRetryOnNameClash(code: String) {
        try {
            compile(code)
        } catch (_: KotlinFrontEndException) {
            println(
                "Kotlin compiler exception detected. " +
                    "This could be caused by a name clash with previously compiled snippets. Will retry to compile" +
                    "\n$code\n" +
                    "with a fresh script engine."
            )
            borrowNewEngine().compileWithRetryOnNameClash(code)
        } catch (e: ScriptException) {
            throw KotlinScriptException(e)
        } finally {
            returnEngine(this)
        }
    }
}
