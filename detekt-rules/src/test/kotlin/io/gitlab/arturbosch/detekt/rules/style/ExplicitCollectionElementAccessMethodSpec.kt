package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.KtTestCompiler
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ExplicitCollectionElementAccessMethodSpec : Spek({

    val subject by memoized { ExplicitCollectionElementAccessMethod(Config.empty) }

    val wrapper by memoized(
        factory = { KtTestCompiler.createEnvironment() },
        destructor = { it.dispose() }
    )

    describe("Kotlin map") {

        it("reports map element access with get method") {
            val code = """
                    fun f() {
                        val map = mapOf<String, String>() 
                        val value = map.get("key") 
                    }"""
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).hasSize(1)
        }

        it("reports map put method usage") {
            val code = """
                    fun f() {
                        val map = mapOf<String, String>()
                        map.put("key", "val") 
                    }"""
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).hasSize(1)
        }

        it("reports map element access with get method of non-abstract map") {
            val code = """
                    fun f() {
                        val map = hashMapOf<String, String>() 
                        val value = map.get("key") 
                    }"""
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).hasSize(1)
        }

        it("reports map element insert with put method of non-abstract map") {
            val code = """
                    fun f() {
                        val map = hashMapOf<String, String>() 
                        val value = map.put("key", "value") 
                    }"""
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).hasSize(1)
        }

        it("does not report map access with []") {
            val code = """
                    fun f() {
                        val map = mapOf<String, String>()
                        map["key"] 
                    }"""
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).isEmpty()
        }

        it("does not report map insert with []") {
            val code = """
                    fun f() {
                        val map = mapOf<String, String>()
                        map["key"] = "value" 
                    }"""
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).isEmpty()
        }

        it("reports map element access with get method from map in a chain") {
            val code = """
                    fun f() {
                        val map = mapOf<String, String>()
                        val value = listOf("1", "2").associateBy { it }.get("1")
                    }"""
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).hasSize(1)
        }

        it("reports map element access with get method from non-abstract map") {
            val code = """
                    fun f() {
                        val map = linkedMapOf<String, String>()
                        val value = map.get("key") 
                    }"""
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).hasSize(1)
        }
    }

    describe("Kotlin list") {

        it("reports list element access with get method") {
            val code = """
                    fun f() {
                        val list = listOf<String>() 
                        val value = list.get(0) 
                    }"""
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).hasSize(1)
        }

        it("does not report element access with get method") {
            val code = """
                    fun f() {
                        val list = listOf<String>() 
                        val value = list[0] 
                    }"""
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).isEmpty()
        }

        it("reports element access with get method of non-abstract list") {
            val code = """
                    fun f() {
                        val list = arrayListOf<String, String>() 
                        val value = list.get("key") 
                    }"""
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).hasSize(1)
        }

    }

    describe("Java map") {

        it("reports map element access with get method") {
            val code = """
                    fun f() {
                        val map = java.util.HashMap<String, String>() 
                        val value = map.get("key") 
                    }"""
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).hasSize(1)
        }

        it("reports map put method usage") {
            val code = """
                    fun f() {
                        val map = java.util.HashMap<String, String>() 
                        map.put("key", "val") 
                    }"""
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).hasSize(1)
        }

        it("does not report map access with []") {
            val code = """
                    fun f() {
                        val map = java.util.HashMap<String, String>() 
                        map["key"] 
                    }"""
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).isEmpty()
        }

        it("does not report map insert with []") {
            val code = """
                    fun f() {
                        val map = java.util.HashMap<String, String>() 
                        map["key"] = "value" 
                    }"""
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).isEmpty()
        }

        it("reports map element access with get method from map in a chain") {
            val code = """
                    fun f() {
                        val map = java.util.HashMap<String, String>() 
                        val value = listOf("1", "2").associateBy { it }.get("1") 
                    }"""
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).hasSize(1)
        }
    }

    describe("Java list") {

        it("reports list element access with get method") {
            val code = """
                    fun f() {
                        val list = java.util.ArrayList<String>() 
                        val value = list.get(0) 
                    }"""
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).hasSize(1)
        }

        it("does not report element access with get method") {
            val code = """
                    fun f() {
                        val list = java.util.ArrayList<String>() 
                        val value = list[0] 
                    }"""
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).isEmpty()
        }
    }

})
