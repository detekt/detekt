package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private const val MAX_LINE_LENGTH = "maxLineLength"
private const val EXCLUDE_PACKAGE_STATEMENTS = "excludePackageStatements"
private const val EXCLUDE_IMPORT_STATEMENTS = "excludeImportStatements"
private const val EXCLUDE_COMMENT_STATEMENTS = "excludeCommentStatements"
private const val EXCLUDE_RAW_STRINGS = "excludeRawStrings"

class MaxLineLengthSpec {

    @Nested
    inner class `a kt file with some long lines` {
        private val code = """
            class MaxLineLength {
                companion object {
                    val LOREM_IPSUM = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua."
            
                    val A_VERY_LONG_MULTI_LINE = $TQ
                        This is another very very very very very very very very, very long multiline String that will break the MaxLineLength"
                    $TQ.trimIndent()
                }
            
                val loremIpsumField = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua."
            
                val longMultiLineField = $TQ
                        This is another very very very very very very very very
                        very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very
                        very long multiline String that will break the MaxLineLength
                    $TQ.trimIndent()
            
                val longMultiLineFieldWithLineBreaks =
                    $TQ
                        This is another very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very
                        very long multiline String with Line Break that will break the MaxLineLength
                    $TQ.trimIndent()
            
                val longMultiLineFieldWithLeadingQuote =
                    $TQ
                        "This is yet another very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very"
                        "very long multiline String with Line Break that will break the MaxLineLength"
                    $TQ.trimIndent()
            
                fun main() {
                    val thisIsAVeryLongValName = "This is a very, very long String that will break the MaxLineLength"
            
                    if (thisIsAVeryLongValName.length > "This is not quite as long of a String".length) {
                        println("It's indeed a very long String")
                    }
            
                    val hello = anIncrediblyLongAndComplexMethodNameThatProbablyShouldBeMuchShorterButForTheSakeOfTheTestItsNot()
                    val loremIpsum = getLoremIpsum()
            
                    println(hello)
                    println(loremIpsum)
            
                }
            
                // https://longurlmaker.com/go?id=z4Is.gd8c11rangy50farawayRedirx1drawn%2Bout60c3protractedTinyLinkstringylingeringfar%2BreachinglMyURLSHurl86018lengthy7frunningoutstretched1111ilengthy2xSimURLSmallr800xSimURL361juEasyURLSimURL0022faraway1095xhighfar%2Boff1618sustained0Shima8961toutstretchedexpanded0stretch611220drawn%2BoutdwkTightURL8kDoioplongish10Xil14b101ShredURLTraceURLbptoweringB6512TinyURL6towering0rGetShorty004bm5301URLprotracted0prolonged61MooURLy1948jspread%2Bout428u0t3stretchingfarawaylasting11ShredURLc2bDigBigexpandedX.se90a20TinyURL26WapURLr1cprolongedkelongatedc1f2c01loftylengthycontinuede7WapURLgGetShorty2NutshellURLcontinued6a2lastingr5protracted1expandeddistantspread%2BoutURl.iersustainedNotLongSHurl3w2SimURL011xSnipURL02GetShorty2prolonged0f02f60blingeringIs.gd301URLTinyLinktowering3d200t01osustained2WapURL90ShortURL11spread%2Boute02URLPieFly2toweringDwarfurl70elongated9s070SnipURL6Is.gd7spread%2Boutc0hy210vtcnf43Redirxb9148n1lingering6PiURL16URLcutaspread%2BoutYATUCoutstretchede70lUlimita1e610ShortenURL1lnk.inenduringUlimit0U760l8m72011793v7020TightURLelongatedYATUCt6UrlTeaetc91e5kspun%2Bout010d1e1b1Dwarfurl6Shortlinksb0sustained0enlarged6great1187e5e690URLCutter1spun%2Bout10drawn%2Bouttall4EasyURLDecentURLenduringd1eTraceURL5yGetShortyTinyLinkfar%2Boff1prolonged4cc0stretcheddeepprotracted3f001elongate9018ystretchinglastingi7TinyURL7expanded910continuedremotef8sustainedz175lingeringcbloftyprolonged10079running0UlimitB6515Shrinkr00LiteURL1loftyoutstretchedclnk.in3farawayg5runningTinyLinkspread%2Bout1stringy11c036greatfarawaystretchingefar%2Boff31spread%2Bout4kDoiopMooURL53m19Beam.tolastingShredURL1s25ShimBeam.to8nstretchtowering80StartURLShortURL4lengthened018Is.gdNotLongzWapURLNutshellURLe2spun%2Bout119elongated7elongated5outstretchedh8k1stringyloftyShredURL84running06308d071Minilien3wg3UrlTealoftystretchedwCanURLfar%2Boff7atf104083towering820ganglingw35m1a063LiteURLt081NanoRef361lnk.in0deep0Shrinkr6e80far%2Boff9170Redirxy6btspread%2Boutsustained10UlimitShortlinks2toweringGetShorty3ShrinkrDecentURLsustaineddbg1nfShortURL331a001enlargedB65RedirxelongatedMinilien809UrlT
                fun anIncrediblyLongAndComplexMethodNameThatProbablyShouldBeMuchShorterButForTheSakeOfTheTestItsNot(): String {
                    return "Hello"
                }
            
                fun getLoremIpsum() = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua."
            }
        """.trimIndent()

        @Test
        fun `should report no errors when maxLineLength is set to 200`() {
            val rule = MaxLineLength(
                TestConfig(
                    MAX_LINE_LENGTH to "200",
                )
            )

            val findings = rule.lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should report all errors with default maxLineLength`() {
            val rule = MaxLineLength(Config.empty)

            val findings = rule.lint(code)
            assertThat(findings).hasSize(3)
        }

        @Test
        fun `should report all errors with default maxLineLength including raw strings`() {
            val rule = MaxLineLength(
                TestConfig(
                    EXCLUDE_RAW_STRINGS to false,
                )
            )

            val findings = rule.lint(code)
            assertThat(findings).hasSize(7)
        }

        @Test
        fun `should report meaningful signature for all violations`() {
            val rule = MaxLineLength(Config.empty)

            val findings = rule.lint(code)
            assertThat(findings).hasSize(3)
                .allSatisfy { assertThat(it.entity.signature.substringAfterLast('$')).isNotBlank() }
        }

        @Test
        fun `should not report raw string with spaces - #7555`() {
            val rule = MaxLineLength(TestConfig())
            val code = """
                class MaxLineLength {
                    val longSingleLineRawString1 =
                        ${TQ}This is first yet another very very very very very very very very very very very very very very very very very very very very very very very very very very very very$TQ

                    val longSingleLineRawString2 =
                        $TQ
                            This is second yet another very very very very very very very very very very very very very very very very very very very very very very very very very very very very$TQ

                    val longSingleLineRawString3 =

                        $TQ
                            This is third yet another very very very very very very very very very very very very very very very very very very very very very very very very very very very$TQ

                    val longSingleLineRawString4 =

                        $TQ

                            This is third yet another very very very very very very very very very very very very very very very very very very very very very very very very very very very$TQ

                    val longSingleLineRawString2WithTrimIndent =
                        $TQ
                            This is second yet another very very very very very very very very very very very very very very very very very very very very very very very very very very very very$TQ
                        .trimIndent()
                }
            """.trimIndent()
            assertThat(rule.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `a kt file with long but suppressed lines` {

        private val code = """
            class MaxLineLengthSuppressed {
                companion object {
                    @Suppress("MaxLineLength")
                    val LOREM_IPSUM = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua."
            
                    @Suppress("MaxLineLength")
                    val A_VERY_LONG_MULTI_LINE = $TQ
                        This is anotehr very very very very very very very very, very long multiline String that will break the MaxLineLength"
                    $TQ.trimIndent()
                }
            
                @Suppress("MaxLineLength")
                val loremIpsumField = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua."
            
                @Suppress("MaxLineLength")
                val longMultiLineField = $TQ
                        This is anotehr very very very very very very very very
                        very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very
                        very long multiline String that will break the MaxLineLength
                    $TQ.trimIndent()
            
                @Suppress("MaxLineLength")
                val longMultiLineFieldWithLineBreaks =
                    $TQ
                        This is anotehr very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very
                        very long multiline String with Line Break that will break the MaxLineLength
                    $TQ.trimIndent()
            
                fun main() {
                    val thisIsAVeryLongValName = "This is a very, very long String that will break the MaxLineLength"
            
                    if (thisIsAVeryLongValName.length > "This is not quite as long of a String".length) {
                        println("It's indeed a very long String")
                    }
            
                    @Suppress("MaxLineLength")
                    val hello = anIncrediblyLongAndComplexMethodNameThatProbablyShouldBeMuchShorterButForTheSakeOfTheTestItsNot()
                    val loremIpsum = getLoremIpsum()
            
                    println(hello)
                    println(loremIpsum)
            
                }
            
                fun anIncrediblyLongAndComplexMethodNameThatProbablyShouldBeMuchShorterButForTheSakeOfTheTestItsNot(): String {
                    return "Hello"
                }
            
                @Suppress("MaxLineLength")
                fun getLoremIpsum() = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua."
            }
            
            @Suppress("MaxLineLength")
            class AClassWithSuperLongNameItIsSooooLongThatIHaveTroubleThinkingAboutAVeryLongNameManThisIsReallyHardToFillAllTheNecessaryCharacters
            
            @Suppress("MaxLineLength")
            class AClassWithReallyLongCommentsInside {
                /*
                 a really long line that is inside a normal comment ------------------------------------------------------------------------------------------------>
                 */
            
                /**
                 a really long line that is inside a KDoc comment   ------------------------------------------------------------------------------------------------>
                 */
            }
        """.trimIndent()

        @Test
        fun `should not report as lines are suppressed`() {
            val rule = MaxLineLength(Config.empty)

            val findings = rule.lint(code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `a kt file with a long package name and long import statements` {
        val code = """
            package anIncrediblyLongAndComplexPackageNameThatProbablyShouldBeMuchShorterButForTheSakeOfTheTestItsNot
            
            import java.nio.file.attribute.PosixFileAttributeView
        """.trimIndent()

        @Test
        fun `should not report the package statement and import statements by default`() {
            val rule = MaxLineLength(
                TestConfig(
                    MAX_LINE_LENGTH to "40",
                )
            )

            val findings = rule.lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should report the package statement and import statements if they're enabled`() {
            val rule = MaxLineLength(
                TestConfig(
                    MAX_LINE_LENGTH to "40",
                    EXCLUDE_PACKAGE_STATEMENTS to "false",
                    EXCLUDE_IMPORT_STATEMENTS to "false",
                )
            )

            val findings = rule.lint(code)
            assertThat(findings).hasSize(2)
        }

        @Test
        fun `should not report anything if both package and import statements are disabled`() {
            val rule = MaxLineLength(
                TestConfig(
                    MAX_LINE_LENGTH to "40",
                    EXCLUDE_PACKAGE_STATEMENTS to "true",
                    EXCLUDE_IMPORT_STATEMENTS to "true",
                )
            )

            val findings = rule.lint(code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `a kt file with a long package name, long import statements, a long line and long comments` {
        private val code = """
            class MaxLineLengthWithLongComments {
                // Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.
                /* Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. */
            
                /*
                 * Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.
                 */
                companion object {
                    val LOREM_IPSUM = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua."
                }
            
                val loremIpsumField = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua."
            
                fun main() {
                    val thisIsAVeryLongValName = "This is a very, very long String that will break the MaxLineLength"
            
                    if (thisIsAVeryLongValName.length > "This is not quite as long of a String".length) {
                        println("It's indeed a very long String")
                    }
            
                    val loremIpsum = getLoremIpsum()
            
                    println(loremIpsum)
            
                }
            
                fun getLoremIpsum() = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua."
            }
        """.trimIndent()

        @Test
        fun `should report the package statement, import statements, line and comments by default`() {
            val rule = MaxLineLength(
                TestConfig(
                    MAX_LINE_LENGTH to "60",
                )
            )

            val findings = rule.lint(code)
            assertThat(findings).hasSize(8)
        }

        @Test
        fun `should report the package statement, import statements, line and comments if they're enabled`() {
            val rule = MaxLineLength(
                TestConfig(
                    MAX_LINE_LENGTH to "60",
                    EXCLUDE_PACKAGE_STATEMENTS to "false",
                    EXCLUDE_IMPORT_STATEMENTS to "false",
                    EXCLUDE_COMMENT_STATEMENTS to "false",
                )
            )

            val findings = rule.lint(code)
            assertThat(findings).hasSize(8)
        }

        @Test
        fun `should not report comments if they're disabled`() {
            val rule = MaxLineLength(
                TestConfig(
                    MAX_LINE_LENGTH to "60",
                    EXCLUDE_COMMENT_STATEMENTS to "true",
                )
            )

            val findings = rule.lint(code)
            assertThat(findings).hasSize(5)
        }
    }

    @Nested
    inner class `a kt file with a long package name, long import statements and a long line` {
        val code = """
            package anIncrediblyLongAndComplexPackageNameThatProbablyShouldBeMuchShorterButForTheSakeOfTheTestItsNot
            
            import java.nio.file.attribute.PosixFileAttributeView
            
            class Test {
                fun anIncrediblyLongAndComplexMethodNameThatProbablyShouldBeMuchShorterButForTheSakeOfTheTestItsNot() {}
            }
        """.trimIndent()

        @Test
        fun `should only the function line by default`() {
            val rule = MaxLineLength(
                TestConfig(
                    MAX_LINE_LENGTH to "40",
                )
            )

            val findings = rule.lint(code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should report the package statement, import statements and line if they're not excluded`() {
            val rule = MaxLineLength(
                TestConfig(
                    MAX_LINE_LENGTH to "40",
                    EXCLUDE_PACKAGE_STATEMENTS to "false",
                    EXCLUDE_IMPORT_STATEMENTS to "false",
                )
            )

            val findings = rule.lint(code)
            assertThat(findings).hasSize(3)
        }

        @Test
        fun `should report only method if both package and import statements are disabled`() {
            val rule = MaxLineLength(
                TestConfig(
                    MAX_LINE_LENGTH to "40",
                    EXCLUDE_PACKAGE_STATEMENTS to "true",
                    EXCLUDE_IMPORT_STATEMENTS to "true",
                )
            )

            val findings = rule.lint(code)
            assertThat(findings).singleElement()
                .hasStartSourceLocation(6, 1)
                .hasEndSourceLocation(6, 109)
        }
    }

    @Test
    fun `report the correct lines on raw strings with backslash on it - issue #5314`() {
        val rule = MaxLineLength(
            TestConfig(
                MAX_LINE_LENGTH to "30",
                "excludeRawStrings" to "false",
            )
        )

        val findings = rule.lint(
            """
                // some other content
                val x = Regex($TQ
                    Text (.*?)\(in parens\) this is too long to be valid.
                    The regex/raw string continues down another line      .
                $TQ.trimIndent())
                // that is the right length
            """.trimIndent()
        )
        assertThat(findings).satisfiesExactlyInAnyOrder(
            { assertThat(it).hasTextLocation(40 to 97) },
            { assertThat(it).hasTextLocation(98 to 157) },
        )
    }

    @Test
    fun `report the correct lines on raw strings with backslash on it 2 - issue #5314`() {
        val rule = MaxLineLength(
            TestConfig(
                MAX_LINE_LENGTH to "30",
                "excludeRawStrings" to "false",
            )
        )

        val findings = rule.lint(
            """
                // some other content
                val x = "Foo".matches($TQ...too long\(parens\) and some more$TQ.toRegex())
                // that is the right length
            """.trimIndent()
        )
        assertThat(findings).singleElement()
            .hasTextLocation(22 to 96)
    }

    @Test
    fun `report the correct lines on interpolated strings - issue #5314`() {
        val rule = MaxLineLength(
            TestConfig(
                MAX_LINE_LENGTH to "65",
            )
        )

        val findings = rule.lint(
            """
                interface TaskContainer {
                    fun register(name: String, block: Number.() -> Unit = {})
                }
                interface Project {
                    val tasks: TaskContainer
                }
                fun repros(project: Project) {
                    val part = "name".capitalize()
                    project.tasks.register("shortName${'$'}{part}WithSuffix")
                    project.tasks.register("veryVeryVeryVeryVeryVeryLongName${'$'}{part}WithSuffix1")
                    project.tasks.register("veryVeryVeryVeryVeryVeryLongName${'$'}{part}WithSuffix2") {
                        this.toByte()
                    }
                    project.tasks
                        .register("veryVeryVeryVeryVeryVeryLongName${'$'}{part}WithSuffix3") {
                        this.toByte()
                    }
                }
            """.trimIndent()
        )
        assertThat(findings).satisfiesExactlyInAnyOrder(
            {
                assertThat(it).hasTextLocation(
                    "    project.tasks.register(\"veryVeryVeryVeryVeryVeryLongName\${part}WithSuffix1\")"
                )
            },
            {
                assertThat(it).hasTextLocation(
                    "    project.tasks.register(\"veryVeryVeryVeryVeryVeryLongName\${part}WithSuffix2\") {"
                )
            },
            {
                assertThat(it).hasTextLocation(
                    "        .register(\"veryVeryVeryVeryVeryVeryLongName\${part}WithSuffix3\") {"
                )
            },
        )
    }

    @Nested
    inner class `code containing comment with long markdown url` {
        @Test
        fun `should not report for long markdown url in kdoc`() {
            val code = """
                /**
                * This is doc with markdown url See: [Maven Publish Plugin | Publications](https://docs.gradle.org/current/userguide/publishing_maven.html#publishing_maven:publications)
                * [Maven Publish Plugin | Publications](https://docs.gradle.org/current/userguide/publishing_maven.html#publishing_maven:publications)     
                */
                class Test
            """.trimIndent()
            val rule = MaxLineLength(
                TestConfig(
                    MAX_LINE_LENGTH to "60",
                )
            )

            assertThat(rule.lint(code)).isEmpty()
        }

        @Test
        fun `should not report for long markdown url in comments`() {
            val code = """
                // This is doc with markdown url See: [Maven Publish Plugin | Publications](https://docs.gradle.org/current/userguide/publishing_maven.html#publishing_maven:publications)
                // [Maven Publish Plugin | Publications](https://docs.gradle.org/current/userguide/publishing_maven.html#publishing_maven:publications)     
                // [Maven Publish Plugin | Publications](https://docs.gradle.org/current/userguide/publishing_maven.html#publishing_maven:publications "With title")     
                class Test
            """.trimIndent()
            val rule = MaxLineLength(
                TestConfig(
                    MAX_LINE_LENGTH to "60",
                )
            )

            assertThat(rule.lint(code)).isEmpty()
        }

        @Test
        fun `should report for wrong formatted long markdown url in comments`() {
            val code = """
                /**
                * This is doc with markdown url See: [Maven Publish Plugin | Publications](https://docs.gradle.org/current/userguide/publishing_maven.html#publishing_maven:publications
                * [Maven Publish Plugin | Publications(https://docs.gradle.org/current/userguide/publishing_maven.html#publishing_maven:publications)     
                * Maven Publish Plugin | Publications](https://docs.gradle.org/current/userguide/publishing_maven.html#publishing_maven:publications "With title")     
                */
                class Test
            """.trimIndent()
            val rule = MaxLineLength(
                TestConfig(
                    MAX_LINE_LENGTH to "60",
                )
            )

            assertThat(rule.lint(code)).hasSize(3)
        }
    }

    @Nested
    inner class `code containing comment with long reference url` {
        @Test
        fun `should not report for long markdown url in kdoc`() {
            val code = """
                class Test {
                    /**
                    * [Maven Publish Plugin | Publications][funNameWhichIsVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryLong]
                    * [Maven Publish Plugin | Publications] [funNameWhichIsVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryLong]
                    * [funNameWhichIsVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryLong]
                    */
                    fun funNameWhichIsVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryLong() {
                        /*no-op*/
                    }
                }
            """.trimIndent()
            val rule = MaxLineLength(
                TestConfig(
                    MAX_LINE_LENGTH to "60",
                )
            )

            assertThat(rule.lint(code)).hasSize(1)
        }

        @Test
        fun `should report for wrong formatted long markdown url in comments`() {
            val code = """
                class Test {
                    /**
                    * [funNameWhichIsVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVery VeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryLong]
                    * [funNameWhichIsVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryLong ]
                    */
                    fun funNameWhichIsVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryLong() {
                        /*no-op*/
                    }
                }
            """.trimIndent()
            val rule = MaxLineLength(
                TestConfig(
                    MAX_LINE_LENGTH to "60",
                )
            )

            assertThat(rule.lint(code)).hasSize(3)
        }
    }
}

private const val TQ = "\"\"\""
