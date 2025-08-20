package dev.detekt.rules.bugs

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinCoreEnvironmentTest
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class DontDowncastCollectionTypesSpec(private val env: KotlinEnvironmentContainer) {
    private val subject = DontDowncastCollectionTypes(Config.empty)

    @Nested
    inner class `valid cases` {
        @Test
        fun `detects List type casts`() {
            val code = """
                fun main() {
                    val myList = listOf(1,2,3)
                    val mutList = myList as MutableList<Int>
                }
            """.trimIndent()
            val result = subject.lintWithContext(env, code)
            assertThat(result).hasSize(1)
            assertThat(result.first().message).isEqualTo(
                "Down-casting from type List to MutableList is risky. Use `toMutableList()` instead."
            )
        }

        @Test
        fun `detects List type safe casts`() {
            val code = """
                fun main() {
                    val myList : List<Int>? = null
                    val mutList = myList as? MutableList<Int>
                }
            """.trimIndent()
            val result = subject.lintWithContext(env, code)
            assertThat(result).hasSize(1)
            assertThat(result.first().message).isEqualTo(
                "Down-casting from type List to MutableList is risky. Use `toMutableList()` instead."
            )
        }

        @Test
        fun `detects List type checks`() {
            val code = """
                fun main() {
                    val myList = listOf(1,2,3)
                    if (myList is MutableList<Int>) {
                        myList.add(4)
                    }
                }
            """.trimIndent()
            val result = subject.lintWithContext(env, code)
            assertThat(result).hasSize(1)
            assertThat(result.first().message).isEqualTo(
                "Down-casting from type List to MutableList is risky. Use `toMutableList()` instead."
            )
        }

        @Test
        fun `detects Set type casts`() {
            val code = """
                fun main() {
                    val mySet = setOf(1,2,3)
                    val mutSet = mySet as MutableSet<Int>
                }
            """.trimIndent()
            val result = subject.lintWithContext(env, code)
            assertThat(result).hasSize(1)
            assertThat(result.first().message).isEqualTo(
                "Down-casting from type Set to MutableSet is risky. Use `toMutableSet()` instead."
            )
        }

        @Test
        fun `detects Set type safe casts`() {
            val code = """
                fun main() {
                    val mySet : Set<Int>? = null
                    val mutSet = mySet as? MutableSet<Int>
                }
            """.trimIndent()
            val result = subject.lintWithContext(env, code)
            assertThat(result).hasSize(1)
            assertThat(result.first().message).isEqualTo(
                "Down-casting from type Set to MutableSet is risky. Use `toMutableSet()` instead."
            )
        }

        @Test
        fun `detects Set type checks`() {
            val code = """
                fun main() {
                    val mySet = setOf(1,2,3)
                    if (mySet is MutableSet<Int>) {
                        mySet.add(4)
                    }
                }
            """.trimIndent()
            val result = subject.lintWithContext(env, code)
            assertThat(result).hasSize(1)
            assertThat(result.first().message).isEqualTo(
                "Down-casting from type Set to MutableSet is risky. Use `toMutableSet()` instead."
            )
        }

        @Test
        fun `detects Map type casts`() {
            val code = """
                fun main() {
                    val myMap = mapOf(1 to 2)
                    val mutMap = myMap as MutableMap<Int, Int>
                }
            """.trimIndent()
            val result = subject.lintWithContext(env, code)
            assertThat(result).hasSize(1)
            assertThat(result.first().message).isEqualTo(
                "Down-casting from type Map to MutableMap is risky. Use `toMutableMap()` instead."
            )
        }

        @Test
        fun `detects Map type safe casts`() {
            val code = """
                fun main() {
                    val myMap : Map<Int, Int>? = null
                    val mutMap = myMap as? MutableMap<Int, Int>
                }
            """.trimIndent()
            val result = subject.lintWithContext(env, code)
            assertThat(result).hasSize(1)
            assertThat(result.first().message).isEqualTo(
                "Down-casting from type Map to MutableMap is risky. Use `toMutableMap()` instead."
            )
        }

        @Test
        fun `detects Map type checks`() {
            val code = """
                fun main() {
                    val myMap = mapOf(1 to 2)
                    if (myMap is MutableMap<Int, Int>) {
                        myMap[3] = 4
                    }
                }
            """.trimIndent()
            val result = subject.lintWithContext(env, code)
            assertThat(result).hasSize(1)
            assertThat(result.first().message).isEqualTo(
                "Down-casting from type Map to MutableMap is risky. Use `toMutableMap()` instead."
            )
        }
    }

    @Nested
    inner class `ignores valid casts` {

        @Test
        fun `ignores MutableList casts`() {
            val code = """
                fun main() {
                    val myList = mutableListOf(1,2,3)
                    val mutList = myList as MutableList<Int>
                }
            """.trimIndent()
            val result = subject.lintWithContext(env, code)
            assertThat(result).isEmpty()
        }

        @Test
        fun `ignores MutableSet casts`() {
            val code = """
                fun main() {
                    val mySet = mutableSetOf(1,2,3)
                    val mutSet = mySet as MutableSet<Int>
                }
            """.trimIndent()
            val result = subject.lintWithContext(env, code)
            assertThat(result).isEmpty()
        }

        @Test
        fun `ignores Map type casts`() {
            val code = """
                fun main() {
                    val myMap = mutableMapOf(1 to 2)
                    val mutMap = myMap as MutableMap<Int, Int>
                }
            """.trimIndent()
            val result = subject.lintWithContext(env, code)
            assertThat(result).isEmpty()
        }
    }

    @Nested
    inner class `type-aliases` {

        @Test
        fun `detects ArrayList type casts`() {
            val code = """
                fun main() {
                    val myList = listOf(1,2,3)
                    val mutList = myList as ArrayList<Int>
                }
            """.trimIndent()
            val result = subject.lintWithContext(env, code)
            assertThat(result).hasSize(1)
            assertThat(result.first().message).isEqualTo(
                "Down-casting from type List to ArrayList is risky."
            )
        }

        @Test
        fun `detects ArrayList type safe casts`() {
            val code = """
                fun main() {
                    val myList : List<Int>? = null
                    val mutList = myList as? ArrayList<Int>
                }
            """.trimIndent()
            val result = subject.lintWithContext(env, code)
            assertThat(result).hasSize(1)
            assertThat(result.first().message).isEqualTo(
                "Down-casting from type List to ArrayList is risky."
            )
        }

        @Test
        fun `detects ArrayList type checks`() {
            val code = """
                fun main() {
                    val myList = listOf(1,2,3)
                    if (myList is ArrayList<Int>) {
                        myList.add(4)
                    }
                }
            """.trimIndent()
            val result = subject.lintWithContext(env, code)
            assertThat(result).hasSize(1)
            assertThat(result.first().message).isEqualTo(
                "Down-casting from type List to ArrayList is risky."
            )
        }

        @Test
        fun `detects LinkedHashSet type casts`() {
            val code = """
                fun main() {
                    val mySet = setOf(1,2,3)
                    val mutSet = mySet as LinkedHashSet<Int>
                }
            """.trimIndent()
            val result = subject.lintWithContext(env, code)
            assertThat(result).hasSize(1)
            assertThat(result.first().message).isEqualTo(
                "Down-casting from type Set to LinkedHashSet is risky."
            )
        }

        @Test
        fun `detects LinkedHashSet type safe casts`() {
            val code = """
                fun main() {
                    val mySet : Set<Int>? = null
                    val mutSet = mySet as? LinkedHashSet<Int>
                }
            """.trimIndent()
            val result = subject.lintWithContext(env, code)
            assertThat(result).hasSize(1)
            assertThat(result.first().message).isEqualTo(
                "Down-casting from type Set to LinkedHashSet is risky."
            )
        }

        @Test
        fun `detects LinkedHashSet type checks`() {
            val code = """
                fun main() {
                    val mySet = setOf(1,2,3)
                    if (mySet is LinkedHashSet<Int>) {
                        mySet.add(4)
                    }
                }
            """.trimIndent()
            val result = subject.lintWithContext(env, code)
            assertThat(result).hasSize(1)
            assertThat(result.first().message).isEqualTo(
                "Down-casting from type Set to LinkedHashSet is risky."
            )
        }

        @Test
        fun `detects HashSet type casts`() {
            val code = """
                fun main() {
                    val mySet = setOf(1,2,3)
                    val mutSet = mySet as HashSet<Int>
                }
            """.trimIndent()
            val result = subject.lintWithContext(env, code)
            assertThat(result).hasSize(1)
            assertThat(result.first().message).isEqualTo(
                "Down-casting from type Set to HashSet is risky."
            )
        }

        @Test
        fun `detects HashSet type safe casts`() {
            val code = """
                fun main() {
                    val mySet : Set<Int>? = null
                    val mutSet = mySet as? HashSet<Int>
                }
            """.trimIndent()
            val result = subject.lintWithContext(env, code)
            assertThat(result).hasSize(1)
            assertThat(result.first().message).isEqualTo(
                "Down-casting from type Set to HashSet is risky."
            )
        }

        @Test
        fun `detects HashSet type checks`() {
            val code = """
                fun main() {
                    val mySet = setOf(1,2,3)
                    if (mySet is HashSet<Int>) {
                        mySet.add(4)
                    }
                }
            """.trimIndent()
            val result = subject.lintWithContext(env, code)
            assertThat(result).hasSize(1)
            assertThat(result.first().message).isEqualTo(
                "Down-casting from type Set to HashSet is risky."
            )
        }

        @Test
        fun `detects HashMap type casts`() {
            val code = """
                fun main() {
                    val myMap = mapOf(1 to 2)
                    val mutMap = myMap as HashMap<Int, Int>
                }
            """.trimIndent()
            val result = subject.lintWithContext(env, code)
            assertThat(result).hasSize(1)
            assertThat(result.first().message).isEqualTo(
                "Down-casting from type Map to HashMap is risky."
            )
        }

        @Test
        fun `detects HashMap type safe casts`() {
            val code = """
                fun main() {
                    val myMap : Map<Int, Int>? = null
                    val mutMap = myMap as? HashMap<Int, Int>
                }
            """.trimIndent()
            val result = subject.lintWithContext(env, code)
            assertThat(result).hasSize(1)
            assertThat(result.first().message).isEqualTo(
                "Down-casting from type Map to HashMap is risky."
            )
        }

        @Test
        fun `detects HashMap type checks`() {
            val code = """
                fun main() {
                    val myMap = mapOf(1 to 2)
                    if (myMap is HashMap<Int, Int>) {
                        myMap[3] = 4
                    }
                }
            """.trimIndent()
            val result = subject.lintWithContext(env, code)
            assertThat(result).hasSize(1)
            assertThat(result.first().message).isEqualTo(
                "Down-casting from type Map to HashMap is risky."
            )
        }

        @Test
        fun `detects LinkedHashMap type casts`() {
            val code = """
                fun main() {
                    val myMap = mapOf(1 to 2)
                    val mutMap = myMap as LinkedHashMap<Int, Int>
                }
            """.trimIndent()
            val result = subject.lintWithContext(env, code)
            assertThat(result).hasSize(1)
            assertThat(result.first().message).isEqualTo(
                "Down-casting from type Map to LinkedHashMap is risky."
            )
        }

        @Test
        fun `detects LinkedHashMap type safe casts`() {
            val code = """
                fun main() {
                    val myMap : Map<Int, Int>? = null
                    val mutMap = myMap as? LinkedHashMap<Int, Int>
                }
            """.trimIndent()
            val result = subject.lintWithContext(env, code)
            assertThat(result).hasSize(1)
            assertThat(result.first().message).isEqualTo(
                "Down-casting from type Map to LinkedHashMap is risky."
            )
        }

        @Test
        fun `detects LinkedHashMap type checks`() {
            val code = """
                fun main() {
                    val myMap = mapOf(1 to 2)
                    if (myMap is LinkedHashMap<Int, Int>) {
                        myMap[3] = 4
                    }
                }
            """.trimIndent()
            val result = subject.lintWithContext(env, code)
            assertThat(result).hasSize(1)
            assertThat(result.first().message).isEqualTo(
                "Down-casting from type Map to LinkedHashMap is risky."
            )
        }
    }
}
