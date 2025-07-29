package io.gitlab.arturbosch.detekt.formatting

import com.intellij.openapi.util.text.StringUtilRt
import dev.detekt.api.Finding
import dev.detekt.test.utils.compileContentForTest
import dev.detekt.test.utils.compileForTest
import dev.detekt.test.utils.resource
import io.gitlab.arturbosch.detekt.test.FakeLanguageVersionSettings
import org.intellij.lang.annotations.Language
import java.io.File
import kotlin.io.path.toPath

fun FormattingRule.lint(@Language("kotlin") content: String, fileName: String = "Test.kt"): List<Finding> {
    require('/' !in fileName && '\\' !in fileName) {
        "filename must be a file name only and not contain any path elements"
    }
    val root = compileContentForTest(content, fileName)
    return this.visitFile(root, languageVersionSettings = FakeLanguageVersionSettings())
}

fun loadFile(resourceName: String) = compileForTest(resource(resourceName).toPath())

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

val longLines = """
    /**
     * These are class docs.
     */
    class C {
        /**
         * These are function docs.
         */
        fun getLoremIpsum() = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua."

        companion object {
            /**
             * This is a constant for "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua."
             */
            val LOREM_IPSUM = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua."
        }
    }
""".trimIndent()
