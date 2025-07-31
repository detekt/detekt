package dev.detekt.api

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class SimplePatternToRegexSpec {
    @Nested
    inner class `empty pattern` {
        private val subject = "".simplePatternToRegex()

        @Test
        fun `matches an empty string`() {
            val actual = subject.matches("")
            assertThat(actual).isTrue()
        }

        @Test
        fun `does not match a blank string`() {
            val actual = subject.matches(" ")
            assertThat(actual).isFalse()
        }
    }

    @Nested
    inner class `blank pattern` {
        private val subject = " \t".simplePatternToRegex()

        @Test
        fun `matches an empty string`() {
            val actual = subject.matches(" \t")
            assertThat(actual).isTrue()
        }

        @Test
        fun `does not match a different string`() {
            val actual = subject.matches("  ")
            assertThat(actual).isFalse()
        }
    }

    @Nested
    inner class `Static pattern` {
        private val subject = "abc".simplePatternToRegex()

        @Test
        fun `matches the same string`() {
            val actual = subject.matches("abc")
            assertThat(actual).isTrue()
        }

        @Test
        fun `does not match a other string`() {
            val actual = subject.matches("aaa")
            assertThat(actual).isFalse()
        }
    }

    @Nested
    inner class `Asterisk wildcard` {
        @Nested
        inner class `single wildcard` {
            @Nested
            inner class `pattern with wildcard at the beginning` {
                private val subject = "*xyz".simplePatternToRegex()

                @Test
                fun `matches pattern exactly`() {
                    val actual = subject.matches("xyz")
                    assertThat(actual).isTrue()
                }

                @Test
                fun `matches pattern with anything before`() {
                    val actual = subject.matches("abcxyz")
                    assertThat(actual).isTrue()
                }

                @Test
                fun `does not match with anything after`() {
                    val actual = subject.matches("xyzabc")
                    assertThat(actual).isFalse()
                }
            }

            @Nested
            inner class `Pattern with wildcard at the end` {
                private val subject = "xyz*".simplePatternToRegex()

                @Test
                fun `matches pattern exactly`() {
                    val actual = subject.matches("xyz")
                    assertThat(actual).isTrue()
                }

                @Test
                fun `matches pattern with anything after`() {
                    val actual = subject.matches("xyzabc")
                    assertThat(actual).isTrue()
                }

                @Test
                fun `does not match with anything before`() {
                    val actual = subject.matches("abcxyz")
                    assertThat(actual).isFalse()
                }
            }

            @Nested
            inner class `Pattern with wildcard at the middle` {
                private val subject = "x*yz".simplePatternToRegex()

                @Test
                fun `matches pattern exactly`() {
                    val actual = subject.matches("xyz")
                    assertThat(actual).isTrue()
                }

                @Test
                fun `matches pattern with anything in between`() {
                    val actual = subject.matches("xaaaayz")
                    assertThat(actual).isTrue()
                }

                @Test
                fun `does not match with anything before`() {
                    val actual = subject.matches("axyz")
                    assertThat(actual).isFalse()
                }

                @Test
                fun `does not match with anything after`() {
                    val actual = subject.matches("xyza")
                    assertThat(actual).isFalse()
                }
            }
        }

        @Nested
        inner class `multiple wildcards` {
            private val subject = "x*yz*".simplePatternToRegex()

            @Test
            fun `matches pattern`() {
                val actual = subject.matches("x.aaa.yz.bbb")
                assertThat(actual).isTrue()
            }
        }
    }

    @Nested
    inner class `Questionmark wildcard` {
        @Nested
        inner class `single wildcard` {
            @Nested
            inner class `pattern with wildcard at the beginning` {
                private val subject = "?xyz".simplePatternToRegex()

                @Test
                fun `matches with any character before`() {
                    val actual = subject.matches("_xyz")
                    assertThat(actual).isTrue()
                }

                @Test
                fun `does not match with no character before`() {
                    val actual = subject.matches("xyz")
                    assertThat(actual).isFalse()
                }

                @Test
                fun `does not match with more than on character before`() {
                    val actual = subject.matches("aaxyz")
                    assertThat(actual).isFalse()
                }
            }

            @Nested
            inner class `pattern with wildcard at the end` {
                private val subject = "xyz?".simplePatternToRegex()

                @Test
                fun `matches with any character after`() {
                    val actual = subject.matches("xyz_")
                    assertThat(actual).isTrue()
                }

                @Test
                fun `does not match with no character after`() {
                    val actual = subject.matches("xyz")
                    assertThat(actual).isFalse()
                }

                @Test
                fun `does not match with more than on character after`() {
                    val actual = subject.matches("xyz_a")
                    assertThat(actual).isFalse()
                }
            }

            @Nested
            inner class `pattern with wildcard at the middle` {
                private val subject = "x?yz".simplePatternToRegex()

                @Test
                fun `matches with any single character`() {
                    val actual = subject.matches("x_yz")
                    assertThat(actual).isTrue()
                }

                @Test
                fun `does not match with more than one character`() {
                    val actual = subject.matches("x_a_yz")
                    assertThat(actual).isFalse()
                }
            }
        }

        @Nested
        inner class `multiple wildcards` {
            private val subject = "x?y?z".simplePatternToRegex()

            @Test
            fun `matches pattern`() {
                val actual = subject.matches("x.y_z")
                assertThat(actual).isTrue()
            }
        }
    }

    @Nested
    inner class `characters that have a special meaning in regular expression must be escaped` {
        @Nested
        inner class Period {
            private val subject = "a.b.c".simplePatternToRegex()

            @Test
            fun `matches the same string`() {
                val actual = subject.matches("a.b.c")
                assertThat(actual).isTrue()
            }

            @Test
            fun `does not match a other string`() {
                val actual = subject.matches("a_b_c")
                assertThat(actual).isFalse()
            }
        }

        @Nested
        inner class `character classes and quantifiers` {
            private val subject = """ab\d{2,5}\s\wc""".simplePatternToRegex()

            @Test
            fun `can be used`() {
                val actual = subject.matches("""ab123 Xc""")
                assertThat(actual).isTrue()
            }
        }
    }

    @Nested
    inner class `invalid pattern` {
        @Test
        fun `fails during creation`() {
            assertThatThrownBy { """a[b""".simplePatternToRegex() }
                .isInstanceOf(IllegalArgumentException::class.java)
        }
    }
}
