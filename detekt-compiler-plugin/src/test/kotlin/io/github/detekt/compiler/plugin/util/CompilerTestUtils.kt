package io.github.detekt.compiler.plugin.util

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import io.github.detekt.compiler.plugin.DetektCommandLineProcessor
import io.github.detekt.compiler.plugin.DetektComponentRegistrar
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

object CompilerTestUtils {

    @OptIn(ExperimentalCompilerApi::class)
    fun compile(@Language("kotlin") vararg kotlinSources: String): KotlinCompilation.Result {
        val sourceFiles = kotlinSources.map {
            SourceFile.kotlin("KClass.kt", it, trimIndent = true)
        }
        return KotlinCompilation().apply {
            sources = sourceFiles
            compilerPlugins = listOf(DetektComponentRegistrar())
            commandLineProcessors = listOf(DetektCommandLineProcessor())
        }.compile()
    }
}
