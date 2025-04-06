package io.gitlab.arturbosch.detekt.rules.style

import io.github.detekt.test.utils.KotlinEnvironmentContainer
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lintWithContext
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UseIsNullOrEmptySpec(val env: KotlinEnvironmentContainer) {
    val subject = UseIsNullOrEmpty(Config.empty)

    @Nested
    inner class `report UseIsNullOrEmpty rule` {
        @Nested
        inner class List {
            @Test
            fun `null or isEmpty()`() {
                val code = """
                    fun test(x: List<Int>?) {
                        if (x == null || x.isEmpty()) return
                    }
                """.trimIndent()
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).singleElement().hasMessage(
                    "This 'x == null || x.isEmpty()' can be replaced with 'isNullOrEmpty()' call"
                )
                assertThat(findings).hasStartSourceLocation(2, 9)
            }

            @Test
            fun `null or count() == 0`() {
                val code = """
                    fun test(x: List<Int>?) {
                        if (x == null || x.count() == 0) return
                    }
                """.trimIndent()
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }

            @Test
            fun `null or size == 0`() {
                val code = """
                    fun test(x: List<Int>?) {
                        if (x == null || x.size == 0) return
                    }
                """.trimIndent()
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }

            @Test
            fun `flipped null check`() {
                val code = """
                    fun test(x: List<Int>?) {
                        if (null == x || x.isEmpty()) return
                    }
                """.trimIndent()
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }

            @Test
            fun `flipped count check`() {
                val code = """
                    fun test(x: List<Int>?) {
                        if (x == null || 0 == x.count()) return
                    }
                """.trimIndent()
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }

            @Test
            fun `flipped size check`() {
                val code = """
                    fun test(x: List<Int>?) {
                        if (x == null || 0 == x.size) return
                    }
                """.trimIndent()
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }

            @Test
            fun `chained call`() {
                val code = """
                    class A {
                        val t: String? = ""
                    }
                    fun test() {
                        val a = A()
                        if (a.t == null || a.t.length == 0) return
                    }
                """.trimIndent()
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }

            @Test
            fun `chained call with package string`() {
                val code = """
                    fun test() {
                        if (java.io.File.separator == null || java.io.File.separator.length == 0) return
                    }
                """.trimIndent()
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }
        }

        @Nested
        inner class Set {
            @Test
            fun `null or isEmpty()`() {
                val code = """
                    fun test(x: Set<Int>?) {
                        if (x == null || x.isEmpty()) return
                    }
                """.trimIndent()
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }

            @Test
            fun `null or count() == 0`() {
                val code = """
                    fun test(x: Set<Int>?) {
                        if (x == null || x.count() == 0) return
                    }
                """.trimIndent()
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }

            @Test
            fun `null or size == 0`() {
                val code = """
                    fun test(x: Set<Int>?) {
                        if (x == null || x.size == 0) return
                    }
                """.trimIndent()
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }
        }

        @Nested
        inner class Collection {
            @Test
            fun `null or isEmpty()`() {
                val code = """
                    fun test(x: Collection<Int>?) {
                        if (x == null || x.isEmpty()) return
                    }
                """.trimIndent()
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }

            @Test
            fun `null or count() == 0`() {
                val code = """
                    fun test(x: Collection<Int>?) {
                        if (x == null || x.count() == 0) return
                    }
                """.trimIndent()
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }

            @Test
            fun `null or size == 0`() {
                val code = """
                    fun test(x: Collection<Int>?) {
                        if (x == null || x.size == 0) return
                    }
                """.trimIndent()
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }
        }

        @Nested
        inner class Map {
            @Test
            fun `null or isEmpty()`() {
                val code = """
                    fun test(x: Map<Int, String>?) {
                        if (x == null || x.isEmpty()) return
                    }
                """.trimIndent()
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }

            @Test
            fun `null or count() == 0`() {
                val code = """
                    fun test(x: Map<Int, String>?) {
                        if (x == null || x.count() == 0) return
                    }
                """.trimIndent()
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }

            @Test
            fun `null or size == 0`() {
                val code = """
                    fun test(x: Map<Int, String>?) {
                        if (x == null || x.size == 0) return
                    }
                """.trimIndent()
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }
        }

        @Nested
        inner class Array {
            @Test
            fun `null or isEmpty()`() {
                val code = """
                    fun test(x: Array<Int>?) {
                        if (x == null || x.isEmpty()) return
                    }
                """.trimIndent()
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }

            @Test
            fun `null or count() == 0`() {
                val code = """
                    fun test(x: Array<Int>?) {
                        if (x == null || x.count() == 0) return
                    }
                """.trimIndent()
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }

            @Test
            fun `null or size == 0`() {
                val code = """
                    fun test(x: Array<Int>?) {
                        if (x == null || x.size == 0) return
                    }
                """.trimIndent()
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }
        }

        @Nested
        inner class String {
            @Test
            fun `null or isEmpty()`() {
                val code = """
                    fun test(x: String?) {
                        if (x == null || x.isEmpty()) return
                    }
                """.trimIndent()
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }

            @Test
            fun `null or count() == 0`() {
                val code = """
                    fun test(x: String?) {
                        if (x == null || x.count() == 0) return
                    }
                """.trimIndent()
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }

            @Test
            fun `null or length == 0`() {
                val code = """
                    fun test(x: String?) {
                        if (x == null || x.length == 0) return
                    }
                """.trimIndent()
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }

            @Test
            fun `null or equal empty string`() {
                val code = """
                    fun test(x: String?) {
                        if (x == null || x == "") return
                    }
                """.trimIndent()
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }
        }

        @Nested
        inner class MutableList {
            @Test
            fun `null or isEmpty()`() {
                val code = """
                    fun test(x: MutableList<Int>?) {
                        if (x == null || x.isEmpty()) return
                    }
                """.trimIndent()
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }
        }

        @Nested
        inner class MutableSet {
            @Test
            fun `null or isEmpty()`() {
                val code = """
                    fun test(x: MutableSet<Int>?) {
                        if (x == null || x.isEmpty()) return
                    }
                """.trimIndent()
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }
        }

        @Nested
        inner class MutableCollection {
            @Test
            fun `null or isEmpty()`() {
                val code = """
                    fun test(x: MutableCollection<Int>?) {
                        if (x == null || x.isEmpty()) return
                    }
                """.trimIndent()
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }
        }

        @Nested
        inner class MutableMap {
            @Test
            fun `null or isEmpty()`() {
                val code = """
                    fun test(x: MutableMap<Int, String>?) {
                        if (x == null || x.isEmpty()) return
                    }
                """.trimIndent()
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }
        }
    }

    @Nested
    inner class `does not report UseIsNullOrEmpty rule` {
        @Nested
        inner class IntArray {
            @Test
            fun `null or isEmpty()`() {
                val code = """
                    fun test(x: IntArray?) {
                        if (x == null || x.isEmpty()) return
                    }
                """.trimIndent()
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).isEmpty()
            }

            @Test
            fun `null or count() == 0`() {
                val code = """
                    fun test(x: IntArray?) {
                        if (x == null || x.count() == 0) return
                    }
                """.trimIndent()
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).isEmpty()
            }

            @Test
            fun `null or size == 0`() {
                val code = """
                    fun test(x: IntArray?) {
                        if (x == null || x.size == 0) return
                    }
                """.trimIndent()
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).isEmpty()
            }
        }

        @Nested
        inner class Sequence {
            @Test
            fun `null or count() == 0`() {
                val code = """
                    fun test(x: Sequence<Int>?) {
                        if (x == null || x.count() == 0) return
                    }
                """.trimIndent()
                val findings = subject.lintWithContext(env, code)
                assertThat(findings).isEmpty()
            }
        }

        @Test
        fun `different variables`() {
            val code = """
                fun test(x: List<Int>?, y: List<Int>) {
                    if (x == null || y.isEmpty()) return
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `not null check`() {
            val code = """
                fun test(x: List<Int>?) {
                    if (x != null && x.isEmpty()) return
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `not size zero check`() {
            val code = """
                fun test(x: List<Int>?) {
                    if (x == null || x.count() == 1) return
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `not null`() {
            val code = """
                fun test(x: List<Int>) {
                    if (x == null || x.isEmpty()) return
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `var class member`() {
            val code = """
                class Test {
                    var x: List<Int>? = null
                
                    fun test() {
                        if (x == null || x?.count() == 0) return
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }
}
