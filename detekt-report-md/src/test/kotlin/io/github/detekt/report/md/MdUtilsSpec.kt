package io.github.detekt.report.md

import io.github.detekt.test.utils.readResourceContent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MdUtilsSpec {
    @Test
    fun `asserts that the generated Markdown is the same as expected`() {
        val expected = readResourceContent("MdOutputFormatTest.md")
        val result = renderSample()

        assertThat(result).isEqualTo(expected)
    }
}

private fun renderSample() = markdown {
    h1("h1") {
        h2("h2")
        h3("h3")
        h4("h4")
        h5("h5")
        h6("h6")
        text("text")
        line()
        orderedListItem("orderedListItem")
        orderedListItem("") {
            details("orderedListItem") {
                orderedListItem("orderedListItem")
                orderedListItem("orderedListItem")
                orderedListItem("") {
                    details("details") {
                        listItem("listItem")
                        listItem("listItem")
                        text(" ")
                        codeLine("codeLine")
                        text(quote("quote"))
                        text(" ")
                        text(bold("bold"))
                        text(italic("italic"))
                        text(strikethrough("strikethrough"))
                        text(subscript("subscript"))
                        text(superscript("superscript"))
                        text(link("link", "https://detekt.dev/"))
                    }
                }
            }
        }
        orderedListItem("orderedListItem")
        codeBlock(
            """
                codeBlock
            """.trimIndent(),
            "kotlin"
        )
    }
}.toString()
