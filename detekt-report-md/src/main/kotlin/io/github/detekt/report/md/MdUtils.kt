package io.github.detekt.report.md

import org.jetbrains.kotlin.backend.common.peek
import org.jetbrains.kotlin.backend.common.pop
import org.jetbrains.kotlin.backend.common.push
import kotlin.math.min

private const val MAX_INDENT = 3

typealias Block = () -> Unit

@Suppress("TooManyFunctions")
class MdUtils {
    private var content: String = ""
    private val indentStack = ArrayDeque<Int>()

    override fun toString() = content

    private fun indent() = " ".repeat(getBlockIndent())
    private fun getBlockIndent() = indentStack.peek() ?: 0
    private fun saveBlockIndent(indent: Int) { indentStack.push(getBlockIndent() + indent) }
    private fun restoreBlockIndent() { indentStack.pop() }

    private fun addBlock(tag: String = "", text: String = "", block: Block) {
        if ((tag + text).isNotEmpty()) {
            addText(tag + text)
        }

        if (block != {}) {
            saveBlockIndent(min(tag.length, MAX_INDENT))
            block()
            restoreBlockIndent()
        }
    }

    private fun addText(text: String) { content += indentText(text) }

    private fun indentText(text: String): String {
        val indent = indent()
        val indentedText = text.replace("\n", "\n$indent")
        return "$indent$indentedText\n"
    }

    fun h1(text: String, block: Block = {}) { addBlock("# ", text, block) }
    fun h2(text: String, block: Block = {}) { addBlock("## ", text, block) }
    fun h3(text: String, block: Block = {}) { addBlock("### ", text, block) }
    fun h4(text: String, block: Block = {}) { addBlock("#### ", text, block) }
    fun h5(text: String, block: Block = {}) { addBlock("##### ", text, block) }
    fun h6(text: String, block: Block = {}) { addBlock("###### ", text, block) }
    fun line(block: Block = {}) { addBlock(tag = "---", block = block) }
    fun text(text: String, block: Block = {}) { addBlock(text = text, block = block) }
    fun orderedListItem(text: String, block: Block = {}) { addBlock("1. ", text, block) }
    fun listItem(text: String, block: Block = {}) { addBlock("- ", text, block) }
    fun codeLine(text: String) { addText("`$text`") }
    fun codeBlock(text: String, lang: String) { addText("```$lang\n$text\n```") }

    fun details(summary: String, block: Block = {}) {
        addText("<details>")
        addText("<summary>$summary</summary>\n")
        addBlock(block = block)
        addText("</details>")
    }

    fun bold(text: String) = "**$text**"
    fun italic(text: String) = "*$text*"
    fun strikethrough(text: String) = "~~$text~~"
    fun subscript(text: String) = "<sub>$text</sub>"
    fun superscript(text: String) = "<sup>$text</sup>"
    fun quote(text: String) = "> $text"
    fun link(text: String, url: String) = "[$text]($url)"
}

fun markdown(build: MdUtils.() -> Unit): MdUtils {
    val markdown = MdUtils()
    markdown.build()
    return markdown
}
