@file:Suppress("ClassName")

package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.ValueWithReason
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.toConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.Locale

private const val COMMENTS = "comments"
private const val ALLOWED_PATTERNS = "allowedPatterns"

class ForbiddenCommentSpec {
    @Nested
    inner class `the default values are configured` {

        @Test
        @DisplayName("should report TODO: usages")
        fun reportTodoColon() {
            val findings =
                ForbiddenComment(Config.empty).compileAndLint("// TODO: I need to fix this.")
            assertThat(findings).singleElement()
                .hasMessage("Forbidden TODO todo marker in comment, please do the changes.")
        }

        @Test
        fun `should not report TODO usages`() {
            val findings =
                ForbiddenComment(Config.empty).compileAndLint("// TODO I need to fix this.")
            assertThat(findings).isEmpty()
        }

        @Test
        @DisplayName("should report FIXME: usages")
        fun reportFixMe() {
            val findings =
                ForbiddenComment(Config.empty).compileAndLint("// FIXME: I need to fix this.")
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should not report FIXME usages`() {
            val findings =
                ForbiddenComment(Config.empty).compileAndLint("// FIXME I need to fix this.")
            assertThat(findings).isEmpty()
        }

        @Test
        @DisplayName("should report STOPSHIP: usages")
        fun reportStopShipColon() {
            val findings =
                ForbiddenComment(Config.empty).compileAndLint("// STOPSHIP: I need to fix this.")
            assertThat(findings).singleElement().hasMessage(
                "Forbidden STOPSHIP todo marker in comment, please address the problem before shipping the code."
            )
        }

        @Test
        fun `should not report STOPSHIP usages`() {
            val findings =
                ForbiddenComment(Config.empty).compileAndLint("// STOPSHIP I need to fix this.")
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should report violation in multiline comment`() {
            val code = """
                /*
                 TODO: I need to fix this.
                 */
            """.trimIndent()
            val findings = ForbiddenComment(Config.empty).compileAndLint(code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should report violation in single line block comment`() {
            val code = """
                /*TODO: I need to fix this.*/
            """.trimIndent()
            val findings = ForbiddenComment(Config.empty).compileAndLint(code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should report violation in KDoc`() {
            val code = """
                /**
                 * TODO: I need to fix this.
                 */
                class A {
                    /**
                     * TODO: I need to fix this.
                     */
                }
            """.trimIndent()
            val findings = ForbiddenComment(Config.empty).compileAndLint(code)
            assertThat(findings).hasSize(2)
        }

        @Test
        fun `should report violation in star aligned comment`() {
            val code = """
                /*
                 * TODO: I need to fix this.
                 */
                class A
            """.trimIndent()
            val findings = ForbiddenComment(Config.empty).compileAndLint(code)
            assertThat(findings).hasSize(1)
        }
    }

    @Nested
    inner class `custom default values are configured` {
        val banana = "// Banana."

        @Nested
        inner class `when given Banana` {
            val config = TestConfig(COMMENTS to listOf("Banana"))

            @Test
            @DisplayName("should not report TODO: usages")
            fun todoColon() {
                val findings =
                    ForbiddenComment(config).compileAndLint("// TODO: I need to fix this.")
                assertThat(findings).isEmpty()
            }

            @Test
            @DisplayName("should not report FIXME: usages")
            fun fixmeColon() {
                val findings =
                    ForbiddenComment(config).compileAndLint("// FIXME: I need to fix this.")
                assertThat(findings).isEmpty()
            }

            @Test
            @DisplayName("should not report STOPME: usages")
            fun stopShipColon() {
                val findings =
                    ForbiddenComment(config).compileAndLint("// STOPSHIP: I need to fix this.")
                assertThat(findings).isEmpty()
            }

            @Test
            fun `should report Banana usages`() {
                val findings = ForbiddenComment(config).compileAndLint(banana)
                assertThat(findings).hasSize(1)
            }

            @Test
            fun `should report Banana usages regardless of case sensitive`() {
                val forbiddenComment =
                    ForbiddenComment(TestConfig(COMMENTS to listOf("(?i)bAnAnA")))
                val findings = forbiddenComment.compileAndLint(banana)
                assertThat(findings).hasSize(1)
            }
        }

        @Nested
        @DisplayName("when given listOf(\"banana\")")
        inner class ListOfBanana {
            val config = TestConfig(COMMENTS to listOf("Banana"))

            @Test
            @DisplayName("should not report TODO: usages")
            fun todoColon() {
                val findings =
                    ForbiddenComment(config).compileAndLint("// TODO: I need to fix this.")
                assertThat(findings).isEmpty()
            }

            @Test
            @DisplayName("should not report FIXME: usages")
            fun fixmeColon() {
                val findings =
                    ForbiddenComment(config).compileAndLint("// FIXME: I need to fix this.")
                assertThat(findings).isEmpty()
            }

            @Test
            @DisplayName("should not report STOPME: usages")
            fun stopShipColon() {
                val findings =
                    ForbiddenComment(config).compileAndLint("// STOPSHIP: I need to fix this.")
                assertThat(findings).isEmpty()
            }

            @Test
            fun `should report Banana usages`() {
                val findings = ForbiddenComment(config).compileAndLint(banana)
                assertThat(findings).hasSize(1)
            }
        }
    }

    @Nested
    inner class `custom default values with allowed patterns are configured` {

        private val patternsConfig = TestConfig(
            ALLOWED_PATTERNS to "Ticket|Task",
        )

        @Test
        fun `should not report Comment usages when any one pattern is present`() {
            val comment = "// Comment Ticket:234."
            val findings = ForbiddenComment(patternsConfig).compileAndLint(comment)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not report Comment usages when all patterns are present`() {
            val comment = "// Comment Ticket:123 Task:456 comment."
            val findings = ForbiddenComment(patternsConfig).compileAndLint(comment)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `custom message is configured` {
        private val messageConfig = TestConfig(
            COMMENTS to listOf("Comment"),
        )

        @Test
        fun `should report a Finding with message 'Custom Message'`() {
            val comment = "// Comment"
            val findings = ForbiddenComment(messageConfig).compileAndLint(comment)
            assertThat(findings).hasSize(1)
            assertThat(findings.first().message)
                .isEqualTo("This comment contains 'Comment' that has been defined as forbidden.")
        }
    }

    @Nested
    inner class `custom message is not configured` {
        private val messageWithReasonConfig = TestConfig(
            COMMENTS to listOf(ValueWithReason("Comment", "Comment is disallowed").toConfig())
        )

        @Test
        fun `should report a Finding with reason`() {
            val comment = "// Comment"
            val findings = ForbiddenComment(messageWithReasonConfig).compileAndLint(comment)
            assertThat(findings).hasSize(1)
            assertThat(findings.first().message).isEqualTo("Comment is disallowed")
        }
    }

    @Nested
    inner class `custom value pattern is configured` {
        private val patternStr = """^(?i)REVIEW\b"""
        private val messageConfig = TestConfig(
            COMMENTS to listOf("STOPSHIP", patternStr),
        )

        @Test
        fun `should not report a finding when review doesn't match the pattern`() {
            val comment = "// to express in the preview that it's not a normal TextView."
            val findings = ForbiddenComment(messageConfig).compileAndLint(comment)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should report a finding when STOPSHIP is present`() {
            val comment = "// STOPSHIP to express in the preview that it's not a normal TextView."
            val findings = ForbiddenComment(messageConfig).compileAndLint(comment)
            assertThat(findings).hasSize(1)
            assertThat(findings).singleElement()
                .hasMessage(
                    String.format(
                        Locale.ROOT,
                        ForbiddenComment.DEFAULT_ERROR_MESSAGE,
                        "STOPSHIP"
                    )
                )
        }

        @Test
        fun `should report a finding when review pattern is matched with comment with leading space`() {
            val comment = "// REVIEW foo -> flag"
            val findings = ForbiddenComment(messageConfig).compileAndLint(comment)
            assertThat(findings).hasSize(1)
            assertThat(findings).singleElement()
                .hasMessage(
                    String.format(
                        Locale.ROOT,
                        ForbiddenComment.DEFAULT_ERROR_MESSAGE,
                        patternStr
                    )
                )
        }

        @Test
        fun `should report a finding when review pattern is matched with comment with out leading space`() {
            val comment = "//REVIEW foo -> flag"
            val findings = ForbiddenComment(messageConfig).compileAndLint(comment)
            assertThat(findings).hasSize(1)
            assertThat(findings).singleElement()
                .hasMessage(
                    String.format(
                        Locale.ROOT,
                        ForbiddenComment.DEFAULT_ERROR_MESSAGE,
                        patternStr
                    )
                )
        }

        @Test
        fun `should report a finding matching two patterns`() {
            val comment = "// REVIEW foo -> flag STOPSHIP"
            val findings = ForbiddenComment(messageConfig).compileAndLint(comment)
            assertThat(findings).hasSize(2)
        }

        @Test
        fun `should report a finding matching a pattern contained in the comment`() {
            val comment = "// foo STOPSHIP bar"
            val findings = ForbiddenComment(messageConfig).compileAndLint(comment)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should report a finding matching a pattern contained in multiple single line comments`() {
            val comment = """
                // foo STOPSHIP bar
                // foo STOPSHIP bar
                // foo STOPSHIP bar
            """.trimIndent()
            val findings = ForbiddenComment(messageConfig).compileAndLint(comment)
            assertThat(findings).hasSize(3)
        }
    }

    @Nested
    inner class `comment on indented code` {
        private val patternStr = "^ "
        private val messageConfig = TestConfig(
            COMMENTS to listOf(patternStr),
        )

        @Test
        fun `should report a finding when leading extra space is not allowed`() {
            val comment = """
                class A {
                    //  comment
                    val a = 0
                }
            """.trimIndent()
            val findings = ForbiddenComment(messageConfig).compileAndLint(comment)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should not report a finding when leading extra space is not allowed with no leading space`() {
            val comment = """
                class A {
                    // comment with space in between
                    val a = 0
                }
            """.trimIndent()
            val findings = ForbiddenComment(messageConfig).compileAndLint(comment)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not report a finding when leading extra space is not allowed in multiline comment`() {
            val comment = """
                class A {
                    /*
                    * comment with space in between
                    * comment with space in between
                    */
                    val a = 0
                }
            """.trimIndent()
            val findings = ForbiddenComment(messageConfig).compileAndLint(comment)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should report a finding when leading extra space is not allowed in multiline comment with leading space`() {
            val comment = """
                class A {
                    /*
                    *  comment
                    */
                    val a = 0
                }
            """.trimIndent()
            val findings = ForbiddenComment(messageConfig).compileAndLint(comment)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should not report a finding when leading extra space is not allowed in star aligned comment`() {
            val comment = """
                class A {
                    /*
                     * comment
                     */
                    val a = 0
                }
            """.trimIndent()
            val findings = ForbiddenComment(messageConfig).compileAndLint(comment)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should report a finding when leading space is not allowed in star aligned comment with leading space`() {
            val comment = """
                class A {
                    /*
                     *  comment
                     */
                    val a = 0
                }
            """.trimIndent()
            val findings = ForbiddenComment(messageConfig).compileAndLint(comment)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should report a finding when leading space is not allowed in multiline block comment with leading space`() {
            val comment = """
                class A {
                    /*
                         comment
                     */
                    val a = 0
                }
            """.trimIndent()
            val findings = ForbiddenComment("^    comment").compileAndLint(comment)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should report a finding when negative leading space is not allowed in multiline block comment with leading space`() {
            val comment = """
                class A {
                    /*
                  comment
                     */
                    val a = 0
                }
            """.trimIndent()
            val findings = ForbiddenComment("^comment").compileAndLint(comment)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should report a finding start space contained in a single line comment`() {
            val comment = """
                class a {
                    fun test() {
                        //  foo
                        val a = 0
                    }
                }
            """.trimIndent()
            val findings = ForbiddenComment("^ foo").compileAndLint(comment)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should not report when not finding start contained in a single line comment`() {
            val comment = """
                class a {
                    fun test() {
                        // foo
                        val a = 0
                    }
                }
            """.trimIndent()
            val findings = ForbiddenComment("^ foo").compileAndLint(comment)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should report a finding when whole comment is not allowed`() {
            val comment = """
                class A {
                    // stopship
                    val a = 0
                }
            """.trimIndent()
            val findings = ForbiddenComment("stopship").compileAndLint(comment)
            assertThat(findings).hasSize(1)
        }
    }

    @Nested
    inner class `mixed code and comment lines` {

        @Test
        fun `should report a finding in trailing single line comment`() {
            val comment = """
                fun f() {} // TODO implement
            """.trimIndent()
            val findings = ForbiddenComment("TODO").compileAndLint(comment)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should report a finding when multiline comment exists before code`() {
            val comment = """
                class A {
                    /*public*/ fun f() {}
                }
            """.trimIndent()
            val findings = ForbiddenComment("public").compileAndLint(comment)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should report a finding when multiline comment exists inside code`() {
            val comment = """
                class A {
                    fun f() /*: String*/ {}
                }
            """.trimIndent()
            val findings = ForbiddenComment("^: .+$").compileAndLint(comment)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should report a finding when multiline comment exists after code`() {
            val comment = """
                class A {
                    fun f(): String = /*error("foo")*/
                        TODO()
                }
            """.trimIndent()
            val findings = ForbiddenComment("^error").compileAndLint(comment)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should report a finding when kdoc comment exists before code`() {
            val comment = """
                class A {
                    /**public*/ fun f() {}
                }
            """.trimIndent()
            val findings = ForbiddenComment("public").compileAndLint(comment)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should report a finding when kdoc comment exists inside code`() {
            val comment = """
                class A {
                    fun f() /**: String*/ {}
                }
            """.trimIndent()
            val findings = ForbiddenComment("^: .+$").compileAndLint(comment)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should report a finding when kdoc comment exists after code`() {
            val comment = """
                class A {
                    fun f(): String = /**error("foo")*/
                        TODO()
                }
            """.trimIndent()
            val findings = ForbiddenComment("^error").compileAndLint(comment)
            assertThat(findings).hasSize(1)
        }
    }

    @Nested
    inner class `regex semantics for comments` {

        @Test
        fun `should report a finding at the beginning of lines`() {
            val comment = """
                /*
                 * foo bar
                 * baz qux
                 */
            """.trimIndent()
            val findings = ForbiddenComment("^baz").compileAndLint(comment)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should not report a finding at the beginning of lines when the flag is off`() {
            val comment = """
                /*
                 * foo bar
                 * baz qux
                 */
            """.trimIndent()
            val findings = ForbiddenComment("(?-m)^baz").compileAndLint(comment)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should report a finding at the end of lines`() {
            val comment = """
                /*
                 * foo bar
                 * baz qux
                 */
            """.trimIndent()
            val findings = ForbiddenComment("bar$").compileAndLint(comment)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should not report a finding at the end of lines when flag is off`() {
            val comment = """
                /*
                 * foo bar
                 * baz qux
                 */
            """.trimIndent()
            val findings = ForbiddenComment("(?-m)bar$").compileAndLint(comment)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should report a finding at the beginning of a comment`() {
            val comment = """
                /*
                 * foo bar
                 * baz qux
                 */
            """.trimIndent()
            val findings1 = ForbiddenComment("^foo").compileAndLint(comment)
            assertThat(findings1).hasSize(1)
            val findings2 = ForbiddenComment("\\Afoo").compileAndLint(comment)
            assertThat(findings2).hasSize(1)
        }

        @Test
        fun `should report a finding at the end of a comment`() {
            val comment = """
                /*
                 * foo bar
                 * baz qux
                 */
            """.trimIndent()
            val findings1 = ForbiddenComment("qux$").compileAndLint(comment)
            assertThat(findings1).hasSize(1)
            val findings2 = ForbiddenComment("qux\\Z").compileAndLint(comment)
            assertThat(findings2).hasSize(1)
        }

        @Test
        fun `should report a finding across lines`() {
            val comment = """
                /*
                 * foo bar
                 * baz qux
                 */
            """.trimIndent()
            val findings = ForbiddenComment("^foo.*qux$").compileAndLint(comment)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should not report a finding across lines when the flag is off`() {
            val comment = """
                /*
                 * foo bar
                 * baz qux
                 */
            """.trimIndent()
            val findings = ForbiddenComment("(?-s)^foo.*qux$").compileAndLint(comment)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should report all separate findings at once`() {
            val comment = """
                /*
                 * foo baz
                 * bar qux
                 */
            """.trimIndent()
            val findings = ForbiddenComment("^foo", "^bar").compileAndLint(comment)
            assertThat(findings).hasSize(2)
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    inner class `comment getContent` {

        @Suppress("LongMethod", "UnusedPrivateFunction")
        private fun getCommentsContentArguments() = listOf(
            Arguments.of("// comment", "comment"),
            Arguments.of("//  comment", " comment"),
            Arguments.of("//comment", "comment"),
            Arguments.of("// ", ""),
            Arguments.of("//  ", " "),
            Arguments.of("/* comment */", "comment"),
            Arguments.of("/*  comment  */", " comment "),
            Arguments.of("/* */", ""),
            Arguments.of("/*  */", ""),
            Arguments.of("/*comment*/", "comment"),
            Arguments.of("/*** comment ***/", "** comment **"),
            Arguments.of(
                """
                    /*
                    * good
                    * good
                    */
                """.trimIndent(),
                """
                    good
                    good
                    
                """.trimIndent()
            ),
            Arguments.of(
                """
                    /*
                    *bad
                    * good
                    */
                """.trimIndent(),
                """
                    bad
                    good
                    
                """.trimIndent()
            ),
            Arguments.of(
                """
                    /*
                    *bad
                    *   good
                    */
                """.trimIndent(),
                """
                    bad
                      good
                    
                """.trimIndent()
            ),
            Arguments.of(
                """
                    /*
                    comment
                    * a
                    * b
                    * c*/
                """.trimIndent(),
                """
                    comment
                    a
                    b
                    c
                """.trimIndent()
            ),
            Arguments.of(
                """
                    /*comment
                    * a
                    * b
                    * c*/
                """.trimIndent(),
                """
                    comment
                    a
                    b
                    c
                """.trimIndent()
            ),
            Arguments.of(
                """
                    /*  comment
                    * a
                    * b
                    * c  */
                """.trimIndent(),
                """
                     comment
                    a
                    b
                    c 
                """.trimIndent()
            ),
            Arguments.of(
                """
                    /*
                     * good
                     * good
                     */
                """.trimIndent(),
                """
                    good
                    good
                    
                """.trimIndent()
            ),
            Arguments.of(
                """
                    /*
                     *bad
                     * good
                     */
                """.trimIndent(),
                """
                    bad
                    good
                    
                """.trimIndent()
            ),
            Arguments.of(
                """
                    /*
                     *bad
                     *   good
                     */
                """.trimIndent(),
                """
                    bad
                      good
                    
                """.trimIndent()
            ),
            Arguments.of(
                """
                    /*
                       comment
                     * a
                     * b
                     * c*/
                """.trimIndent(),
                """
                      comment
                    a
                    b
                    c
                """.trimIndent()
            ),
            Arguments.of(
                """
                    /*comment
                     * a
                     * b
                     * c*/
                """.trimIndent(),
                """
                    comment
                    a
                    b
                    c
                """.trimIndent()
            ),
            Arguments.of(
                """
                    /*
                    a
                    b
                    c
                    */
                """.trimIndent(),
                """
                    a
                    b
                    c
                    
                """.trimIndent()
            ),
            Arguments.of(
                """
                    /*
                    a
                    b

                    c
                    */
                """.trimIndent(),
                """
                    a
                    b

                    c
                    
                """.trimIndent()
            ),
            Arguments.of(
                """
                    /*
                    
                    
                    */
                """.trimIndent(),
                "".trimIndent()
            ),
            Arguments.of(
                """
                    /*
                    
                    a
                    
                    */
                """.trimIndent(),
                "a\n\n"
            ),
            Arguments.of(
                """
                    /*
                    
                    
                     */
                """.trimIndent(),
                "".trimIndent()
            ),
            Arguments.of(
                """
                    /*
                    
                      a
                    
                     */
                """.trimIndent(),
                " a\n\n"
            ),
            Arguments.of(
                """
                     /*
                    a
                      b
                        c
                      */
                """.trimIndent(),
                """
                    a
                      b
                        c
                     
                """.trimIndent()
            ),
            Arguments.of(
                """
                    /*
                    
                     * a
                    
                     */
                """.trimIndent(),
                "a\n\n"
            ),
            Arguments.of(
                """
                    /*
                     * foo
                     * bar
                     * baz
                    */
                """.trimIndent(),
                """
                    foo
                    bar
                    baz
                    
                """.trimIndent()
            ),
            Arguments.of(
                """
                    /*
                     * foo
                    * bar
                     * baz
                     */
                """.trimIndent(),
                """
                    foo
                    bar
                    baz
                    
                """.trimIndent()
            ),
            Arguments.of(
                """
                      /*

                    a
                     b
                      c
                       d
                       *e
                       * f
                       *  g
                         * h
                        *   i
                       */
                """.trimIndent(),
                """
                    a
                     b
                      c
                       d
                    e
                    f
                     g
                    h
                      i
                      
                """.trimIndent()
            ),
        )

        @ParameterizedTest(name = "Given {0} comment, getContent return {1}")
        @MethodSource("getCommentsContentArguments")
        fun test(comment: String, content: String) {
            assertThat(comment.getCommentContent()).isEqualTo(content)
        }
    }
}

@Suppress("TestFunctionName") // This is a factory function for ForbiddenComment
private fun ForbiddenComment(vararg comments: String): ForbiddenComment =
    ForbiddenComment(TestConfig(COMMENTS to comments.toList()))
