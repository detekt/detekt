package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ExplicitCollectionElementAccessMethodSpec : Spek({
    setupKotlinEnvironment()

    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { ExplicitCollectionElementAccessMethod(Config.empty) }

    describe("Kotlin map") {

        it("reports map element access with get method") {
            val code = """
                    fun f() {
                        val map = mapOf<String, String>() 
                        val value = map.get("key") 
                    }"""
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("does not report safe map element access") {
            val code = """
                    fun f() {
                        val map = mapOf<String, String>() 
                        val value = map?.get("key") 
                    }"""
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("reports map put method usage") {
            val code = """
                    fun f() {
                        val map = mutableMapOf<String, String>()
                        map.put("key", "val") 
                    }"""
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("reports map element access with get method of non-abstract map") {
            val code = """
                    fun f() {
                        val map = hashMapOf<String, String>() 
                        val value = map.get("key") 
                    }"""
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("reports map element insert with put method of non-abstract map") {
            val code = """
                    fun f() {
                        val map = hashMapOf<String, String>() 
                        val value = map.put("key", "value") 
                    }"""
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("does not report map access with []") {
            val code = """
                    fun f() {
                        val map = mapOf<String, String>()
                        val value = map["key"] 
                    }"""
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("does not report map insert with []") {
            val code = """
                    fun f() {
                        val map = mutableMapOf<String, String>()
                        map["key"] = "value" 
                    }"""
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("reports map element access with get method from map in a chain") {
            val code = """
                    fun f() {
                        val map = mapOf<String, String>()
                        val value = listOf("1", "2").associateBy { it }.get("1")
                    }"""
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("reports map element access with get method from non-abstract map") {
            val code = """
                    fun f() {
                        val map = linkedMapOf<String, String>()
                        val value = map.get("key") 
                    }"""
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }
    }

    describe("Kotlin list") {
        it("reports list element access with get method") {
            val code = """
                    fun f() {
                        val list = listOf<String>() 
                        val value = list.get(0) 
                    }"""
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("reports mutable list element access with get method") {
            val code = """
                    fun f() {
                        val list = mutableListOf<String>()
                        val value = list.get(0) 
                    }"""
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("does not report element access with []") {
            val code = """
                    fun f() {
                        val list = listOf<String>() 
                        val value = list[0] 
                    }"""
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("reports element access with get method of non-abstract list") {
            val code = """
                    fun f() {
                        val list = arrayListOf<String>() 
                        val value = list.get(0) 
                    }"""
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }
    }

    describe("Java map") {

        it("reports map element access with get method") {
            val code = """
                    fun f() {
                        val map = java.util.HashMap<String, String>() 
                        val value = map.get("key") 
                    }"""
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("reports map put method usage") {
            val code = """
                    fun f() {
                        val map = java.util.HashMap<String, String>() 
                        map.put("key", "val") 
                    }"""
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("does not report map access with []") {
            val code = """
                    fun f() {
                        val map = java.util.HashMap<String, String>() 
                        val value = map["key"] 
                    }"""
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("does not report map insert with []") {
            val code = """
                    fun f() {
                        val map = java.util.HashMap<String, String>() 
                        map["key"] = "value" 
                    }"""
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("reports map element access with get method from map in a chain") {
            val code = """
                    fun f() {
                        val map = java.util.HashMap<String, String>() 
                        val value = listOf("1", "2").associateBy { it }.get("1") 
                    }"""
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }
    }

    describe("Java list") {

        it("reports list element access with get method") {
            val code = """
                    fun f() {
                        val list = java.util.ArrayList<String>() 
                        val value = list.get(0) 
                    }"""
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("does not report element access with []") {
            val code = """
                    fun f() {
                        val list = java.util.ArrayList<String>() 
                        val value = list[0] 
                    }"""
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }
    }

    describe("getters") {

        it("does not crash for getter") {
            val code = """
                class A {
                    val i: Int get() = 1 + 2
                    val c: Char? get() = "".first() ?: throw IllegalArgumentException("getter")
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }
    }

    describe("fluent api doesn't crash") {

        it("does not crash for fluent api") {
            val code = """
                val string = ""
                    .toString()
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }
    }
})
