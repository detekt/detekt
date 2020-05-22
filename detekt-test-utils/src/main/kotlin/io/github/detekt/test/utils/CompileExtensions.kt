package io.github.detekt.test.utils

import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Path

/**
 * Use this method if you define a kt file/class as a plain string in your test.
 */
fun compileContentForTest(
    @Language("kotlin") content: String,
    filename: String = TEST_FILENAME
): KtFile =
    KtTestCompiler.compileFromContent(content, filename)

/**
 * Use this method if you test a kt file/class in the test resources.
 */
fun compileForTest(path: Path) = KtTestCompiler.compile(path)
