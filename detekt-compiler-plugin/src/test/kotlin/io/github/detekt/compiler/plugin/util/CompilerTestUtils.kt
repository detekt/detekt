package io.github.detekt.compiler.plugin.util

import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import io.github.detekt.compiler.plugin.DetektCompilerPluginRegistrar
import org.intellij.lang.annotations.Language

object CompilerTestUtils {

    fun compile(@Language("kotlin") vararg kotlinSources: String): JvmCompilationResult {
        val sourceFiles = kotlinSources.map {
            SourceFile.kotlin("KClass.kt", it, trimIndent = true)
        }
        return KotlinCompilation().apply {
            verbose = false
            sources = sourceFiles
            compilerPluginRegistrars = listOf(DetektCompilerPluginRegistrar())
        }.compile()
    }
}
