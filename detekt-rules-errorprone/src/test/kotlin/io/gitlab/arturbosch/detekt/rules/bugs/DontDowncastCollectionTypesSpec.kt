package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import io.gitlab.arturbosch.detekt.test.lintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class DontDowncastCollectionTypesSpec : Spek({
    setupKotlinEnvironment()

    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { DontDowncastCollectionTypes(Config.empty) }

    describe("DontDowncastCollectionTypes rule") {

        describe("valid cases") {
            it("detects List type casts") {
                val code = """
                fun main() {
                    val myList = listOf(1,2,3)
                    val mutList = myList as MutableList<Int>
                }
                """
                val result = subject.compileAndLintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result.first().message).isEqualTo(
                    "Down-casting from type List to MutableList is risky. Use `toMutableList()` instead."
                )
            }

            it("detects List type safe casts") {
                val code = """
                fun main() {
                    val myList : List<Int>? = null
                    val mutList = myList as? MutableList<Int>
                }
                """
                val result = subject.compileAndLintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result.first().message).isEqualTo(
                    "Down-casting from type List to MutableList is risky. Use `toMutableList()` instead."
                )
            }

            it("detects List type checks") {
                val code = """
                fun main() {
                    val myList = listOf(1,2,3)
                    if (myList is MutableList<Int>) {
                        myList.add(4)
                    }
                }
                """
                val result = subject.compileAndLintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result.first().message).isEqualTo(
                    "Down-casting from type List to MutableList is risky. Use `toMutableList()` instead."
                )
            }

            it("detects Set type casts") {
                val code = """
                fun main() {
                    val mySet = setOf(1,2,3)
                    val mutSet = mySet as MutableSet<Int>
                }
                """
                val result = subject.compileAndLintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result.first().message).isEqualTo(
                    "Down-casting from type Set to MutableSet is risky. Use `toMutableSet()` instead."
                )
            }

            it("detects Set type safe casts") {
                val code = """
                fun main() {
                    val mySet : Set<Int>? = null
                    val mutSet = mySet as? MutableSet<Int>
                }
                """
                val result = subject.compileAndLintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result.first().message).isEqualTo(
                    "Down-casting from type Set to MutableSet is risky. Use `toMutableSet()` instead."
                )
            }

            it("detects Set type checks") {
                val code = """
                fun main() {
                    val mySet = setOf(1,2,3)
                    if (mySet is MutableSet<Int>) {
                        mySet.add(4)
                    }
                }
                """
                val result = subject.compileAndLintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result.first().message).isEqualTo(
                    "Down-casting from type Set to MutableSet is risky. Use `toMutableSet()` instead."
                )
            }

            it("detects Map type casts") {
                val code = """
                fun main() {
                    val myMap = mapOf(1 to 2)
                    val mutMap = myMap as MutableMap<Int, Int>
                }
                """
                val result = subject.compileAndLintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result.first().message).isEqualTo(
                    "Down-casting from type Map to MutableMap is risky. Use `toMutableMap()` instead."
                )
            }

            it("detects Map type safe casts") {
                val code = """
                fun main() {
                    val myMap : Map<Int, Int>? = null
                    val mutMap = myMap as? MutableMap<Int, Int>
                }
                """
                val result = subject.compileAndLintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result.first().message).isEqualTo(
                    "Down-casting from type Map to MutableMap is risky. Use `toMutableMap()` instead."
                )
            }

            it("detects Map type checks") {
                val code = """
                fun main() {
                    val myMap = mapOf(1 to 2)
                    if (myMap is MutableMap<Int, Int>) {
                        myMap[3] = 4
                    }
                }
                """
                val result = subject.compileAndLintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result.first().message).isEqualTo(
                    "Down-casting from type Map to MutableMap is risky. Use `toMutableMap()` instead."
                )
            }
        }

        describe("ignores valid casts") {

            it("ignores MutableList casts") {
                val code = """
                fun main() {
                    val myList = mutableListOf(1,2,3)
                    val mutList = myList as MutableList<Int>
                }
                """
                val result = subject.compileAndLintWithContext(env, code)
                assertThat(result).isEmpty()
            }

            it("ignores MutableSet casts") {
                val code = """
                fun main() {
                    val mySet = mutableSetOf(1,2,3)
                    val mutSet = mySet as MutableSet<Int>
                }
                """
                val result = subject.compileAndLintWithContext(env, code)
                assertThat(result).isEmpty()
            }

            it("ignores Map type casts") {
                val code = """
                fun main() {
                    val myMap = mutableMapOf(1 to 2)
                    val mutMap = myMap as MutableMap<Int, Int>
                }
                """
                val result = subject.compileAndLintWithContext(env, code)
                assertThat(result).isEmpty()
            }

            it("ignores Synthetic types") {
                val code = """
                import kotlinx.android.synthetic.main.tooltip_progress_bar.view.*
                fun main() {
                    val params = tooltip_guide.layoutParams as LayoutParams
                }
                """
                val result = subject.lintWithContext(env, code)
                assertThat(result).isEmpty()
            }
        }

        describe("type-aliases") {

            it("detects ArrayList type casts") {
                val code = """
                fun main() {
                    val myList = listOf(1,2,3)
                    val mutList = myList as ArrayList<Int>
                }
                """
                val result = subject.compileAndLintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result.first().message).isEqualTo(
                    "Down-casting from type List to ArrayList is risky."
                )
            }

            it("detects ArrayList type safe casts") {
                val code = """
                fun main() {
                    val myList : List<Int>? = null
                    val mutList = myList as? ArrayList<Int>
                }
                """
                val result = subject.compileAndLintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result.first().message).isEqualTo(
                    "Down-casting from type List to ArrayList is risky."
                )
            }

            it("detects ArrayList type checks") {
                val code = """
                fun main() {
                    val myList = listOf(1,2,3)
                    if (myList is ArrayList<Int>) {
                        myList.add(4)
                    }
                }
                """
                val result = subject.compileAndLintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result.first().message).isEqualTo(
                    "Down-casting from type List to ArrayList is risky."
                )
            }

            it("detects LinkedHashSet type casts") {
                val code = """
                fun main() {
                    val mySet = setOf(1,2,3)
                    val mutSet = mySet as LinkedHashSet<Int>
                }
                """
                val result = subject.compileAndLintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result.first().message).isEqualTo(
                    "Down-casting from type Set to LinkedHashSet is risky."
                )
            }

            it("detects LinkedHashSet type safe casts") {
                val code = """
                fun main() {
                    val mySet : Set<Int>? = null
                    val mutSet = mySet as? LinkedHashSet<Int>
                }
                """
                val result = subject.compileAndLintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result.first().message).isEqualTo(
                    "Down-casting from type Set to LinkedHashSet is risky."
                )
            }

            it("detects LinkedHashSet type checks") {
                val code = """
                fun main() {
                    val mySet = setOf(1,2,3)
                    if (mySet is LinkedHashSet<Int>) {
                        mySet.add(4)
                    }
                }
                """
                val result = subject.compileAndLintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result.first().message).isEqualTo(
                    "Down-casting from type Set to LinkedHashSet is risky."
                )
            }

            it("detects HashSet type casts") {
                val code = """
                fun main() {
                    val mySet = setOf(1,2,3)
                    val mutSet = mySet as HashSet<Int>
                }
                """
                val result = subject.compileAndLintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result.first().message).isEqualTo(
                    "Down-casting from type Set to HashSet is risky."
                )
            }

            it("detects HashSet type safe casts") {
                val code = """
                fun main() {
                    val mySet : Set<Int>? = null
                    val mutSet = mySet as? HashSet<Int>
                }
                """
                val result = subject.compileAndLintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result.first().message).isEqualTo(
                    "Down-casting from type Set to HashSet is risky."
                )
            }

            it("detects HashSet type checks") {
                val code = """
                fun main() {
                    val mySet = setOf(1,2,3)
                    if (mySet is HashSet<Int>) {
                        mySet.add(4)
                    }
                }
                """
                val result = subject.compileAndLintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result.first().message).isEqualTo(
                    "Down-casting from type Set to HashSet is risky."
                )
            }

            it("detects HashMap type casts") {
                val code = """
                fun main() {
                    val myMap = mapOf(1 to 2)
                    val mutMap = myMap as HashMap<Int, Int>
                }
                """
                val result = subject.compileAndLintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result.first().message).isEqualTo(
                    "Down-casting from type Map to HashMap is risky."
                )
            }

            it("detects HashMap type safe casts") {
                val code = """
                fun main() {
                    val myMap : Map<Int, Int>? = null
                    val mutMap = myMap as? HashMap<Int, Int>
                }
                """
                val result = subject.compileAndLintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result.first().message).isEqualTo(
                    "Down-casting from type Map to HashMap is risky."
                )
            }

            it("detects HashMap type checks") {
                val code = """
                fun main() {
                    val myMap = mapOf(1 to 2)
                    if (myMap is HashMap<Int, Int>) {
                        myMap[3] = 4
                    }
                }
                """
                val result = subject.compileAndLintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result.first().message).isEqualTo(
                    "Down-casting from type Map to HashMap is risky."
                )
            }

            it("detects LinkedHashMap type casts") {
                val code = """
                fun main() {
                    val myMap = mapOf(1 to 2)
                    val mutMap = myMap as LinkedHashMap<Int, Int>
                }
                """
                val result = subject.compileAndLintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result.first().message).isEqualTo(
                    "Down-casting from type Map to LinkedHashMap is risky."
                )
            }

            it("detects LinkedHashMap type safe casts") {
                val code = """
                fun main() {
                    val myMap : Map<Int, Int>? = null
                    val mutMap = myMap as? LinkedHashMap<Int, Int>
                }
                """
                val result = subject.compileAndLintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result.first().message).isEqualTo(
                    "Down-casting from type Map to LinkedHashMap is risky."
                )
            }

            it("detects LinkedHashMap type checks") {
                val code = """
                fun main() {
                    val myMap = mapOf(1 to 2)
                    if (myMap is LinkedHashMap<Int, Int>) {
                        myMap[3] = 4
                    }
                }
                """
                val result = subject.compileAndLintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result.first().message).isEqualTo(
                    "Down-casting from type Map to LinkedHashMap is risky."
                )
            }
        }
    }
})
