package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.test.TestConfig
import dev.detekt.test.assertThat
import dev.detekt.test.lint
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class StringShouldBeRawStringSpec {
    @ParameterizedTest(name = "does report for string {0} when max allowed escape is {1}")
    @MethodSource("getViolations")
    fun checkViolationsForStringVariableDeclaration(stringTemplate: String, maxEscapedCharacterCount: Int) {
        val code = """
            fun test() {
                val i = 0
                val a = "test"
                val value = $stringTemplate
            }
        """.trimIndent()
        val subject =
            StringShouldBeRawString(TestConfig(MAX_ESCAPED_CHARACTER_COUNT to maxEscapedCharacterCount))
        val findings = subject.lint(code)
        assertThat(findings).hasSize(1)
    }

    @ParameterizedTest(name = "does not report for raw string {0} when max allowed escape is {1}")
    @MethodSource("getNonViolations")
    fun checkNonViolationsForStringVariableDeclaration(stringTemplate: String, maxEscapedCharacterCount: Int) {
        val code = """
            fun test() {
                val i = 0
                val a = "test"
                val value = $stringTemplate
            }
        """.trimIndent()
        val subject =
            StringShouldBeRawString(TestConfig(MAX_ESCAPED_CHARACTER_COUNT to maxEscapedCharacterCount))
        val findings = subject.lint(code)
        assertThat(findings).isEmpty()
    }

    @ParameterizedTest(name = "does not report for string {0} as allowed char is {1}")
    @MethodSource("getNonViolationsDueToMaxEscapedCharacterCount")
    fun checkNonViolationsForStringVariableDeclarationDueToMaxAllowed(
        stringTemplate: String,
        maxEscapedCharacterCount: Int,
    ) {
        val code = """
            fun test() {
                val i = 0
                val a = "test"
                val value = $stringTemplate
            }
        """.trimIndent()
        val subject =
            StringShouldBeRawString(TestConfig(MAX_ESCAPED_CHARACTER_COUNT to maxEscapedCharacterCount))
        val findings = subject.lint(code)
        assertThat(findings).isEmpty()
    }

    @ParameterizedTest(name = "does report for string {0} as it exceeds max allowed char ({1}-1)")
    @MethodSource("getNonViolationsDueToMaxEscapedCharacterCount")
    fun checkNonViolationsForStringVariableDeclarationMoreThanMaxAllowed(
        stringTemplate: String,
        maxEscapedCharacterCount: Int,
    ) {
        val code = """
            fun test() {
                val i = 0
                val a = "test"
                val value = $stringTemplate
            }
        """.trimIndent()
        val subject =
            StringShouldBeRawString(TestConfig(MAX_ESCAPED_CHARACTER_COUNT to maxEscapedCharacterCount - 1))
        val findings = subject.lint(code)
        assertThat(findings).hasSize(1)
    }

    @ParameterizedTest(name = "does not report for string {0} as {2} characters are allowed")
    @MethodSource("getViolations", "getNonViolationsDueToMaxEscapedCharacterCount")
    @Suppress("UNUSED", "unused")
    fun checkNonViolationsForStringVariableDeclarationWithAllowedCharacters(
        stringTemplate: String,
        maxEscapedCharacterCount: Int,
        allowedCharacters: List<String>,
    ) {
        val code = """
            fun test() {
                val i = $maxEscapedCharacterCount
                val a = "test"
                val value = $stringTemplate
            }
        """.trimIndent()
        val subject = StringShouldBeRawString(
            TestConfig(
                MAX_ESCAPED_CHARACTER_COUNT to 0,
                IGNORED_CHARACTERS to allowedCharacters,
            )
        )
        val findings = subject.lint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report when variable contains double inverted character`() {
        val code = """
            fun test() {
                @Suppress("ObjectPropertyName")
                val `"a` = "Random string"
                val testString = "a${'$'}{`"a`}"
            }
        """.trimIndent()
        val subject = StringShouldBeRawString(TestConfig(MAX_ESCAPED_CHARACTER_COUNT to 0))
        val findings = subject.lint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does report in case of multiple strings with not a string concatenation operation`() {
        val code = """
            fun test() {
                val totalSize = "\n + \n".length + "\n + \n".length
            }
        """.trimIndent()
        val subject = StringShouldBeRawString(TestConfig(MAX_ESCAPED_CHARACTER_COUNT to 1))
        val findings = subject.lint(code)
        assertThat(findings).hasSize(2)
    }

    @Test
    fun `does report in case of multiple violations in multiple expressions`() {
        val code = """
            fun test() {
                val size1 = "\n + \n".length
                val size2 = "\n + \n".length
            }
        """.trimIndent()
        val subject = StringShouldBeRawString(TestConfig(MAX_ESCAPED_CHARACTER_COUNT to 1))
        val findings = subject.lint(code)
        assertThat(findings).hasSize(2)
    }

    @Test
    fun `does report in case of string has operator call with violations`() {
        val code = """
            operator fun String.not() = true
            val totalSize = !"\n\n"
        """.trimIndent()
        val subject = StringShouldBeRawString(TestConfig(MAX_ESCAPED_CHARACTER_COUNT to 1))
        val findings = subject.lint(code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `does report in case of string has function call with violations`() {
        val code = """
            operator fun String.not() = true
            val totalSize = "\n\n".not()
        """.trimIndent()
        val subject = StringShouldBeRawString(TestConfig(MAX_ESCAPED_CHARACTER_COUNT to 1))
        val findings = subject.lint(code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `does not report in case of string has function call`() {
        val code = """
            operator fun String.not() = true
            val totalSize = (!"\n\n").toString() + "\n"
        """.trimIndent()
        val subject = StringShouldBeRawString(TestConfig(MAX_ESCAPED_CHARACTER_COUNT to 2))
        val findings = subject.lint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report in case of of violations in same parent expression`() {
        val code = """
            operator fun String.not() = true
            val totalSize = (!"\n\n").toString() + "\n\n\n"
        """.trimIndent()
        val subject = StringShouldBeRawString(TestConfig(MAX_ESCAPED_CHARACTER_COUNT to 2))
        val findings = subject.lint(code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `does not report in case of violations with plus operator in same parent expression`() {
        val code = """
            operator fun String.not() = true
            val totalSize = (!"\n\n").toString() + "\n" + "\n"
        """.trimIndent()
        val subject = StringShouldBeRawString(TestConfig(MAX_ESCAPED_CHARACTER_COUNT to 1))
        val findings = subject.lint(code)
        assertThat(findings).hasSize(2)
    }

    @Test
    fun `does not report in case of violations with plus operator and paren at start in same parent expression`() {
        val code = """
            operator fun String.not() = true
            val totalSize = ((!"\n\n").toString() + "\n") + "\n"
        """.trimIndent()
        val subject = StringShouldBeRawString(TestConfig(MAX_ESCAPED_CHARACTER_COUNT to 1))
        val findings = subject.lint(code)
        assertThat(findings).hasSize(2)
    }

    @Test
    fun `does not report in case of violations with plus operator and paren at end in same parent expression`() {
        val code = """
            operator fun String.not() = true
            val totalSize = (!"\n\n").toString() + ("\n" + "\n")
        """.trimIndent()
        val subject = StringShouldBeRawString(TestConfig(MAX_ESCAPED_CHARACTER_COUNT to 1))
        val findings = subject.lint(code)
        assertThat(findings).hasSize(2)
    }

    @Test
    fun `does not report in case of violations with plus operator and paren at mid in same parent expression`() {
        val code = """
            operator fun String.not() = true
            val totalSize = (!"\n\n").toString() + ("\n" + "\n") + "\n"
        """.trimIndent()
        val subject = StringShouldBeRawString(TestConfig(MAX_ESCAPED_CHARACTER_COUNT to 1))
        val findings = subject.lint(code)
        assertThat(findings).hasSize(2)
    }

    @Test
    fun `does not report in case of violations with plus operator and paren at mid`() {
        val code = """
            operator fun String.not() = true
            val totalSize = (!"\n\n").toString() + ("\n" + "\n") + "\n"
        """.trimIndent()
        val subject = StringShouldBeRawString(TestConfig(MAX_ESCAPED_CHARACTER_COUNT to 2))
        val findings = subject.lint(code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `does report in case of function call in string concatenation with parenthesis`() {
        val code = """
            operator fun String.not() = true
            val totalSize = (!("\n\n" + "\n")).toString() + ("\n" + "\n") + "\n"
        """.trimIndent()
        val subject = StringShouldBeRawString(TestConfig(MAX_ESCAPED_CHARACTER_COUNT to 2))
        val findings = subject.lint(code)
        assertThat(findings).hasSize(2)
    }

    @Test
    fun `does not report in case of string has function call wrapped in parenthesis`() {
        val code = """
            operator fun String.not() = true
            val totalSize = ((!"\n\n").toString()) + "\n"
        """.trimIndent()
        val subject = StringShouldBeRawString(TestConfig(MAX_ESCAPED_CHARACTER_COUNT to 2))
        val findings = subject.lint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report in case of string has get operator call`() {
        val code = """
            operator fun String.get(index: Int) = this
            val totalSize = "\n\n"[0] + "\n"
        """.trimIndent()
        val subject = StringShouldBeRawString(TestConfig(MAX_ESCAPED_CHARACTER_COUNT to 2))
        val findings = subject.lint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report in case of string has get operator call wrapped in parenthesis`() {
        val code = """
            operator fun String.get(index: Int) = this
            val totalSize = ("\n\n"[0]) + "\n"
        """.trimIndent()
        val subject = StringShouldBeRawString(TestConfig(MAX_ESCAPED_CHARACTER_COUNT to 2))
        val findings = subject.lint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report in case of function call in string concatenation has less violations`() {
        val code = """
            fun test(string: String) = string
            fun test() {
                val finalString = "\n" + test("\n\n") + "\n"
            }
        """.trimIndent()
        val subject = StringShouldBeRawString(TestConfig(MAX_ESCAPED_CHARACTER_COUNT to 2))
        val findings = subject.lint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does report in case of function call in string concatenation more violations`() {
        val code = """
            fun test(string: String) = string
            fun test() {
                val finalString = "\n" + test("\n\n") + "\n"
            }
        """.trimIndent()
        val subject = StringShouldBeRawString(TestConfig(MAX_ESCAPED_CHARACTER_COUNT to 1))
        val findings = subject.lint(code)
        assertThat(findings).hasSize(2)
    }

    @Test
    fun `does not report in case of single line comment`() {
        val code = """
            fun test() {
                // As in case when multiple \n \n \n it checks those
                val size1 = "\nThis rule is awesome\n"
            }
        """.trimIndent()
        val subject = StringShouldBeRawString(TestConfig(MAX_ESCAPED_CHARACTER_COUNT to 2))
        val findings = subject.lint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report in case of block line comment`() {
        val code = """
            fun test() {
                /**
                * As in case when multiple \n \n \n it checks those
                */
                val size1 = "\nThis rule is awesome\n"
            }
        """.trimIndent()
        val subject = StringShouldBeRawString(TestConfig(MAX_ESCAPED_CHARACTER_COUNT to 2))
        val findings = subject.lint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does report in case of annotated string`() {
        val code = """
            fun test() {
                @Suppress("wierd annotation with\n")
                val size1 = "This rule is awesome"
            }
        """.trimIndent()
        val subject = StringShouldBeRawString(TestConfig(MAX_ESCAPED_CHARACTER_COUNT to 0))
        val findings = subject.lint(code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `does not report in case of multiline string in annotation`() {
        val code = """
            fun test() {
                @Suppress(""${'"'}wierd annotation with\n""${'"'})
                val size1 = "This rule is awesome"
            }
        """.trimIndent()
        val subject = StringShouldBeRawString(TestConfig(MAX_ESCAPED_CHARACTER_COUNT to 0))
        val findings = subject.lint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report when replaceIndent is used - #6145`() {
        val code = """
            fun test() {    
                val x = ""${'"'}
                    ...
                ""${'"'}.replaceIndent("\t\t\t\t")
            }
        """.trimIndent()
        val subject = StringShouldBeRawString(TestConfig(MAX_ESCAPED_CHARACTER_COUNT to 0))
        val findings = subject.lint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report when replaceIndent is used but report other expression inside it`() {
        val code = """
            fun test() {    
                val x = ""${'"'}
                    ...
                ""${'"'}.replaceIndent("\t\t\t\t".also { 
                        "\t a \n" 
                    }
                )
            }
        """.trimIndent()
        val subject = StringShouldBeRawString(TestConfig(MAX_ESCAPED_CHARACTER_COUNT to 0))
        val findings = subject.lint(code)
        assertThat(findings).singleElement()
            .hasSourceLocation(5, 13)
    }

    @Test
    fun `does not report when prependIndent is used - #6145`() {
        val code = """
            fun test() {    
                val x = ""${'"'}
                    ...
                ""${'"'}.trimIndent()
                val usage = ""${'"'}
                    ...
                    ${'$'}{x.prependIndent("\t\t\t\t")}
                ""${'"'}
            }
        """.trimIndent()
        val subject = StringShouldBeRawString(TestConfig(MAX_ESCAPED_CHARACTER_COUNT to 0))
        val findings = subject.lint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report when only quotes is present - #6145`() {
        val code = """
            fun test() {    
                val triplet = "\"\"\""
                val sextuple = "\"\"\"\"\"\""
                val quadrupleQuintuplePair = "\"\"\"\"" + "\"\"\"\"\""
            }
        """.trimIndent()
        val subject = StringShouldBeRawString(TestConfig(MAX_ESCAPED_CHARACTER_COUNT to 0))
        val findings = subject.lint(code)
        assertThat(findings).isEmpty()
    }

    companion object {
        private const val MAX_ESCAPED_CHARACTER_COUNT = "maxEscapedCharacterCount"
        private const val IGNORED_CHARACTERS = "ignoredCharacters"

        @Suppress("LongMethod")
        @JvmStatic
        fun getViolations() = listOf(
            Arguments.of(""""\"[^\"\\\\\\n]*(?:\\\\.[^\"\\\\\\n]*)*\""""", 0, listOf("\\\\", "\\n", "\\\"")),
            Arguments.of(
                """
                    "{\n" +
                    "  \"window\": {\n" +
                    "    \"title\": \"Sample Quantum With AI and ML Widget\",\n" +
                    "    \"name\": \"main_window\",\n" +
                    "    \"width\": 500,\n" +
                    "    \"height\": 500\n" +
                    "  }\n" +
                    "}"
                """.trimIndent(),
                2,
                listOf("\\n", "\\\""),
            ),
            Arguments.of(
                """
                        "{\n" +
                    "  \"window\": {\n" +
                    "    \"title\": \"Sample Quantum With AI and ML Widget\",\n" +
                    "    \"name\": \"main_window\",\n" +
                    "    \"width\": 500,\n" +
                    "    \"height\": 500\n" +
                    "  }\n" +
                    "}"
                """.trimIndent(),
                2,
                listOf("\\n", "\\\""),
            ),
            Arguments.of(
                """
                    |        "{\n" +
                        |"  \"window\": {\n" +
                      |  "    \"title\": \"Sample Quantum With AI and ML Widget\",\n" +
                     |   "    \"name\": \"main_window\",\n" +
                       | "    \"width\": 500,\n" +
                      |  "    \"height\": 500\n" +
                        "  }\n" +
                        "}"
                """.trimMargin(),
                2,
                listOf("\\n", "\\\""),
            ),
            Arguments.of(
                """
                    "abc" + 
                    
                    
                    
                    "efg" + 
                    
                    
                    "\n\n\n"
                """.trimIndent(),
                0,
                listOf("\\n")
            ),
            Arguments.of(""""In java new line char is \n"""", 0, listOf("\\n")),
            Arguments.of("""("In java new line char is \n")""", 0, listOf("\\n")),
            Arguments.of("""("\n") + ("\n") + ("\n")""", 0, listOf("\\n")),
            Arguments.of("""("\n" + "\n") + ("\n" + "\n")""", 0, listOf("\\n")),
            Arguments.of("""("\n") + ("\n") + ((("\n")))""", 0, listOf("\\n")),
            Arguments.of("""("\n") + ((("\n"))) + ("\n")""", 0, listOf("\\n")),
            Arguments.of("""((("\n"))) + ("\n") + ("\n")""", 0, listOf("\\n")),
            Arguments.of(
                """
                                ("\n") + (
                        ("\n") + ("\n")
                    )
                """.trimIndent(),
                2,
                listOf("\\n"),
            ),
            Arguments.of(
                """
                    (
                        ("\n") + ("\n")
                    )  + ("\n")
                """.trimIndent(),
                0,
                listOf("\\n"),
            ),
            Arguments.of(""""\n \\".isEmpty()""", 0, listOf("\\n", "\\\\")),
        )

        @Suppress("LongMethod")
        @JvmStatic
        fun getNonViolations() = listOf(
            Arguments.of("""""${'"'}\t\t\t""${'"'}""", 0),
            Arguments.of("""""${'"'}\\\""${'"'}""", 0),
            Arguments.of("""""${'"'}Normal and long string""${'"'}""", 0),
            Arguments.of("\"Normal and long string\"", 0),
            Arguments.of("""""${'"'}In java new line char is \n""${'"'}""", 0),
            Arguments.of("""""${'"'}This is point number ${'$'}i In java new line char is \n""${'"'}""", 0),
            Arguments.of(
                """
                    ""${'"'}
                    abc
                    
                    efg
                    
                    hij
                    ""${'"'}
                """.trimIndent(),
                2
            ),
            Arguments.of(
                """
                    ""${'"'}
                      {
                          "window": {
                            "title": "Sample Quantum With AI and ML Widget",
                            "name": "main_window",
                            "width": 500,
                            "height": 500
                          }
                      }
                    ""${'"'}
                """.trimIndent(),
                0
            ),
            Arguments.of(
                """
                    "abc" + 
                    
                    
                    
                    "efg" + 
                    
                    
                    "hij"
                """.trimIndent(),
                0
            ),
        )

        @Suppress("LongMethod")
        @JvmStatic
        fun getNonViolationsDueToMaxEscapedCharacterCount() = listOf(
            Arguments.of(""""\t\t\t"""", 3, listOf("\\t")),
            Arguments.of(""""\\\\\\"""", 3, listOf("\\\\")),
            Arguments.of(""" "abc" + "\n" + "efg" + "\n" + "hij\n"  """, 3, listOf("\\n")),
            Arguments.of(""""\"[^\"\\\\\\n]*(?:\\\\.[^\"\\\\\\n]*)*\""""", 12, listOf("\\n", "\\\\", "\\\"")),
            Arguments.of(""""In java new line char is \n"""", 1, listOf("\\n")),
            Arguments.of(""""This is point number ${'$'}i In java new line char is \n"""", 1, listOf("\\n")),
            Arguments.of(""" "abc" + "\n" + "efg" + "\n" + "hij"  """, 2, listOf("\\n")),
            Arguments.of(
                """
                    "{\n" +
                    "  \"window\": {\n" +
                    "    \"title\": \"Sample Quantum With AI and ML Widget\",\n" +
                    "    \"name\": \"main_window\",\n" +
                    "    \"width\": 500,\n" +
                    "    \"height\": 500\n" +
                    "  }\n" +
                    "}"
                """.trimIndent(),
                21,
                listOf("\\n", "\\\""),
            ),
            Arguments.of("""("\n") + ("\n") + ("\n")""", 3, listOf("\\n")),
            Arguments.of("""("\n") + ("\n") + ((("\n")))""", 3, listOf("\\n")),
            Arguments.of(
                """
                                ("\n") + (
                        ("\n") + ("\n")
                    )
                """.trimIndent(),
                3,
                listOf("\\n"),
            ),
            Arguments.of(""""\n + \n" + ""${'"'}\n\n""${'"'} + "\n + \n"""", 4, listOf("\\n")),
            Arguments.of("""""${'"'}\n\n""${'"'} + "\n + \n" + "\n + \n"""", 4, listOf("\\n")),
            Arguments.of(""""\n + \n" + "\n + \n" + ""${'"'}\n\n""${'"'}""", 4, listOf("\\n")),
            Arguments.of(""""\n\n" + a + "\n"""", 3, listOf("\\n")),
            Arguments.of(""""\n" + "\n\n" + a""", 3, listOf("\\n")),
            Arguments.of("""a + "\n\n" + "\n\n"""", 4, listOf("\\n")),
        )
    }
}
