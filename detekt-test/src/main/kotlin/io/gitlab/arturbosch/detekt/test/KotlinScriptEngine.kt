package io.gitlab.arturbosch.detekt.test

import org.jetbrains.kotlin.cli.common.environment.setIdeaIoUseFallback
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngine
import javax.script.ScriptEngineManager
import javax.script.ScriptException

/**
 * The object to create the Kotlin script engine for code compilation.
 *
 * @author schalkms
 */
object KotlinScriptEngine {

	private lateinit var engine: KotlinJsr223JvmLocalScriptEngine

	init {
		createEngine()
	}

	/**
	 * Compiles a given code string with the Jsr223 script engine.
	 * If a compilation error occurs the script engine is recovered.
	 *
	 * @param code The String to compile
	 * @throws ScriptException
	 */
	fun compile(code: String) {
		try {
			engine.compile(code)
		} catch (e: ScriptException) {
			createEngine() // recover
			throw KotlinScriptException(e)
		}
	}

	private fun createEngine() {
		setIdeaIoUseFallback() // To avoid error on Windows

		val scriptEngineManager = ScriptEngineManager()
		val localEngine = scriptEngineManager.getEngineByExtension("kts") as? KotlinJsr223JvmLocalScriptEngine
		requireNotNull(localEngine) { "Kotlin script engine not supported" }
		engine = localEngine
	}
}
