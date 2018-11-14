package io.gitlab.arturbosch.detekt.test

import org.jetbrains.kotlin.cli.common.environment.setIdeaIoUseFallback
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngine
import javax.script.ScriptEngineManager
import javax.script.ScriptException

/**
 * @author schalkms
 */
object KotlinScriptEngine {

	private val engine: KotlinJsr223JvmLocalScriptEngine

	init {
		setIdeaIoUseFallback() // To avoid error on Windows

		val scriptEngineManager = ScriptEngineManager()
		val localEngine = scriptEngineManager.getEngineByExtension("kts") as? KotlinJsr223JvmLocalScriptEngine
		requireNotNull(localEngine) { "Kotlin script engine not supported" }
		engine = localEngine
	}

	fun compile(code: String) {
		try {
			engine.compile(code)
		} catch (e: ScriptException) {
			throw KotlinScriptException(e)
		}
	}
}
