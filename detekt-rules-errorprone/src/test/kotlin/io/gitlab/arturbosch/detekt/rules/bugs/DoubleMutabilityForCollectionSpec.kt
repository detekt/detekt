package io.gitlab.arturbosch.detekt.rules.bugs

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.assertThat
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinCoreEnvironmentTest
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private const val MUTABLE_TYPES = "mutableTypes"

@KotlinCoreEnvironmentTest
class DoubleMutabilityForCollectionSpec(private val env: KotlinEnvironmentContainer) {
    private val subject = DoubleMutabilityForCollection(Config.empty)

    @Nested
    inner class `local variables` {

        @Nested
        inner class `valid cases` {

            @Test
            fun `detects var declaration with mutable list`() {
                val code = """
                    fun main() {
                        var myList = mutableListOf(1, 2, 3)
                    }
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result).hasStartSourceLocation(2, 5)
            }

            @Test
            fun `detects var declaration with mutable set`() {
                val code = """
                    fun main() {
                        var mySet = mutableSetOf(1, 2, 3)
                    }
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result).hasStartSourceLocation(2, 5)
            }

            @Test
            fun `detects var declaration with mutable map`() {
                val code = """
                    fun main() {
                        var myMap = mutableMapOf("answer" to 42)
                    }
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result).hasStartSourceLocation(2, 5)
            }

            @Test
            fun `detects var declaration with ArrayList`() {
                val code = """
                    fun main() {
                        var myArrayList = ArrayList<Int>()
                    }
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result).hasStartSourceLocation(2, 5)
            }

            @Test
            fun `detects var declaration with LinkedHashSet`() {
                val code = """
                    fun main() {
                        var myLinkedHashSet = LinkedHashSet<Int>()
                    }
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result).hasStartSourceLocation(2, 5)
            }

            @Test
            fun `detects var declaration with HashSet`() {
                val code = """
                    fun main() {
                        var myHashSet = HashSet<Int>()
                    }
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result).hasStartSourceLocation(2, 5)
            }

            @Test
            fun `detects var declaration with LinkedHashMap`() {
                val code = """
                    fun main() {
                        var myLinkedHashMap = LinkedHashMap<String, Int>()
                    }
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result).hasStartSourceLocation(2, 5)
            }

            @Test
            fun `detects var declaration with HashMap`() {
                val code = """
                    fun main() {
                        var myHashMap = HashMap<String, Int>()
                    }
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result).hasStartSourceLocation(2, 5)
            }

            @Test
            fun `detects var declaration with MutableState, when configured`() {
                val rule = DoubleMutabilityForCollection(
                    TestConfig(MUTABLE_TYPES to listOf("MutableState"))
                )

                val code = """
                    data class MutableState<T>(var state: T)
                    fun main() {
                        var myState = MutableState("foo")
                    }
                """.trimIndent()
                val result = rule.lintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result).hasStartSourceLocation(3, 5)
            }

            @Test
            fun `detects var declaration with MutableState via factory function, when configured`() {
                val rule = DoubleMutabilityForCollection(
                    TestConfig(MUTABLE_TYPES to listOf("MutableState"))
                )

                val code = """
                    data class MutableState<T>(var state: T)
                    fun <T> mutableStateOf(value: T): MutableState<T> = MutableState(value)
                    fun main() {
                        var myState = mutableStateOf("foo")
                    }
                """.trimIndent()
                val result = rule.lintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result).hasStartSourceLocation(4, 5)
            }

            @Test
            fun `detects var declaration with MutableState via calculation lambda, when configured`() {
                val rule = DoubleMutabilityForCollection(TestConfig(MUTABLE_TYPES to listOf("MutableState")))

                val code = """
                    data class MutableState<T>(var state: T)
                    fun <T> mutableStateOf(value: T): MutableState<T> = MutableState(value)
                    fun <T> remember(calculation: () -> T): T = calculation()
                    fun main() {
                        var myState = remember { mutableStateOf("foo") }
                    }
                """.trimIndent()
                val result = rule.lintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result).hasStartSourceLocation(5, 5)
            }
        }

        @Nested
        inner class `ignores declaration with val` {

            @Test
            fun `does not detect val declaration with mutable list`() {
                val code = """
                    fun main() {
                        val myList = mutableListOf(1, 2, 3)
                    }
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).isEmpty()
            }

            @Test
            fun `does not detect val declaration with mutable set`() {
                val code = """
                    fun main() {
                        val mySet = mutableSetOf(1, 2, 3)
                    }
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).isEmpty()
            }

            @Test
            fun `does not detect val declaration with mutable map`() {
                val code = """
                    fun main() {
                        val myMap = mutableMapOf("answer" to 42)
                    }
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).isEmpty()
            }

            @Test
            fun `does not detect val declaration with ArrayList`() {
                val code = """
                    fun main() {
                        val myArrayList = ArrayList<Int>()
                    }
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).isEmpty()
            }

            @Test
            fun `does not detect val declaration with LinkedHashSet`() {
                val code = """
                    fun main() {
                        val myLinkedHashSet = LinkedHashSet<Int>()
                    }
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).isEmpty()
            }

            @Test
            fun `does not detect val declaration with HashSet`() {
                val code = """
                    fun main() {
                        val myHashSet = HashSet<Int>()
                    }
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).isEmpty()
            }

            @Test
            fun `does not detect val declaration with LinkedHashMap`() {
                val code = """
                    fun main() {
                        val myLinkedHashMap = LinkedHashMap<String, Int>()
                    }
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).isEmpty()
            }

            @Test
            fun `does not detect val declaration with HashMap`() {
                val code = """
                    fun main() {
                        val myHashMap = HashMap<String, Int>()
                    }
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).isEmpty()
            }
        }

        @Nested
        inner class `ignores declaration with var and immutable types` {

            @Test
            fun `does not detect var declaration with immutable list`() {
                val code = """
                    fun main() {
                        val myList = listOf(1, 2, 3)
                    }
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).isEmpty()
            }

            @Test
            fun `does not detect var declaration with immutable set`() {
                val code = """
                    fun main() {
                        val mySet = setOf(1, 2, 3)
                    }
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).isEmpty()
            }

            @Test
            fun `does not detect var declaration with immutable map`() {
                val code = """
                    fun main() {
                        val myMap = mapOf("answer" to 42)
                    }
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).isEmpty()
            }
        }

        @Nested
        inner class `ignores declaration with var and property delegation` {

            @Test
            fun `does not detect var declaration with property delegate`() {
                val rule = DoubleMutabilityForCollection(
                    TestConfig(MUTABLE_TYPES to listOf("MutableState"))
                )

                val code = """
                    data class MutableState<T>(var state: T)
                    fun <T> mutableStateOf(value: T): MutableState<T> = MutableState(value)
                    fun <T> remember(calculation: () -> T): T = calculation()
                    inline operator fun <T> MutableState<T>.getValue(thisObj: Any?, property: kotlin.reflect.KProperty<*>): T = state
                    inline operator fun <T> MutableState<T>.setValue(thisObj: Any?, property: kotlin.reflect.KProperty<*>, value: T) {
                        this.state = value
                    }
                    fun main() {
                        var myState by remember { mutableStateOf("foo") }
                    }
                """.trimIndent()
                val result = rule.lintWithContext(env, code)
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
                    var myList = mutableListOf(1, 2, 3)
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result).hasStartSourceLocation(1, 1)
            }

            @Test
            fun `detects var declaration with mutable set`() {
                val code = """
                    var mySet = mutableSetOf(1, 2, 3)
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result).hasStartSourceLocation(1, 1)
            }

            @Test
            fun `detects var declaration with mutable map`() {
                val code = """
                    var myMap = mutableMapOf("answer" to 42)
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result).hasStartSourceLocation(1, 1)
            }

            @Test
            fun `detects var declaration with ArrayList`() {
                val code = """
                    var myArrayList = ArrayList<Int>()
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result).hasStartSourceLocation(1, 1)
            }

            @Test
            fun `detects var declaration with LinkedHashSet`() {
                val code = """
                    var myLinkedHashSet = LinkedHashSet<Int>()
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result).hasStartSourceLocation(1, 1)
            }

            @Test
            fun `detects var declaration with HashSet`() {
                val code = """
                    var myHashSet = HashSet<Int>()
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result).hasStartSourceLocation(1, 1)
            }

            @Test
            fun `detects var declaration with LinkedHashMap`() {
                val code = """
                    var myLinkedHashMap = LinkedHashMap<String, Int>()
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result).hasStartSourceLocation(1, 1)
            }

            @Test
            fun `detects var declaration with HashMap`() {
                val code = """
                    var myHashMap = HashMap<String, Int>()
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result).hasStartSourceLocation(1, 1)
            }

            @Test
            fun `detects var declaration with MutableState, when configured`() {
                val rule = DoubleMutabilityForCollection(
                    TestConfig(MUTABLE_TYPES to listOf("MutableState"))
                )

                val code = """
                    data class MutableState<T>(var state: T)
                    var myState = MutableState("foo")
                """.trimIndent()
                val result = rule.lintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result).hasStartSourceLocation(2, 1)
            }

            @Test
            fun `detects var declaration with MutableState via factory function, when configured`() {
                val rule = DoubleMutabilityForCollection(
                    TestConfig(MUTABLE_TYPES to listOf("MutableState"))
                )

                val code = """
                    data class MutableState<T>(var state: T)
                    fun <T> mutableStateOf(value: T): MutableState<T> = MutableState(value)
                    var myState = mutableStateOf("foo")
                """.trimIndent()
                val result = rule.lintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result).hasStartSourceLocation(3, 1)
            }

            @Test
            fun `detects var declaration with MutableState via calculation lambda, when configured`() {
                val rule = DoubleMutabilityForCollection(
                    TestConfig(MUTABLE_TYPES to listOf("MutableState"))
                )

                val code = """
                    data class MutableState<T>(var state: T)
                    fun <T> mutableStateOf(value: T): MutableState<T> = MutableState(value)
                    fun <T> remember(calculation: () -> T): T = calculation()
                    var myState = remember { mutableStateOf("foo") }
                """.trimIndent()
                val result = rule.lintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result).hasStartSourceLocation(4, 1)
            }
        }

        @Nested
        inner class `ignores declaration with val` {

            @Test
            fun `does not detect val declaration with mutable list`() {
                val code = """
                    val myList = mutableListOf(1, 2, 3)
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).isEmpty()
            }

            @Test
            fun `does not detect val declaration with mutable set`() {
                val code = """
                    val mySet = mutableSetOf(1, 2, 3)
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).isEmpty()
            }

            @Test
            fun `does not detect val declaration with mutable map`() {
                val code = """
                    val myMap = mutableMapOf("answer" to 42)
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).isEmpty()
            }

            @Test
            fun `does not detect val declaration with ArrayList`() {
                val code = """
                    val myArrayList = ArrayList<Int>()
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).isEmpty()
            }

            @Test
            fun `does not detect val declaration with LinkedHashSet`() {
                val code = """
                    val myLinkedHashSet = LinkedHashSet<Int>()
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).isEmpty()
            }

            @Test
            fun `does not detect val declaration with HashSet`() {
                val code = """
                    val myHashSet = HashSet<Int>()
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).isEmpty()
            }

            @Test
            fun `does not detect val declaration with LinkedHashMap`() {
                val code = """
                    val myLinkedHashMap = LinkedHashMap<String, Int>()
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).isEmpty()
            }

            @Test
            fun `does not detect val declaration with HashMap`() {
                val code = """
                    val myHashMap = HashMap<String, Int>()
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).isEmpty()
            }
        }

        @Nested
        inner class `ignores declaration with var and immutable types` {

            @Test
            fun `does not detect var declaration with immutable list`() {
                val code = """
                    val myList = listOf(1, 2, 3)
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).isEmpty()
            }

            @Test
            fun `does not detect var declaration with immutable set`() {
                val code = """
                    val mySet = setOf(1, 2, 3)
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).isEmpty()
            }

            @Test
            fun `does not detect var declaration with immutable map`() {
                val code = """
                    val myMap = mapOf("answer" to 42)
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).isEmpty()
            }
        }

        @Nested
        inner class `ignores declaration with var and property delegation` {

            @Test
            fun `does not detect var declaration with property delegate`() {
                val rule = DoubleMutabilityForCollection(
                    TestConfig(MUTABLE_TYPES to listOf("MutableState"))
                )

                val code = """
                    data class MutableState<T>(var state: T)
                    fun <T> mutableStateOf(value: T): MutableState<T> = MutableState(value)
                    fun <T> remember(calculation: () -> T): T = calculation()
                    inline operator fun <T> MutableState<T>.getValue(thisObj: Any?, property: kotlin.reflect.KProperty<*>): T = state
                    inline operator fun <T> MutableState<T>.setValue(thisObj: Any?, property: kotlin.reflect.KProperty<*>, value: T) {
                        this.state = value
                    }
                    var myState by remember { mutableStateOf("foo") }
                """.trimIndent()
                val result = rule.lintWithContext(env, code)
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
                        var myOtherList = mutableListOf(1, 2, 3)
                    }
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result).hasStartSourceLocation(2, 5)
            }

            @Test
            fun `detects var declaration with mutable set`() {
                val code = """
                    class MyClass {
                        var mySet = mutableSetOf(1, 2, 3)
                    }
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result).hasStartSourceLocation(2, 5)
            }

            @Test
            fun `detects var declaration with mutable map`() {
                val code = """
                    class MyClass {
                        var myMap = mutableMapOf("answer" to 42)
                    }
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result).hasStartSourceLocation(2, 5)
            }

            @Test
            fun `detects var declaration with ArrayList`() {
                val code = """
                    class MyClass {
                        var myArrayList = ArrayList<Int>()
                    }
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result).hasStartSourceLocation(2, 5)
            }

            @Test
            fun `detects var declaration with LinkedHashSet`() {
                val code = """
                    class MyClass {
                        var myLinkedHashSet = LinkedHashSet<Int>()
                    }
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result).hasStartSourceLocation(2, 5)
            }

            @Test
            fun `detects var declaration with HashSet`() {
                val code = """
                    class MyClass {
                        var myHashSet = HashSet<Int>()
                    }
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result).hasStartSourceLocation(2, 5)
            }

            @Test
            fun `detects var declaration with LinkedHashMap`() {
                val code = """
                    class MyClass {
                        var myLinkedHashMap = LinkedHashMap<String, Int>()
                    }
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result).hasStartSourceLocation(2, 5)
            }

            @Test
            fun `detects var declaration with HashMap`() {
                val code = """
                    class MyClass {
                        var myHashMap = HashMap<String, Int>()
                    }
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result).hasStartSourceLocation(2, 5)
            }

            @Test
            fun `detects var declaration with MutableState, when configured`() {
                val rule = DoubleMutabilityForCollection(
                    TestConfig(MUTABLE_TYPES to listOf("MutableState"))
                )

                val code = """
                    data class MutableState<T>(var state: T)
                    class MyClass {
                        var myState = MutableState("foo")
                    }
                """.trimIndent()
                val result = rule.lintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result).hasStartSourceLocation(3, 5)
            }

            @Test
            fun `detects var declaration with MutableState via factory function, when configured`() {
                val rule = DoubleMutabilityForCollection(
                    TestConfig(MUTABLE_TYPES to listOf("MutableState"))
                )

                val code = """
                    data class MutableState<T>(var state: T)
                    fun <T> mutableStateOf(value: T): MutableState<T> = MutableState(value)
                    class MyClass {
                        var myState = mutableStateOf("foo")
                    }
                """.trimIndent()
                val result = rule.lintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result).hasStartSourceLocation(4, 5)
            }

            @Test
            fun `detects var declaration with MutableState via calculation lambda, when configured`() {
                val rule = DoubleMutabilityForCollection(
                    TestConfig(MUTABLE_TYPES to listOf("MutableState"))
                )

                val code = """
                    data class MutableState<T>(var state: T)
                    fun <T> mutableStateOf(value: T): MutableState<T> = MutableState(value)
                    fun <T> remember(calculation: () -> T): T = calculation()
                    class MyClass {
                        var myState = remember { mutableStateOf("foo") }
                    }
                """.trimIndent()
                val result = rule.lintWithContext(env, code)
                assertThat(result).hasSize(1)
                assertThat(result).hasStartSourceLocation(5, 5)
            }
        }

        @Nested
        inner class `ignores declaration with val` {

            @Test
            fun `does not detect val declaration with mutable list`() {
                val code = """
                    class MyClass {
                        val myList = mutableListOf(1, 2, 3)
                    }
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).isEmpty()
            }

            @Test
            fun `does not detect val declaration with mutable set`() {
                val code = """
                    class MyClass {
                        val mySet = mutableSetOf(1, 2, 3)
                    }
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).isEmpty()
            }

            @Test
            fun `does not detect val declaration with mutable map`() {
                val code = """
                    class MyClass {
                        val myMap = mutableMapOf("answer" to 42)
                    }
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).isEmpty()
            }

            @Test
            fun `does not detect val declaration with ArrayList`() {
                val code = """
                    class MyClass {
                        val myArrayList = ArrayList<Int>()
                    }
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).isEmpty()
            }

            @Test
            fun `does not detect val declaration with LinkedHashSet`() {
                val code = """
                    class MyClass {
                        val myLinkedHashSet = LinkedHashSet<Int>()
                    }
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).isEmpty()
            }

            @Test
            fun `does not detect val declaration with HashSet`() {
                val code = """
                    class MyClass {
                        val myHashSet = HashSet<Int>()
                    }
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).isEmpty()
            }

            @Test
            fun `does not detect val declaration with LinkedHashMap`() {
                val code = """
                    class MyClass {
                        val myLinkedHashMap = LinkedHashMap<String, Int>()
                    }
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).isEmpty()
            }

            @Test
            fun `does not detect val declaration with HashMap`() {
                val code = """
                    class MyClass {
                        val myHashMap = HashMap<String, Int>()
                    }
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).isEmpty()
            }
        }

        @Nested
        inner class `ignores declaration with var and immutable types` {

            @Test
            fun `does not detect var declaration with immutable list`() {
                val code = """
                    class MyClass {
                        val myList = listOf(1, 2, 3)
                    }
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).isEmpty()
            }

            @Test
            fun `does not detect var declaration with immutable set`() {
                val code = """
                    class MyClass {
                        val mySet = setOf(1, 2, 3)
                    }
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).isEmpty()
            }

            @Test
            fun `does not detect var declaration with immutable map`() {
                val code = """
                    class MyClass {
                        val myMap = mapOf("answer" to 42)
                    }
                """.trimIndent()
                val result = subject.lintWithContext(env, code)
                assertThat(result).isEmpty()
            }
        }

        @Nested
        inner class `ignores declaration with var and property delegation` {

            @Test
            fun `does not detect var declaration with property delegate`() {
                val rule = DoubleMutabilityForCollection(
                    TestConfig(MUTABLE_TYPES to listOf("MutableState"))
                )

                val code = """
                    data class MutableState<T>(var state: T)
                    fun <T> mutableStateOf(value: T): MutableState<T> = MutableState(value)
                    fun <T> remember(calculation: () -> T): T = calculation()
                    inline operator fun <T> MutableState<T>.getValue(thisObj: Any?, property: kotlin.reflect.KProperty<*>): T = state
                    inline operator fun <T> MutableState<T>.setValue(thisObj: Any?, property: kotlin.reflect.KProperty<*>, value: T) {
                        this.state = value
                    }
                    class MyClass {
                        var myState by remember { mutableStateOf("foo") }
                    }
                """.trimIndent()
                val result = rule.lintWithContext(env, code)
                assertThat(result).isEmpty()
            }
        }
    }
}
