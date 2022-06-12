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
     * Since the script engines are reused, this might cause name clashes between compilation attempts. In this case
     * a new script engine is created and the compilation is attempted a second time.
     *
     * @throws KotlinScriptException if the given code snippet does not compile
     */
    fun compile(@Language("kotlin") code: String) {
        borrowEngine().compileWithRetryOnFrontendException(code)
    }

    @Suppress("ForbiddenMethodCall")
    private fun PooledScriptEngine.compileWithRetryOnFrontendException(code: String) {
        try {
            compile(code)
        } catch (_: KotlinFrontEndException) {
            println(
                "w: Kotlin compiler exception detected. " +
                    "This is most likely caused by a name clash with previously compiled snippets"
            )
            borrowNewEngine().compileWithRetryOnFrontendException(code)
        } catch (e: ScriptException) {
            throw KotlinScriptException(e)
        } finally {
            returnEngine(this)
        }
    }
}
