package io.github.detekt.test.utils

import io.github.detekt.parser.KotlinFirLoader
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.fir.psi
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.absolute

/**
 * Use this method if you define a kt file/class as a plain string in your test.
 */
fun compileContentForTest(
    @Language("kotlin") content: String,
    filename: String,
): KtFile {
    require('/' !in filename && '\\' !in filename) {
        "filename must be a file name only and not contain any path elements"
    }
    return compileContentForTest(content, path = Path("/$filename"))
}

/**
 * Use this method if you define a kt file/class as a plain string in your test.
 */
fun compileContentForTest(
    @Language("kotlin") content: String,
    path: Path = Path("/").absolute().resolve("Test.kt"),
): KtFile = KtTestCompiler.createKtFile(content, path)

/**
 * Use this method if you test a kt file/class in the test resources.
 */
fun compileForTest(path: Path): KtFile = KotlinFirLoader(listOf(KtTestCompiler.compile(path)), emptyList())
    .use { kotlinFirLoader -> kotlinFirLoader.load().outputs.flatMap { it.fir }.single().psi as KtFile }
