package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private const val ACCEPTABLE_LENGTH = "acceptableLength"
private const val ALLOW_NON_STANDARD_GROUPING = "allowNonStandardGrouping"

class UnderscoresInNumericLiteralsSpec {

    @Nested
    inner class `an Int of 1000` {
        val code = "val myInt = 1000"

        @Test
        fun `should not be reported by default`() {
            val findings = UnderscoresInNumericLiterals(Config.empty).compileAndLint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should be reported if acceptableLength is 3`() {
            val findings = UnderscoresInNumericLiterals(
                TestConfig(ACCEPTABLE_LENGTH to "3")
            ).compileAndLint(code)
            assertThat(findings).isNotEmpty
        }
    }

    @Nested
    inner class `an Int of 1_000_000` {
        val code = "val myInt = 1_000_000"

        @Test
        fun `should not be reported`() {
            val findings = UnderscoresInNumericLiterals(Config.empty).compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `a const Int of 1000000` {
        val code = "val myInt = 1000000"

        @Test
        fun `should be reported by default`() {
            val findings = UnderscoresInNumericLiterals(Config.empty).compileAndLint(code)
            assertThat(findings).isNotEmpty
        }

        @Test
        fun `should not be reported if acceptableLength is 7`() {
            val findings = UnderscoresInNumericLiterals(
                TestConfig(ACCEPTABLE_LENGTH to "7")
            ).compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `a Float of 1000f` {
        val code = "val myFloat = 1000f"

        @Test
        fun `should not be reported by default`() {
            val findings = UnderscoresInNumericLiterals(Config.empty).compileAndLint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should be reported if acceptableLength is 3`() {
            val findings = UnderscoresInNumericLiterals(
                TestConfig(ACCEPTABLE_LENGTH to "3")
            ).compileAndLint(code)
            assertThat(findings).isNotEmpty
        }
    }

    @Nested
    inner class `a Float of -1000f` {
        val code = "val myFloat = -1000f"

        @Test
        fun `should not be reported by default`() {
            val findings = UnderscoresInNumericLiterals(Config.empty).compileAndLint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should be reported if acceptableLength is 3`() {
            val findings = UnderscoresInNumericLiterals(
                TestConfig(ACCEPTABLE_LENGTH to "3")
            ).compileAndLint(code)
            assertThat(findings).isNotEmpty
        }
    }

    @Nested
    inner class `a Float of -1_000f` {
        val code = "val myFloat = -1_000f"

        @Test
        fun `should not be reported`() {
            val findings = UnderscoresInNumericLiterals(Config.empty).compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `a Long of 1000000L` {
        val code = "val myLong = 1000000L"

        @Test
        fun `should be reported by default`() {
            val findings = UnderscoresInNumericLiterals(Config.empty).compileAndLint(code)
            assertThat(findings).isNotEmpty
        }

        @Test
        fun `should not be reported if ignored acceptableLength is 7`() {
            val findings = UnderscoresInNumericLiterals(
                TestConfig(ACCEPTABLE_LENGTH to "7")
            ).compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    @DisplayName("a Double of 1_000_000.00_000_000")
    inner class DoubleWithDecimals {
        val code = "val myDouble = 1_000_000.00_000_000"

        @Test
        fun `should not be reported`() {
            val findings = UnderscoresInNumericLiterals(Config.empty).compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `a function with default Int parameter value 1000` {
        val code = "fun testFunction(testParam: Int = 1000) {}"

        @Test
        fun `should not be reported by default`() {
            val findings = UnderscoresInNumericLiterals(Config.empty).compileAndLint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should be reported if acceptableLength is 3`() {
            val findings = UnderscoresInNumericLiterals(
                TestConfig(ACCEPTABLE_LENGTH to "3")
            ).compileAndLint(code)
            assertThat(findings).isNotEmpty
        }
    }

    @Nested
    inner class `an annotation with numeric literals 0 and 10` {
        val code = "fun setCustomDimension(@IntRange(from = 0, to = 10) index: Int, value: String?) {}"

        @Test
        fun `should not be reported`() {
            val findings = UnderscoresInNumericLiterals(Config.empty).lint(code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `an annotation with numeric literals 0 and 1000000` {
        val code = "fun setCustomDimension(@IntRange(from = 0, to = 1000000) index: Int, value: String?) {}"

        @Test
        fun `should be reported by default`() {
            val findings = UnderscoresInNumericLiterals(Config.empty).lint(code)
            assertThat(findings).isNotEmpty
        }
    }

    @Nested
    inner class `an Int of 1000_00_00` {
        val code = "val myInt = 1000_00_00"

        @Test
        fun `should be reported by default`() {
            val findings = UnderscoresInNumericLiterals(Config.empty).compileAndLint(code)
            assertThat(findings).isNotEmpty
        }

        @Test
        fun `should still be reported even if acceptableLength is 99`() {
            val findings = UnderscoresInNumericLiterals(
                TestConfig(ACCEPTABLE_LENGTH to "99")
            ).compileAndLint(code)
            assertThat(findings).isNotEmpty
        }

        @Test
        fun `should not be reported if allowNonStandardGrouping is true`() {
            val findings = UnderscoresInNumericLiterals(
                TestConfig(ALLOW_NON_STANDARD_GROUPING to true)
            ).compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `a binary Int of 0b1011` {
        val code = "val myBinInt = 0b1011"

        @Test
        fun `should not be reported`() {
            val findings = UnderscoresInNumericLiterals(Config.empty).compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `a hexadecimal Int of 0x1facdf` {
        val code = "val myHexInt = 0x1facdf"

        @Test
        fun `should not be reported`() {
            val findings = UnderscoresInNumericLiterals(Config.empty).compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `a hexadecimal Int of 0xFFFFFF` {
        val code = "val myHexInt = 0xFFFFFF"

        @Test
        fun `should not be reported`() {
            val findings = UnderscoresInNumericLiterals(Config.empty).compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `a property named serialVersionUID in an object that implements Serializable` {
        val code = """
            import java.io.Serializable
            
            object TestSerializable : Serializable {
                private val serialVersionUID = 314159L
            }
        """.trimIndent()

        @Test
        fun `should not be reported`() {
            val findings = UnderscoresInNumericLiterals(Config.empty).compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `a long property named serialVersionUID in an object that does not implement Serializable` {
        val code = """
            object TestSerializable {
                private val serialVersionUID = 314159L
            }
        """.trimIndent()

        @Test
        fun `should be reported by default`() {
            val findings = UnderscoresInNumericLiterals(Config.empty).compileAndLint(code)
            assertThat(findings).isNotEmpty
        }
    }

    @Nested
    inner class `a long property with underscores named serialVersionUID in an object that does not implement Serializable` {
        val code = """
            object TestSerializable {
                private val serialVersionUID = 314_159L
            }
        """.trimIndent()

        @Test
        fun `should not be reported`() {
            val findings = UnderscoresInNumericLiterals(Config.empty).compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `a property named serialVersionUID in a companion object inside a serializable class` {

        @Test
        fun `does not report a negative serialVersionUID number`() {
            val code = """
                import java.io.Serializable
                
                class Test : Serializable {
                    companion object {
                        private const val serialVersionUID = -43857148126114372L
                    }
                }
            """.trimIndent()
            val findings = UnderscoresInNumericLiterals(Config.empty).compileAndLint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report a positive serialVersionUID number`() {
            val code = """
                import java.io.Serializable
                
                class Test : Serializable {
                    companion object {
                        private const val serialVersionUID = 43857148126114372L
                    }
                }
            """.trimIndent()
            val findings = UnderscoresInNumericLiterals(Config.empty).compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `a property named serialVersionUID number in a serializable class` {

        @Test
        fun `does not report a negative serialVersionUID number`() {
            val code = """
                import java.io.Serializable
                
                class Test : Serializable {
                    private val serialVersionUID = -43857148126114372L
                }
            """.trimIndent()
            val findings = UnderscoresInNumericLiterals(Config.empty).compileAndLint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report a positive serialVersionUID number`() {
            val code = """
                import java.io.Serializable
                
                class Test : Serializable {
                    private val serialVersionUID = 43857148126114372L
                }
            """.trimIndent()
            val findings = UnderscoresInNumericLiterals(Config.empty).compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `an Int of 10000` {
        val code = "val myInt = 10000"

        @Test
        fun `should be reported by default`() {
            val findings = UnderscoresInNumericLiterals(Config.empty).compileAndLint(code)
            assertThat(findings).isNotEmpty
        }

        @Test
        fun `should not be reported if acceptableLength is 5`() {
            val findings = UnderscoresInNumericLiterals(
                TestConfig(ACCEPTABLE_LENGTH to "5")
            ).compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    @DisplayName("a Float of 30_000_000.1415926535897932385")
    inner class FloatWithDecimals {
        val code = "val myFloat = 30_000_000.1415926535897932385f"

        @Test
        fun `should not be reported`() {
            val findings = UnderscoresInNumericLiterals(Config.empty).compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    @DisplayName("a Double of 3.1415926535897932385")
    inner class Pi {
        val code = "val myDouble = 3.1415926535897932385"

        @Test
        fun `should not be reported`() {
            val findings = UnderscoresInNumericLiterals(Config.empty).compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    @DisplayName("a Double of 3000.1415926535897932385")
    inner class Double {
        val code = "val myDouble = 3000.1415926535897932385"

        @Test
        fun `should be reported if acceptableLength is 3`() {
            val findings = UnderscoresInNumericLiterals(
                TestConfig(ACCEPTABLE_LENGTH to "3")
            ).compileAndLint(code)
            assertThat(findings).isNotEmpty
        }
    }

    @Nested
    @DisplayName("a Float of 1000000.31415926535f")
    inner class Float {
        val code = "val myFloat = 1000000.31415926535f"

        @Test
        fun `should be reported by default`() {
            val findings = UnderscoresInNumericLiterals(Config.empty).compileAndLint(code)
            assertThat(findings).isNotEmpty
        }

        @Test
        fun `should not be reported if acceptableLength is 7`() {
            val findings = UnderscoresInNumericLiterals(
                TestConfig(ACCEPTABLE_LENGTH to "7")
            ).compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    @DisplayName("a String of 1000000.3141592")
    inner class String {
        val code = """val myString = "1000000.3141592""""

        @Test
        fun `should not be reported by default`() {
            val findings = UnderscoresInNumericLiterals(Config.empty).compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }
}
