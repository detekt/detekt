package io.github.detekt.compiler.plugin.util

import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.PluginOption
import com.tschuchort.compiletesting.SourceFile
import io.github.detekt.compiler.plugin.DetektCommandLineProcessor
import io.github.detekt.compiler.plugin.DetektCompilerPluginRegistrar
import org.intellij.lang.annotations.Language
import java.io.OutputStream

object CompilerTestUtils {

    fun compile(
        @Language("kotlin") vararg kotlinSources: String,
        useDefaultConfig: Boolean = true,
    ): JvmCompilationResult {
        val sourceFiles = kotlinSources.map {
            SourceFile.kotlin("KClass.kt", it, trimIndent = true)
        }
        return KotlinCompilation().apply {
            messageOutputStream = object : OutputStream() {
                override fun write(b: Int) {
                    // no-op
                }
            }
            sources = sourceFiles
            compilerPluginRegistrars = listOf(DetektCompilerPluginRegistrar())
            commandLineProcessors = listOf(DetektCommandLineProcessor())
            pluginOptions = listOf(
                PluginOption(
                    "detekt-compiler-plugin",
                    "useDefaultConfig",
                    useDefaultConfig.toString(),
                )
            )
        }.compile()
    }
}
