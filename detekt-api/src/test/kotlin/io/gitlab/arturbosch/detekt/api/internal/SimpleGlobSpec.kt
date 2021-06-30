package io.gitlab.arturbosch.detekt.api.internal

import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object SimpleGlobSpec : Spek({
    describe("glob") {
        describe("empty pattern") {
            val subject by memoized { SimpleGlob.of("") }

            it("matches an empty string") {
                val actual = subject.matches("")
                assertThat(actual).isTrue()
            }
            it("does not match a blank string") {
                val actual = subject.matches(" ")
                assertThat(actual).isFalse()
            }
        }
        describe("blank pattern") {
            val subject by memoized { SimpleGlob.of(" \t") }

            it("matches an empty string") {
                val actual = subject.matches(" \t")
                assertThat(actual).isTrue()
            }
            it("does not match a different string") {
                val actual = subject.matches("  ")
                assertThat(actual).isFalse()
            }
        }
        describe("static pattern") {
            val subject by memoized { SimpleGlob.of("abc") }

            it("matches the same string") {
                val actual = subject.matches("abc")
                assertThat(actual).isTrue()
            }
            it("does not match a other string") {
                val actual = subject.matches("aaa")
                assertThat(actual).isFalse()
            }
        }
        describe("* wildcard") {
            describe("single wildcard") {
                describe("pattern with wildcard at the beginning") {
                    val subject by memoized { SimpleGlob.of("*xyz") }

                    it("matches pattern exactly") {
                        val actual = subject.matches("xyz")
                        assertThat(actual).isTrue()
                    }
                    it("matches pattern with anything before") {
                        val actual = subject.matches("abcxyz")
                        assertThat(actual).isTrue()
                    }
                    it("does not match with anything after") {
                        val actual = subject.matches("xyzabc")
                        assertThat(actual).isFalse()
                    }
                }
                describe("pattern with wildcard at the end") {
                    val subject by memoized { SimpleGlob.of("xyz*") }

                    it("matches pattern exactly") {
                        val actual = subject.matches("xyz")
                        assertThat(actual).isTrue()
                    }
                    it("matches pattern with anything after") {
                        val actual = subject.matches("xyzabc")
                        assertThat(actual).isTrue()
                    }
                    it("does not match with anything before") {
                        val actual = subject.matches("abcxyz")
                        assertThat(actual).isFalse()
                    }
                }
                describe("pattern with wildcard at the middle") {
                    val subject by memoized { SimpleGlob.of("x*yz") }

                    it("matches pattern exactly") {
                        val actual = subject.matches("xyz")
                        assertThat(actual).isTrue()
                    }
                    it("matches pattern with anything in between") {
                        val actual = subject.matches("xaaaayz")
                        assertThat(actual).isTrue()
                    }
                    it("does not match with anything before") {
                        val actual = subject.matches("axyz")
                        assertThat(actual).isFalse()
                    }
                    it("does not match with anything after") {
                        val actual = subject.matches("xyza")
                        assertThat(actual).isFalse()
                    }
                }
            }
            describe("multiple wildcards") {
                val subject by memoized { SimpleGlob.of("x*yz*") }

                it("matches pattern") {
                    val actual = subject.matches("x.aaa.yz.bbb")
                    assertThat(actual).isTrue()
                }
            }
        }
        describe("? wildcard") {
            describe("single wildcard") {
                describe("pattern with wildcard at the beginning") {
                    val subject by memoized { SimpleGlob.of("?xyz") }

                    it("matches with any character before") {
                        val actual = subject.matches("_xyz")
                        assertThat(actual).isTrue()
                    }
                    it("does not match with anything before") {
                        val actual = subject.matches("xyz")
                        assertThat(actual).isFalse()
                    }
                    it("does not match with more than on character before") {
                        val actual = subject.matches("aaxyz")
                        assertThat(actual).isFalse()
                    }
                }
                describe("pattern with wildcard at the end") {
                    val subject by memoized { SimpleGlob.of("xyz?") }

                    it("matches with any character after") {
                        val actual = subject.matches("xyz_")
                        assertThat(actual).isTrue()
                    }
                    it("does not match with anything after") {
                        val actual = subject.matches("xyz")
                        assertThat(actual).isFalse()
                    }
                    it("does not match with more than on character after") {
                        val actual = subject.matches("xyz_a")
                        assertThat(actual).isFalse()
                    }
                }
                describe("pattern with wildcard at the middle") {
                    val subject by memoized { SimpleGlob.of("x?yz") }

                    it("matches with any single character") {
                        val actual = subject.matches("x_yz")
                        assertThat(actual).isTrue()
                    }
                    it("does not match with more than one character") {
                        val actual = subject.matches("x_a_yz")
                        assertThat(actual).isFalse()
                    }
                }
            }
            describe("multiple wildcards") {
                val subject by memoized { SimpleGlob.of("x?y?z") }

                it("matches pattern") {
                    val actual = subject.matches("x.y.z")
                    assertThat(actual).isTrue()
                }
            }
        }

        context("characters that have a special meaning in regular expression must be escaped") {
            describe("period .") {
                val subject by memoized { SimpleGlob.of("a.b.c") }

                it("matches the same string") {
                    val actual = subject.matches("a.b.c")
                    assertThat(actual).isTrue()
                }
                it("does not match a other string") {
                    val actual = subject.matches("a_b_c")
                    assertThat(actual).isFalse()
                }
            }
            describe("""backslash \""") {
                val subject by memoized { SimpleGlob.of("""ab\d""") }

                it("matches the same string") {
                    val actual = subject.matches("""ab\d""")
                    assertThat(actual).isTrue()
                }
                it("does not match a other string") {
                    val actual = subject.matches("ab5")
                    assertThat(actual).isFalse()
                }
            }
        }

        describe("invalid pattern") {
            it("fails") {}
        }
    }
})
