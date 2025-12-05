package dev.detekt.test

import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.cli.jvm.config.javaSourceRoots
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.absolute

/**
 * Use this method if you define a kt file/class as a plain string in your test.
 */
fun compileContentForTest(
    @Language("kotlin") content: String,
    filename: String = "Test.kt",
): KtFile {
    require('/' !in filename && '\\' !in filename) {
        "filename must be a file name only and not contain any path elements"
    }
    return compileContentForTest(content, path = Path("/").absolute().resolve(filename))
}

/**
 * Use this method if you define a kt file/class as a plain string in your test.
 */
fun compileContentForTest(
    @Language("kotlin") content: String,
    environment: KotlinEnvironmentContainer,
    filename: String = "Test.kt",
): KtFile {
    require('/' !in filename && '\\' !in filename) {
        "filename must be a file name only and not contain any path elements"
    }

    return KotlinAnalysisApiEngine.compile(
        code = content,
        javaSourceRoots = environment.configuration.javaSourceRoots.map(::Path),
    )
}

/**
 * Use this method if you define a kt file/class as a plain string in your test.
 */
fun compileContentForTest(
    @Language("kotlin") content: String,
    path: Path,
): KtFile = KtTestCompiler.createKtFile(content, path)

/**
 * Use this method if you test a kt file/class in the test resources.
 */
fun compileForTest(path: Path) = KtTestCompiler.compile(path)
