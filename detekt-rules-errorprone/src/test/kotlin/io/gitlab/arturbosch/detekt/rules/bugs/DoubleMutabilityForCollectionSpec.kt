package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class DoubleMutabilityForCollectionSpec(private val env: KotlinCoreEnvironment) {
    private val subject = DoubleMutabilityForCollection(Config.empty)

    @Nested
    inner class `DoubleMutabilityForCollection rule` {

        @Nested
        inner class `local variables` {

            @Nested
            inner class `valid cases` {

                @Test
                fun `detects var declaration with mutable list`() {
                    val code = """
                    fun main() {
                        var myList = mutableListOf(1,2,3)
                    }
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).hasSize(1)
                    assertThat(result).hasSourceLocation(2, 5)
                }

                @Test
                fun `detects var declaration with mutable set`() {
                    val code = """
                    fun main() {
                        var mySet = mutableSetOf(1,2,3)
                    }
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).hasSize(1)
                    assertThat(result).hasSourceLocation(2, 5)
                }

                @Test
                fun `detects var declaration with mutable map`() {
                    val code = """
                    fun main() {
                        var myMap = mutableMapOf("answer" to 42)
                    }
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).hasSize(1)
                    assertThat(result).hasSourceLocation(2, 5)
                }

                @Test
                fun `detects var declaration with ArrayList`() {
                    val code = """
                    fun main() {
                        var myArrayList = ArrayList<Int>()
                    }
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).hasSize(1)
                    assertThat(result).hasSourceLocation(2, 5)
                }

                @Test
                fun `detects var declaration with LinkedHashSet`() {
                    val code = """
                    fun main() {
                        var myLinkedHashSet = LinkedHashSet<Int>()
                    }
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).hasSize(1)
                    assertThat(result).hasSourceLocation(2, 5)
                }

                @Test
                fun `detects var declaration with HashSet`() {
                    val code = """
                    fun main() {
                        var myHashSet = HashSet<Int>()
                    }
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).hasSize(1)
                    assertThat(result).hasSourceLocation(2, 5)
                }

                @Test
                fun `detects var declaration with LinkedHashMap`() {
                    val code = """
                    fun main() {
                        var myLinkedHashMap = LinkedHashMap<String, Int>()
                    }
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).hasSize(1)
                    assertThat(result).hasSourceLocation(2, 5)
                }

                @Test
                fun `detects var declaration with HashMap`() {
                    val code = """
                    fun main() {
                        var myHashMap = HashMap<String, Int>()
                    }
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).hasSize(1)
                    assertThat(result).hasSourceLocation(2, 5)
                }
            }

            @Nested
            inner class `ignores declaration with val` {

                @Test
                fun `does not detect val declaration with mutable list`() {
                    val code = """
                    fun main() {
                        val myList = mutableListOf(1,2,3)
                    }
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).isEmpty()
                }

                @Test
                fun `does not detect val declaration with mutable set`() {
                    val code = """
                    fun main() {
                        val mySet = mutableSetOf(1,2,3)
                    }
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).isEmpty()
                }

                @Test
                fun `does not detect val declaration with mutable map`() {
                    val code = """
                    fun main() {
                        val myMap = mutableMapOf("answer" to 42)
                    }
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).isEmpty()
                }

                @Test
                fun `does not detect val declaration with ArrayList`() {
                    val code = """
                    fun main() {
                        val myArrayList = ArrayList<Int>()
                    }
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).isEmpty()
                }

                @Test
                fun `does not detect val declaration with LinkedHashSet`() {
                    val code = """
                    fun main() {
                        val myLinkedHashSet = LinkedHashSet<Int>()
                    }
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).isEmpty()
                }

                @Test
                fun `does not detect val declaration with HashSet`() {
                    val code = """
                    fun main() {
                        val myHashSet = HashSet<Int>()
                    }
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).isEmpty()
                }

                @Test
                fun `does not detect val declaration with LinkedHashMap`() {
                    val code = """
                    fun main() {
                        val myLinkedHashMap = LinkedHashMap<String, Int>()
                    }
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).isEmpty()
                }

                @Test
                fun `does not detect val declaration with HashMap`() {
                    val code = """
                    fun main() {
                        val myHashMap = HashMap<String, Int>()
                    }
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).isEmpty()
                }
            }

            @Nested
            inner class `ignores declaration with var and immutable types` {

                @Test
                fun `does not detect var declaration with immutable list`() {
                    val code = """
                    fun main() {
                        val myList = listOf(1,2,3)
                    }
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).isEmpty()
                }

                @Test
                fun `does not detect var declaration with immutable set`() {
                    val code = """
                    fun main() {
                        val mySet = setOf(1,2,3)
                    }
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).isEmpty()
                }

                @Test
                fun `does not detect var declaration with immutable map`() {
                    val code = """
                    fun main() {
                        val myMap = mapOf("answer" to 42)
                    }
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).isEmpty()
                }
            }
        }

        @Nested
        inner class `top level variables` {

            @Nested
            inner class `valid cases` {

                @Test
                fun `detects var declaration with mutable list`() {
                    val code = """
                    var myList = mutableListOf(1,2,3)
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).hasSize(1)
                    assertThat(result).hasSourceLocation(1, 1)
                }

                @Test
                fun `detects var declaration with mutable set`() {
                    val code = """
                    var mySet = mutableSetOf(1,2,3)
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).hasSize(1)
                    assertThat(result).hasSourceLocation(1, 1)
                }

                @Test
                fun `detects var declaration with mutable map`() {
                    val code = """
                    var myMap = mutableMapOf("answer" to 42)
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).hasSize(1)
                    assertThat(result).hasSourceLocation(1, 1)
                }

                @Test
                fun `detects var declaration with ArrayList`() {
                    val code = """
                    var myArrayList = ArrayList<Int>()
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).hasSize(1)
                    assertThat(result).hasSourceLocation(1, 1)
                }

                @Test
                fun `detects var declaration with LinkedHashSet`() {
                    val code = """
                    var myLinkedHashSet = LinkedHashSet<Int>()
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).hasSize(1)
                    assertThat(result).hasSourceLocation(1, 1)
                }

                @Test
                fun `detects var declaration with HashSet`() {
                    val code = """
                    var myHashSet = HashSet<Int>()
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).hasSize(1)
                    assertThat(result).hasSourceLocation(1, 1)
                }

                @Test
                fun `detects var declaration with LinkedHashMap`() {
                    val code = """
                    var myLinkedHashMap = LinkedHashMap<String, Int>()
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).hasSize(1)
                    assertThat(result).hasSourceLocation(1, 1)
                }

                @Test
                fun `detects var declaration with HashMap`() {
                    val code = """
                    var myHashMap = HashMap<String, Int>()
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).hasSize(1)
                    assertThat(result).hasSourceLocation(1, 1)
                }
            }

            @Nested
            inner class `ignores declaration with val` {

                @Test
                fun `does not detect val declaration with mutable list`() {
                    val code = """
                    val myList = mutableListOf(1,2,3)
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).isEmpty()
                }

                @Test
                fun `does not detect val declaration with mutable set`() {
                    val code = """
                    val mySet = mutableSetOf(1,2,3)
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).isEmpty()
                }

                @Test
                fun `does not detect val declaration with mutable map`() {
                    val code = """
                    val myMap = mutableMapOf("answer" to 42)
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).isEmpty()
                }

                @Test
                fun `does not detect val declaration with ArrayList`() {
                    val code = """
                    val myArrayList = ArrayList<Int>()
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).isEmpty()
                }

                @Test
                fun `does not detect val declaration with LinkedHashSet`() {
                    val code = """
                    val myLinkedHashSet = LinkedHashSet<Int>()
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).isEmpty()
                }

                @Test
                fun `does not detect val declaration with HashSet`() {
                    val code = """
                    val myHashSet = HashSet<Int>()
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).isEmpty()
                }

                @Test
                fun `does not detect val declaration with LinkedHashMap`() {
                    val code = """
                    val myLinkedHashMap = LinkedHashMap<String, Int>()
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).isEmpty()
                }

                @Test
                fun `does not detect val declaration with HashMap`() {
                    val code = """
                    val myHashMap = HashMap<String, Int>()
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).isEmpty()
                }
            }

            @Nested
            inner class `ignores declaration with var and immutable types` {

                @Test
                fun `does not detect var declaration with immutable list`() {
                    val code = """
                    val myList = listOf(1,2,3)
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).isEmpty()
                }

                @Test
                fun `does not detect var declaration with immutable set`() {
                    val code = """
                    val mySet = setOf(1,2,3)
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).isEmpty()
                }

                @Test
                fun `does not detect var declaration with immutable map`() {
                    val code = """
                    val myMap = mapOf("answer" to 42)
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).isEmpty()
                }
            }
        }

        @Nested
        inner class `class properties` {

            @Nested
            inner class `valid cases` {

                @Test
                fun `detects var declaration with mutable list`() {
                    val code = """
                    class MyClass {
                        var myOtherList = mutableListOf(1,2,3)
                    }
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).hasSize(1)
                    assertThat(result).hasSourceLocation(2, 5)
                }

                @Test
                fun `detects var declaration with mutable set`() {
                    val code = """
                    class MyClass {
                        var mySet = mutableSetOf(1,2,3)
                    }
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).hasSize(1)
                    assertThat(result).hasSourceLocation(2, 5)
                }

                @Test
                fun `detects var declaration with mutable map`() {
                    val code = """
                    class MyClass {
                        var myMap = mutableMapOf("answer" to 42)
                    }
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).hasSize(1)
                    assertThat(result).hasSourceLocation(2, 5)
                }

                @Test
                fun `detects var declaration with ArrayList`() {
                    val code = """
                    class MyClass {
                        var myArrayList = ArrayList<Int>()
                    }
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).hasSize(1)
                    assertThat(result).hasSourceLocation(2, 5)
                }

                @Test
                fun `detects var declaration with LinkedHashSet`() {
                    val code = """
                    class MyClass {
                        var myLinkedHashSet = LinkedHashSet<Int>()
                    }
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).hasSize(1)
                    assertThat(result).hasSourceLocation(2, 5)
                }

                @Test
                fun `detects var declaration with HashSet`() {
                    val code = """
                    class MyClass {
                        var myHashSet = HashSet<Int>()
                    }
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).hasSize(1)
                    assertThat(result).hasSourceLocation(2, 5)
                }

                @Test
                fun `detects var declaration with LinkedHashMap`() {
                    val code = """
                    class MyClass {
                        var myLinkedHashMap = LinkedHashMap<String, Int>()
                    }
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).hasSize(1)
                    assertThat(result).hasSourceLocation(2, 5)
                }

                @Test
                fun `detects var declaration with HashMap`() {
                    val code = """
                    class MyClass {
                        var myHashMap = HashMap<String, Int>()
                    }
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).hasSize(1)
                    assertThat(result).hasSourceLocation(2, 5)
                }
            }

            @Nested
            inner class `ignores declaration with val` {

                @Test
                fun `does not detect val declaration with mutable list`() {
                    val code = """
                    class MyClass {
                        val myList = mutableListOf(1,2,3)
                    }
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).isEmpty()
                }

                @Test
                fun `does not detect val declaration with mutable set`() {
                    val code = """
                    class MyClass {
                        val mySet = mutableSetOf(1,2,3)
                    }
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).isEmpty()
                }

                @Test
                fun `does not detect val declaration with mutable map`() {
                    val code = """
                    class MyClass {
                        val myMap = mutableMapOf("answer" to 42)
                    }
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).isEmpty()
                }

                @Test
                fun `does not detect val declaration with ArrayList`() {
                    val code = """
                    class MyClass {
                        val myArrayList = ArrayList<Int>()
                    }
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).isEmpty()
                }

                @Test
                fun `does not detect val declaration with LinkedHashSet`() {
                    val code = """
                    class MyClass {
                        val myLinkedHashSet = LinkedHashSet<Int>()
                    }
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).isEmpty()
                }

                @Test
                fun `does not detect val declaration with HashSet`() {
                    val code = """
                    class MyClass {
                        val myHashSet = HashSet<Int>()
                    }
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).isEmpty()
                }

                @Test
                fun `does not detect val declaration with LinkedHashMap`() {
                    val code = """
                    class MyClass {
                        val myLinkedHashMap = LinkedHashMap<String, Int>()
                    }
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).isEmpty()
                }

                @Test
                fun `does not detect val declaration with HashMap`() {
                    val code = """
                    class MyClass {
                        val myHashMap = HashMap<String, Int>()
                    }
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).isEmpty()
                }
            }

            @Nested
            inner class `ignores declaration with var and immutable types` {

                @Test
                fun `does not detect var declaration with immutable list`() {
                    val code = """
                    class MyClass {
                        val myList = listOf(1,2,3)
                    }
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).isEmpty()
                }

                @Test
                fun `does not detect var declaration with immutable set`() {
                    val code = """
                    class MyClass {
                        val mySet = setOf(1,2,3)
                    }
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).isEmpty()
                }

                @Test
                fun `does not detect var declaration with immutable map`() {
                    val code = """
                    class MyClass {
                        val myMap = mapOf("answer" to 42)
                    }
                    """
                    val result = subject.compileAndLintWithContext(env, code)
                    assertThat(result).isEmpty()
                }
            }
        }
    }
}
