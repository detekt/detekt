package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UseIfEmptyOrIfBlankSpec(val env: KotlinCoreEnvironment) {
    val subject = UseIfEmptyOrIfBlank()

    @Nested
    inner class `report UseIfEmptyOrIfBlank rule` {
        @Test
        @DisplayName("String.isBlank")
        fun stringIsBlank() {
            val code = """
                class Api(val name: String)
                
                fun test(api: Api) {
                    val name = if (api.name.isBlank()) "John" else api.name
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasStartSourceLocation(4, 29)
            assertThat(findings[0]).hasMessage("This 'isBlank' call can be replaced with 'ifBlank'")
        }

        @Test
        @DisplayName("String.isNotBlank")
        fun stringIsNotBlank() {
            val code = """
                class Api(val name: String)
                
                fun test(api: Api) {
                    val name = if (api.name.isNotBlank())
                        api.name
                    else
                        "John"
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasStartSourceLocation(4, 29)
            assertThat(findings[0]).hasMessage("This 'isNotBlank' call can be replaced with 'ifBlank'")
        }

        @Test
        @DisplayName("String.isEmpty")
        fun stringIsEmpty() {
            val code = """
                class Api(val name: String)
                
                fun test(api: Api) {
                    val name = if (api.name.isEmpty()) "John" else api.name
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasStartSourceLocation(4, 29)
            assertThat(findings[0]).hasMessage("This 'isEmpty' call can be replaced with 'ifEmpty'")
        }

        @Test
        @DisplayName("String.isNotEmpty")
        fun stringIsNotEmpty() {
            val code = """
                class Api(val name: String)
                
                fun test(api: Api) {
                    val name = if (api.name.isNotEmpty())
                        api.name
                    else
                        "John"
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasStartSourceLocation(4, 29)
            assertThat(findings[0]).hasMessage("This 'isNotEmpty' call can be replaced with 'ifEmpty'")
        }

        @Test
        @DisplayName("List.isEmpty")
        fun listIsEmpty() {
            val code = """
                fun test(list: List<Int>): List<Int> {
                    return if (list.isEmpty()) {
                        listOf(1)
                    } else {
                        list
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        @DisplayName("List.isNotEmpty")
        fun listIsNotEmpty() {
            val code = """
                fun test(list: List<Int>): List<Int> {
                    return if (list.isNotEmpty()) {
                        list
                    } else {
                        listOf(1)
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        @DisplayName("Set.isEmpty")
        fun setIsEmpty() {
            val code = """
                fun test(set: Set<Int>): Set<Int> {
                    return if (set.isEmpty()) {
                        setOf(1)
                    } else {
                        set
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        @DisplayName("Set.isNotEmpty")
        fun setIsNotEmpty() {
            val code = """
                fun test(set: Set<Int>): Set<Int> {
                    return if (set.isNotEmpty()) {
                        set
                    } else {
                        setOf(1)
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        @DisplayName("Map.isEmpty")
        fun mapIsEmpty() {
            val code = """
                fun test(map: Map<Int, Int>): Map<Int, Int> {
                    return if (map.isEmpty()) {
                        mapOf(1 to 2)
                    } else {
                        map
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        @DisplayName("Map.isNotEmpty")
        fun mapIsNotEmpty() {
            val code = """
                fun test(map: Map<Int, Int>): Map<Int, Int> {
                    return if (map.isNotEmpty()) {
                        map
                    } else {
                        mapOf(1 to 2)
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        @DisplayName("Collection.isEmpty")
        fun collectionIsEmpty() {
            val code = """
                fun test(collection: Collection<Int>): Collection<Int> {
                    return if (collection.isEmpty()) {
                        listOf(1)
                    } else {
                        collection
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        @DisplayName("Collection.isNotEmpty")
        fun collectionIsNotEmpty() {
            val code = """
                fun test(collection: Collection<Int>): Collection<Int> {
                    return if (collection.isNotEmpty()) {
                        collection
                    } else {
                        listOf(1)
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `implicit receiver`() {
            val code = """
                fun String.test(): String {
                    return if (isBlank()) {
                        "foo"
                    } else {
                        this
                    }
                }                
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `default value block is not single statement`() {
            val code = """
                fun test(list: List<Int>): List<Int> {
                    return if (list.isEmpty()) {
                        println()
                        listOf(1)
                    } else {
                        list
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `!isEmpty`() {
            val code = """
                fun test(list: List<Int>): List<Int> {
                    return if (!list.isEmpty()) { // list.isNotEmpty()
                        list
                    } else {
                        listOf(1)
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings[0]).hasMessage("This 'isEmpty' call can be replaced with 'ifEmpty'")
        }

        @Test
        fun `!isNotEmpty`() {
            val code = """
                fun test(list: List<Int>): List<Int> {
                    return if (!list.isNotEmpty()) { // list.isEmpty() 
                        listOf(1)
                    } else {
                        list
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings[0]).hasMessage("This 'isNotEmpty' call can be replaced with 'ifEmpty'")
        }
    }

    @Nested
    inner class `does not report UseIfEmptyOrIfBlank rule` {
        @Test
        @DisplayName("String.isNullOrBlank")
        fun stringIsNulLOrBlank() {
            val code = """
                class Api(val name: String?)
                
                fun test(api: Api) {
                    val name = if (api.name.isNullOrBlank()) "John" else api.name
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        @DisplayName("List.isNullOrEmpty")
        fun listIsNullOrEmpty() {
            val code = """
                fun test2(list: List<Int>): List<Int> {
                    return if (list.isNullOrEmpty()) {
                        listOf(1)
                    } else {
                        list
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        @DisplayName("Array.isEmpty")
        fun arrayIsEmpty() {
            val code = """
                fun test(arr: Array<String>): Array<String> {
                    return if (arr.isEmpty()) {
                        arrayOf("a")
                    } else {
                        arr
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        @DisplayName("Array.isNotEmpty")
        fun arrayIsNotEmpty() {
            val code = """
                fun test(arr: Array<String>): Array<String> {
                    return if (arr.isNotEmpty()) {
                        arr
                    } else {
                        arrayOf("a")
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        @DisplayName("IntArray.isEmpty")
        fun intArrayIsEmpty() {
            val code = """
                fun test(arr: IntArray): IntArray {
                    return if (arr.isEmpty()) {
                        intArrayOf(1)
                    } else {
                        arr
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        @DisplayName("IntArray.isNotEmpty")
        fun intArrayIsNotEmpty() {
            val code = """
                fun test(arr: IntArray): IntArray {
                    return if (arr.isNotEmpty()) {
                        arr
                    } else {
                        intArrayOf(1)
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `else if`() {
            val code = """
                fun test(list: List<Int>, b: Boolean): List<Int> {
                    return if (list.isEmpty()) {
                        listOf(1)
                    } else if (b) {
                        listOf(2)
                    } else {
                        list
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `no else`() {
            val code = """
                fun test(list: List<Int>) {
                    if (list.isEmpty()) {
                        listOf(1)
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `not self value`() {
            val code = """
                fun test(list: List<Int>): List<Int> {
                    return if (list.isEmpty()) {
                        listOf(1)
                    } else {
                        list + list
                    }
                }                
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `self value block is not single statement`() {
            val code = """
                fun test(list: List<Int>): List<Int> {
                    return if (list.isEmpty()) {
                        listOf(1)
                    } else {
                        println()
                        list
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `condition is binary expression`() {
            val code = """
                fun test(list: List<Int>): List<Int> {
                    return if (list.isEmpty() == true) {
                        listOf(1)
                    } else {
                        list
                    }
                }                
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }
}
