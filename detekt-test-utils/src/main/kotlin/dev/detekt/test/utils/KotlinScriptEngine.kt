package dev.detekt.test.utils

import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.mainKts.MainKtsScript
import kotlin.script.experimental.api.ScriptDiagnostic
import kotlin.script.experimental.api.isError
import kotlin.script.experimental.api.onFailure
import kotlin.script.experimental.host.ScriptingHostConfiguration
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvm.updateClasspath
import kotlin.script.experimental.jvm.util.scriptCompilationClasspathFromContext
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate

/**
 * The object to use the Kotlin script engine for code compilation.
 */
object KotlinScriptEngine {
    private val host = BasicJvmScriptingHost(ScriptingHostConfiguration())

    private val compilationConfiguration = createJvmCompilationConfigurationFromTemplate<MainKtsScript> {
        jvm {
            // Add test runtime classpath to test script compilation's runtime classpath
            val classPath = scriptCompilationClasspathFromContext(wholeClasspath = true)
            updateClasspath(classPath)
        }
    }

    /**
     * Compiles a given code string using Kotlin's scripting infrastructure.
     *
     * @throws IllegalStateException if the given code snippet does not compile
     */
    fun compile(@Language("kotlin") code: String) {
        host.runInCoroutineContext {
            host.compiler(code.toScriptSource(), compilationConfiguration)
        }.onFailure { result ->
            error(
                result.reports
                    .filter(ScriptDiagnostic::isError)
                    .joinToString("\n") { it.render() }
            )
        }
    }
}
