package io.gitlab.arturbosch.detekt.formatting

import com.pinterest.ktlint.core.ast.visit
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.test.KotlinScriptEngine
import io.gitlab.arturbosch.detekt.test.compileContentForTest
import io.gitlab.arturbosch.detekt.test.compileForTest
import io.gitlab.arturbosch.detekt.test.resource
import io.gitlab.arturbosch.detekt.test.shouldCompileTestSnippets
import org.jetbrains.kotlin.com.intellij.openapi.util.text.StringUtilRt
import java.io.File
import java.nio.file.Paths

fun FormattingRule.lint(content: String): List<Finding> {
    if (shouldCompileTestSnippets) {
        KotlinScriptEngine.compile(content)
    }
    val root = compileContentForTest(content)
    this.visit(root)
    root.node.visit { node -> this.apply(node) }
    return this.findings
}

fun loadFile(resourceName: String) = compileForTest(Paths.get(resource(resourceName)))

fun loadFileContent(resourceName: String) =
    StringUtilRt.convertLineSeparators(File(resource(resourceName)).readText())

val contentAfterChainWrapping = """
fun main() {
    val anchor = owner.firstChild!!
        .siblings(forward = true)
        .dropWhile { it is PsiComment || it is PsiWhiteSpace }
    val s = foo()
        ?: bar
    val s = foo()
        ?.bar
    val s = 1
        + 2
    val s = true &&
        false
    val s = b.equals(o.b) &&
        g == o.g
    val d = 1 +
        -1
    val d = 1
        + -1
    when (foo){
        0 -> {
        }
        1 -> {
        }
        -2 -> {
        }
    }
    if (
      -3 == a()
    ) {}
    if (
      // comment
      -3 == a()
    ) {}
    if (
      /* comment */
      -3 == a()
    ) {}
    if (c)
      -7
    else
      -8
    try {
      fn()
    } catch(e: Exception) {
      -9
    }
    var x =
        -2 >
        (2 + 2)
    -3
}

""".trimIndent()
