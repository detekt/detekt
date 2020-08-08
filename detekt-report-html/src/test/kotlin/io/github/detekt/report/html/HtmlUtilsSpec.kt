package io.github.detekt.report.html

import io.gitlab.arturbosch.detekt.api.SourceLocation
import kotlinx.html.div
import kotlinx.html.stream.createHTML
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class HtmlUtilsSpec : Spek({

    describe("HTML snippet code") {
        val code by memoized {
            """
            package cases
            // reports 1 - line with just one space

            // reports 1 - a comment with trailing space
            // A comment
            // reports 1
            class TrailingWhitespacePositive {
                // reports 1 - line with just one tab

                // reports 1
                fun myFunction() {
                    // reports 1 - line with 1 trailing tab
                    println("A message")
                // reports 1
                }
            }
            """.trimIndent().splitToSequence('\n')
        }

        it("all line") {
            val snippet = createHTML().div() {
                snippetCode("ruleName", code.asSequence(), SourceLocation(7, 1), 34)
            }

            assertThat(snippet).isEqualTo("""
                <div>
                  <pre><code><span class="lineno">   4 </span>// reports 1 - a comment with trailing space
                <span class="lineno">   5 </span>// A comment
                <span class="lineno">   6 </span>// reports 1
                <span class="lineno">   7 </span><span class="error">class TrailingWhitespacePositive {</span>
                <span class="lineno">   8 </span>    // reports 1 - line with just one tab
                <span class="lineno">   9 </span>
                <span class="lineno">  10 </span>    // reports 1
                </code></pre>
                </div>

                """.trimIndent()
            )
        }

        it("part of line") {
            val snippet = createHTML().div() {
                snippetCode("ruleName", code.asSequence(), SourceLocation(7, 7), 26)
            }

            assertThat(snippet).isEqualTo("""
                <div>
                  <pre><code><span class="lineno">   4 </span>// reports 1 - a comment with trailing space
                <span class="lineno">   5 </span>// A comment
                <span class="lineno">   6 </span>// reports 1
                <span class="lineno">   7 </span>class <span class="error">TrailingWhitespacePositive</span> {
                <span class="lineno">   8 </span>    // reports 1 - line with just one tab
                <span class="lineno">   9 </span>
                <span class="lineno">  10 </span>    // reports 1
                </code></pre>
                </div>

                """.trimIndent()
            )
        }

        it("more than one line") {
            val snippet = createHTML().div() {
                snippetCode("ruleName", code.asSequence(), SourceLocation(7, 7), 66)
            }

            assertThat(snippet).isEqualTo("""
                <div>
                  <pre><code><span class="lineno">   4 </span>// reports 1 - a comment with trailing space
                <span class="lineno">   5 </span>// A comment
                <span class="lineno">   6 </span>// reports 1
                <span class="lineno">   7 </span>class <span class="error">TrailingWhitespacePositive {</span>
                <span class="lineno">   8 </span><span class="error">    // reports 1 - line with just one</span> tab
                <span class="lineno">   9 </span>
                <span class="lineno">  10 </span>    // reports 1
                </code></pre>
                </div>

                """.trimIndent()
            )
        }

        it("first line") {
            val snippet = createHTML().div() {
                snippetCode("ruleName", code.asSequence(), SourceLocation(1, 1), 1)
            }

            assertThat(snippet).contains((1..4).map { "  $it " })
        }

        it("second line") {
            val snippet = createHTML().div() {
                snippetCode("ruleName", code.asSequence(), SourceLocation(2, 1), 1)
            }

            assertThat(snippet).contains((1..5).map { "  $it " })
        }

        it("penultimate line") {
            val snippet = createHTML().div() {
                snippetCode("ruleName", code.asSequence(), SourceLocation(15, 1), 1)
            }

            assertThat(snippet).contains((12..16).map { "  $it " })
        }

        it("last line") {
            val snippet = createHTML().div() {
                snippetCode("ruleName", code.asSequence(), SourceLocation(16, 1), 1)
            }

            assertThat(snippet).contains((13..16).map { "  $it " })
        }

        it("when we provide an invalid source location the exception div is shown") {
            val snippet = createHTML().div() {
                snippetCode("ruleName", code.asSequence(), SourceLocation(7, 100), 1)
            }

            assertThat(snippet).contains("""<div class="exception">""")
        }
    }
})
