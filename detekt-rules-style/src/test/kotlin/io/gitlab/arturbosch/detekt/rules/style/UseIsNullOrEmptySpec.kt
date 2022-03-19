package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UseIsNullOrEmptySpec : Spek({
    setupKotlinEnvironment()
    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { UseIsNullOrEmpty() }

    describe("report UseIsNullOrEmpty rule") {
        context("List") {
            it("null or isEmpty()") {
                val code = """
                    fun test(x: List<Int>?) {
                        if (x == null || x.isEmpty()) return
                    }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
                assertThat(findings).hasSourceLocation(2, 9)
                assertThat(findings[0]).hasMessage(
                    "This 'x == null || x.isEmpty()' can be replaced with 'isNullOrEmpty()' call"
                )
            }
            it("null or count() == 0") {
                val code = """
                    fun test(x: List<Int>?) {
                        if (x == null || x.count() == 0) return
                    }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }
            it("null or size == 0") {
                val code = """
                    fun test(x: List<Int>?) {
                        if (x == null || x.size == 0) return
                    }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }
            it("flipped null check") {
                val code = """
                    fun test(x: List<Int>?) {
                        if (null == x || x.isEmpty()) return
                    }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }
            it("flipped count check") {
                val code = """
                    fun test(x: List<Int>?) {
                        if (x == null || 0 == x.count()) return
                    }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }
            it("flipped size check") {
                val code = """
                    fun test(x: List<Int>?) {
                        if (x == null || 0 == x.size) return
                    }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }
        }

        context("Set") {
            it("null or isEmpty()") {
                val code = """
                    fun test(x: Set<Int>?) {
                        if (x == null || x.isEmpty()) return
                    }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }
            it("null or count() == 0") {
                val code = """
                    fun test(x: Set<Int>?) {
                        if (x == null || x.count() == 0) return
                    }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }
            it("null or size == 0") {
                val code = """
                    fun test(x: Set<Int>?) {
                        if (x == null || x.size == 0) return
                    }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }
        }

        context("Collection") {
            it("null or isEmpty()") {
                val code = """
                    fun test(x: Collection<Int>?) {
                        if (x == null || x.isEmpty()) return
                    }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }
            it("null or count() == 0") {
                val code = """
                    fun test(x: Collection<Int>?) {
                        if (x == null || x.count() == 0) return
                    }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }
            it("null or size == 0") {
                val code = """
                    fun test(x: Collection<Int>?) {
                        if (x == null || x.size == 0) return
                    }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }
        }

        context("Map") {
            it("null or isEmpty()") {
                val code = """
                    fun test(x: Map<Int, String>?) {
                        if (x == null || x.isEmpty()) return
                    }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }
            it("null or count() == 0") {
                val code = """
                    fun test(x: Map<Int, String>?) {
                        if (x == null || x.count() == 0) return
                    }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }
            it("null or size == 0") {
                val code = """
                    fun test(x: Map<Int, String>?) {
                        if (x == null || x.size == 0) return
                    }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }
        }

        context("Array") {
            it("null or isEmpty()") {
                val code = """
                    fun test(x: Array<Int>?) {
                        if (x == null || x.isEmpty()) return
                    }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }
            it("null or count() == 0") {
                val code = """
                    fun test(x: Array<Int>?) {
                        if (x == null || x.count() == 0) return
                    }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }
            it("null or size == 0") {
                val code = """
                    fun test(x: Array<Int>?) {
                        if (x == null || x.size == 0) return
                    }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }
        }

        context("String") {
            it("null or isEmpty()") {
                val code = """
                    fun test(x: String?) {
                        if (x == null || x.isEmpty()) return
                    }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }
            it("null or count() == 0") {
                val code = """
                    fun test(x: String?) {
                        if (x == null || x.count() == 0) return
                    }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }
            it("null or length == 0") {
                val code = """
                    fun test(x: String?) {
                        if (x == null || x.length == 0) return
                    }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }

            it("null or equal empty string") {
                val code = """
                    fun test(x: String?) {
                        if (x == null || x == "") return
                    }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }
        }

        context("MutableList") {
            it("null or isEmpty()") {
                val code = """
                    fun test(x: MutableList<Int>?) {
                        if (x == null || x.isEmpty()) return
                    }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }
        }

        context("MutableSet") {
            it("null or isEmpty()") {
                val code = """
                    fun test(x: MutableSet<Int>?) {
                        if (x == null || x.isEmpty()) return
                    }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }
        }

        context("MutableCollection") {
            it("null or isEmpty()") {
                val code = """
                    fun test(x: MutableCollection<Int>?) {
                        if (x == null || x.isEmpty()) return
                    }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }
        }

        context("MutableMap") {
            it("null or isEmpty()") {
                val code = """
                    fun test(x: MutableMap<Int, String>?) {
                        if (x == null || x.isEmpty()) return
                    }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }
        }
    }

    describe("does not report UseIsNullOrEmpty rule") {
        context("IntArray") {
            it("null or isEmpty()") {
                val code = """
                    fun test(x: IntArray?) {
                        if (x == null || x.isEmpty()) return
                    }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).isEmpty()
            }
            it("null or count() == 0") {
                val code = """
                    fun test(x: IntArray?) {
                        if (x == null || x.count() == 0) return
                    }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).isEmpty()
            }
            it("null or size == 0") {
                val code = """
                    fun test(x: IntArray?) {
                        if (x == null || x.size == 0) return
                    }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).isEmpty()
            }
        }

        context("Sequence") {
            it("null or count() == 0") {
                val code = """
                fun test(x: Sequence<Int>?) {
                    if (x == null || x.count() == 0) return
                }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).isEmpty()
            }
        }

        it("different variables") {
            val code = """
                fun test(x: List<Int>?, y: List<Int>) {
                    if (x == null || y.isEmpty()) return
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        it("not null check") {
            val code = """
                fun test(x: List<Int>?) {
                    if (x != null && x.isEmpty()) return
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        it("not size zero check") {
            val code = """
                fun test(x: List<Int>?) {
                    if (x == null || x.count() == 1) return
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        it("not null") {
            val code = """
                fun test(x: List<Int>) {
                    if (x == null || x.isEmpty()) return
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        it("var class member") {
            val code = """
                class Test {
                    var x: List<Int>? = null
                
                    fun test() {
                        if (x == null || x?.count() == 0) return
                    }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }
})
