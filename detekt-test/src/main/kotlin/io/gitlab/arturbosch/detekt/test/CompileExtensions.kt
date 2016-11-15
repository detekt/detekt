package io.gitlab.arturbosch.detekt.test

import java.nio.file.Path

/**
 * Use this method if you define a kt file/class as a plain string in your test.
 */
fun compileContentForTest(content: String) = KtTestCompiler.compileFromContent(content)

/**
 * Use this method if you test a kt file/class in the test resources.
 */
fun compileForTest(path: Path) = KtTestCompiler.compile(path)
