package io.gitlab.arturbosch.detekt.rules.style

import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import io.gitlab.arturbosch.detekt.test.lintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ExplicitCollectionElementAccessMethodSpec : Spek({
    setupKotlinEnvironment(additionalJavaSourceRootPath = resourceAsPath("java"))

    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { ExplicitCollectionElementAccessMethod(Config.empty) }

    describe("Kotlin map") {

        it("reports map element access with get method") {
            val code = """
                    fun f() {
                        val map = mapOf<String, String>() 
                        val value = map.get("key") 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("does not report safe map element access") {
            val code = """
                    fun f() {
                        val map = mapOf<String, String>() 
                        val value = map?.get("key") 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("reports map set method usage with unused return value") {
            val code = """
                    fun f() {
                        val map = mutableMapOf<String, String>() 
                        map.set("key", "value") 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("reports map put method usage with unused return value") {
            val code = """
                    fun f() {
                        val map = mutableMapOf<String, String>()
                        map.put("key", "val") 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("does not report map put method usage with variable assignment") {
            val code = """
                    fun f() {
                        val map = mutableMapOf<String, String>() 
                        val oldValue = map.put("key", "val") 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("does not report map put method with used return value") {
            val code = """
                    fun f(): Boolean {
                        val map = mutableMapOf<String, String>()
                        return map.put("key", "val") == null
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("reports map element access with get method of non-abstract map") {
            val code = """
                    fun f() {
                        val map = hashMapOf<String, String>() 
                        val value = map.get("key") 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("reports map element insert with put method of non-abstract map") {
            val code = """
                    fun f() {
                        val map = hashMapOf<String, String>() 
                        map.put("key", "value") 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("does not report map access with []") {
            val code = """
                    fun f() {
                        val map = mapOf<String, String>()
                        val value = map["key"] 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("does not report map insert with []") {
            val code = """
                    fun f() {
                        val map = mutableMapOf<String, String>()
                        map["key"] = "value" 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("reports map element access with get method from map in a chain") {
            val code = """
                    fun f() {
                        val map = mapOf<String, String>()
                        val value = listOf("1", "2").associateBy { it }.get("1")
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("reports map element access with get method from non-abstract map") {
            val code = """
                    fun f() {
                        val map = linkedMapOf<String, String>()
                        val value = map.get("key") 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("does not report calls on implicit receiver") {
            val code = """
                fun f() {
                    val map = mapOf<String, Int>()
                    with(map) { get("a") }
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(0)
        }
    }

    describe("Kotlin list") {
        it("reports list element access with get method") {
            val code = """
                    fun f() {
                        val list = listOf<String>() 
                        val value = list.get(0) 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("reports mutable list element access with get method") {
            val code = """
                    fun f() {
                        val list = mutableListOf<String>()
                        val value = list.get(0) 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("does not report element access with []") {
            val code = """
                    fun f() {
                        val list = listOf<String>() 
                        val value = list[0] 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("reports element access with get method of non-abstract list") {
            val code = """
                    fun f() {
                        val list = arrayListOf<String>() 
                        val value = list.get(0) 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("does not report calls on implicit receiver") {
            val code = """
                fun f() {
                    val list = listOf<String>()
                    val value = with(list) { get(0) }
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(0)
        }
    }

    describe("Java map") {

        it("reports map element access with get method") {
            val code = """
                    fun f() {
                        val map = java.util.HashMap<String, String>() 
                        val value = map.get("key") 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("reports map set method usage with unused return value") {
            val code = """
                    fun f() {
                        val map = java.util.HashMap<String, String>() 
                        map.set("key", "val") 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("reports map put method usage with unused return value") {
            val code = """
                    fun f() {
                        val map = java.util.HashMap<String, String>() 
                        map.put("key", "val") 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("does not report map access with []") {
            val code = """
                    fun f() {
                        val map = java.util.HashMap<String, String>() 
                        val value = map["key"] 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("does not report map insert with []") {
            val code = """
                    fun f() {
                        val map = java.util.HashMap<String, String>() 
                        map["key"] = "value" 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("reports map element access with get method from map in a chain") {
            val code = """
                    fun f() {
                        val map = java.util.HashMap<String, String>() 
                        val value = listOf("1", "2").associateBy { it }.get("1") 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }
    }

    describe("custom operators") {

        it("reports custom get operator") {
            val code = """
                    class Custom { operator fun get(i: Int) = 42 }
                    fun f() {
                        val custom = Custom()
                        val value = custom.get(0)
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("does not report non-operator get method") {
            val code = """
                    class Custom { fun get(i: Int) = 42 }
                    fun f() {
                        val custom = Custom()
                        val value = custom.get(0)
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("reports custom set operator with unused return value") {
            val code = """
                    class Custom { operator fun set(key: String, value: String) {} }
                    fun f() {
                        val custom = Custom()
                        custom.set("key", "value")
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("does not report non-operator set method") {
            val code = """
                    class Custom { fun set(key: String, value: String) {} }
                    fun f() {
                        val custom = Custom()
                        custom.set("key", "value")
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }
    }

    describe("Java list") {

        it("reports list element access with get method") {
            val code = """
                    fun f() {
                        val list = java.util.ArrayList<String>() 
                        val value = list.get(0) 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("does not report element access with []") {
            val code = """
                    fun f() {
                        val list = java.util.ArrayList<String>() 
                        val value = list[0] 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }
    }

    describe("edge cases") {

        it("does not crash for getter") {
            val code = """
                class A {
                    val i: Int get() = 1 + 2
                    val c: Char? get() = "".first() ?: throw IllegalArgumentException("getter")
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("does not crash for fluent api") {
            val code = """
                val string = ""
                    .toString()
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("does not report for unresolvable code") {
            val code = """
                 fun f() {
                    val unknownType = UnknownType()
                    val value = unknownType.put("answer", 42)
                 }
            """
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        it("does not report for put functions without caller") {
            val code = """
                fun put() { }
                fun f() {
                    put()
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("does not report if the function has 3 or more arguments and it's defined in java - #4288") {
            val code = """
                import com.example.fromjava.Rect

                fun foo() {
                    val rect = Rect()
                    rect.set(0, 1, 2)
                }
            """
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }
})
