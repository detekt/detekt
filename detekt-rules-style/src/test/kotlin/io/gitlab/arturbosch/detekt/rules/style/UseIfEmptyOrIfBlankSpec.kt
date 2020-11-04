package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UseIfEmptyOrIfBlankSpec : Spek({
    setupKotlinEnvironment()
    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { UseIfEmptyOrIfBlank() }

    describe("report UseIfEmptyOrIfBlank rule") {
        it("String.isBlank") {
            val code = """
                class Api(val name: String)
                
                fun test(api: Api) {
                    val name = if (api.name.isBlank()) "John" else api.name
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasSourceLocation(4, 29)
            assertThat(findings[0]).hasMessage("This 'isBlank' call can be replaced with 'ifBlank'")
        }

        it("String.isNotBlank") {
            val code = """
                class Api(val name: String)
                
                fun test(api: Api) {
                    val name = if (api.name.isNotBlank())
                        api.name
                    else
                        "John"
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasSourceLocation(4, 29)
            assertThat(findings[0]).hasMessage("This 'isNotBlank' call can be replaced with 'ifBlank'")
        }

        it("String.isEmpty") {
            val code = """
                class Api(val name: String)
                
                fun test(api: Api) {
                    val name = if (api.name.isEmpty()) "John" else api.name
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasSourceLocation(4, 29)
            assertThat(findings[0]).hasMessage("This 'isEmpty' call can be replaced with 'ifEmpty'")
        }

        it("String.isNotEmpty") {
            val code = """
                class Api(val name: String)
                
                fun test(api: Api) {
                    val name = if (api.name.isNotEmpty())
                        api.name
                    else
                        "John"
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasSourceLocation(4, 29)
            assertThat(findings[0]).hasMessage("This 'isNotEmpty' call can be replaced with 'ifEmpty'")
        }

        it("List.isEmpty") {
            val code = """
                fun test(list: List<Int>): List<Int> {
                    return if (list.isEmpty()) {
                        listOf(1)
                    } else {
                        list
                    }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        it("List.isNotEmpty") {
            val code = """
                fun test(list: List<Int>): List<Int> {
                    return if (list.isNotEmpty()) {
                        list
                    } else {
                        listOf(1)
                    }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        it("Set.isEmpty") {
            val code = """
                fun test(set: Set<Int>): Set<Int> {
                    return if (set.isEmpty()) {
                        setOf(1)
                    } else {
                        set
                    }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        it("Set.isNotEmpty") {
            val code = """
                fun test(set: Set<Int>): Set<Int> {
                    return if (set.isNotEmpty()) {
                        set
                    } else {
                        setOf(1)
                    }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        it("Map.isEmpty") {
            val code = """
                fun test(map: Map<Int, Int>): Map<Int, Int> {
                    return if (map.isEmpty()) {
                        mapOf(1 to 2)
                    } else {
                        map
                    }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        it("Map.isNotEmpty") {
            val code = """
                fun test(map: Map<Int, Int>): Map<Int, Int> {
                    return if (map.isNotEmpty()) {
                        map
                    } else {
                        mapOf(1 to 2)
                    }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        it("Collection.isEmpty") {
            val code = """
                fun test(collection: Collection<Int>): Collection<Int> {
                    return if (collection.isEmpty()) {
                        listOf(1)
                    } else {
                        collection
                    }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        it("Collection.isNotEmpty") {
            val code = """
                fun test(collection: Collection<Int>): Collection<Int> {
                    return if (collection.isNotEmpty()) {
                        collection
                    } else {
                        listOf(1)
                    }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        it("implicit receiver") {
            val code = """
                fun String.test(): String {
                    return if (isBlank()) {
                        "foo"
                    } else {
                        this
                    }
                }                
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        it("default value block is not single statement") {
            val code = """
                fun test(list: List<Int>): List<Int> {
                    return if (list.isEmpty()) {
                        println()
                        listOf(1)
                    } else {
                        list
                    }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        it("!isEmpty") {
            val code = """
                fun test(list: List<Int>): List<Int> {
                    return if (!list.isEmpty()) { // list.isNotEmpty()
                        list
                    } else {
                        listOf(1)
                    }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings[0]).hasMessage("This 'isEmpty' call can be replaced with 'ifEmpty'")
        }

        it("!isNotEmpty") {
            val code = """
                fun test(list: List<Int>): List<Int> {
                    return if (!list.isNotEmpty()) { // list.isEmpty() 
                        listOf(1)
                    } else {
                        list
                    }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings[0]).hasMessage("This 'isNotEmpty' call can be replaced with 'ifEmpty'")
        }
    }

    describe("does not report UseIfEmptyOrIfBlank rule") {
        it("String.isNullOrBlank") {
            val code = """
                class Api(val name: String?)
                
                fun test(api: Api) {
                    val name = if (api.name.isNullOrBlank()) "John" else api.name
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        it("List.isNullOrEmpty") {
            val code = """
                fun test2(list: List<Int>): List<Int> {
                    return if (list.isNullOrEmpty()) {
                        listOf(1)
                    } else {
                        list
                    }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        it("Array.isEmpty") {
            val code = """
                fun test(arr: Array<String>): Array<String> {
                    return if (arr.isEmpty()) {
                        arrayOf("a")
                    } else {
                        arr
                    }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        it("Array.isNotEmpty") {
            val code = """
                fun test(arr: Array<String>): Array<String> {
                    return if (arr.isNotEmpty()) {
                        arr
                    } else {
                        arrayOf("a")
                    }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        it("IntArray.isEmpty") {
            val code = """
                fun test(arr: IntArray): IntArray {
                    return if (arr.isEmpty()) {
                        intArrayOf(1)
                    } else {
                        arr
                    }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        it("IntArray.isNotEmpty") {
            val code = """
                fun test(arr: IntArray): IntArray {
                    return if (arr.isNotEmpty()) {
                        arr
                    } else {
                        intArrayOf(1)
                    }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        it("else if") {
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
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        it("no else") {
            val code = """
                fun test(list: List<Int>) {
                    if (list.isEmpty()) {
                        listOf(1)
                    }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        it("not self value") {
            val code = """
                fun test(list: List<Int>): List<Int> {
                    return if (list.isEmpty()) {
                        listOf(1)
                    } else {
                        list + list
                    }
                }                
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        it("self value block is not single statement") {
            val code = """
                fun test(list: List<Int>): List<Int> {
                    return if (list.isEmpty()) {
                        listOf(1)
                    } else {
                        println()
                        list
                    }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        it("condition is binary expression") {
            val code = """
                fun test(list: List<Int>): List<Int> {
                    return if (list.isEmpty() == true) {
                        listOf(1)
                    } else {
                        list
                    }
                }                
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }
})
