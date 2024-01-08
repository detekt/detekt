package io.github.detekt.test.utils

import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Path
import kotlin.io.path.Path

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
    return KtTestCompiler.createKtFile(content, Path("/"), Path("/$filename"))
}

/**
 * Use this method if you test a kt file/class in the test resources.
 */
fun compileForTest(path: Path) = KtTestCompiler.compile(resourceAsPath("/"), path)
