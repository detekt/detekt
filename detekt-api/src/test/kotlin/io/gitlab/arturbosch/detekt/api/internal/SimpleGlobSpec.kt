package io.gitlab.arturbosch.detekt.api.internal

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SimpleGlobSpec {
    @Nested
    inner class `glob` {
        @Nested
        inner class `empty pattern` {
            private val subject = SimpleGlob.of("")

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
            private val subject = SimpleGlob.of(" \t")

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
        inner class `static pattern` {
            private val subject = SimpleGlob.of("abc")

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
        inner class `* wildcard` {
            @Nested
            inner class `single wildcard` {
                @Nested
                inner class `pattern with wildcard at the beginning` {
                    private val subject = SimpleGlob.of("*xyz")

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
                inner class `pattern with wildcard at the end` {
                    private val subject = SimpleGlob.of("xyz*")

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
                inner class `pattern with wildcard at the middle` {
                    private val subject = SimpleGlob.of("x*yz")

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
                private val subject = SimpleGlob.of("x*yz*")

                @Test
                fun `matches pattern`() {
                    val actual = subject.matches("x.aaa.yz.bbb")
                    assertThat(actual).isTrue()
                }
            }
        }

        @Nested
        inner class `? wildcard` {
            @Nested
            inner class `single wildcard` {
                @Nested
                inner class `pattern with wildcard at the beginning` {
                    private val subject = SimpleGlob.of("?xyz")

                    @Test
                    fun `matches with any character before`() {
                        val actual = subject.matches("_xyz")
                        assertThat(actual).isTrue()
                    }

                    @Test
                    fun `does not match with anything before`() {
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
                    private val subject = SimpleGlob.of("xyz?")

                    @Test
                    fun `matches with any character after`() {
                        val actual = subject.matches("xyz_")
                        assertThat(actual).isTrue()
                    }

                    @Test
                    fun `does not match with anything after`() {
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
                    private val subject = SimpleGlob.of("x?yz")

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
                private val subject = SimpleGlob.of("x?y?z")

                @Test
                fun `matches pattern`() {
                    val actual = subject.matches("x.y.z")
                    assertThat(actual).isTrue()
                }
            }
        }

        @Nested
        inner class `characters that have a special meaning in regular expression must be escaped` {
            @Nested
            inner class `period _` {
                private val subject = SimpleGlob.of("a.b.c")

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
            inner class `backslash` {
                private val subject = SimpleGlob.of("""ab\d""")

                @Test
                fun `matches the same string`() {
                    val actual = subject.matches("""ab\d""")
                    assertThat(actual).isTrue()
                }

                @Test
                fun `does not match a other string`() {
                    val actual = subject.matches("ab5")
                    assertThat(actual).isFalse()
                }
            }
        }

        @Nested
        inner class `invalid pattern` {
            @Test
            fun `fails`() {
                assertThatThrownBy { SimpleGlob.of("""a[b""") }
                    .isInstanceOf(IllegalArgumentException::class.java)
            }
        }
    }
}
