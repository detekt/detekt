package io.gitlab.arturbosch.detekt.rules.style

import io.github.detekt.test.utils.createEnvironment
import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import io.gitlab.arturbosch.detekt.test.lintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class ExplicitCollectionElementAccessMethodSpec(val env: KotlinCoreEnvironment) {
    val subject = ExplicitCollectionElementAccessMethod(Config.empty)

    @Nested
    inner class `Kotlin map` {

        @Test
        fun `reports map element access with get method`() {
            val code = """
                    fun f() {
                        val map = mapOf<String, String>() 
                        val value = map.get("key") 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `does not report safe map element access`() {
            val code = """
                    fun f() {
                        val map = mapOf<String, String>() 
                        val value = map?.get("key") 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `reports map set method usage with unused return value`() {
            val code = """
                    fun f() {
                        val map = mutableMapOf<String, String>() 
                        map.set("key", "value") 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `reports map put method usage with unused return value`() {
            val code = """
                    fun f() {
                        val map = mutableMapOf<String, String>()
                        map.put("key", "val") 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `does not report map put method usage with variable assignment`() {
            val code = """
                    fun f() {
                        val map = mutableMapOf<String, String>() 
                        val oldValue = map.put("key", "val") 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report map put method with used return value`() {
            val code = """
                    fun f(): Boolean {
                        val map = mutableMapOf<String, String>()
                        return map.put("key", "val") == null
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `reports map element access with get method of non-abstract map`() {
            val code = """
                    fun f() {
                        val map = hashMapOf<String, String>() 
                        val value = map.get("key") 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `reports map element insert with put method of non-abstract map`() {
            val code = """
                    fun f() {
                        val map = hashMapOf<String, String>() 
                        map.put("key", "value") 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        @Test
        @DisplayName("does not report map access with []")
        fun noReportMapAccessWithBrackets() {
            val code = """
                    fun f() {
                        val map = mapOf<String, String>()
                        val value = map["key"] 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        @DisplayName("does not report map insert with []")
        fun noReportMapInsertWithBrackets() {
            val code = """
                    fun f() {
                        val map = mutableMapOf<String, String>()
                        map["key"] = "value" 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `reports map element access with get method from map in a chain`() {
            val code = """
                    fun f() {
                        val map = mapOf<String, String>()
                        val value = listOf("1", "2").associateBy { it }.get("1")
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `reports map element access with get method from non-abstract map`() {
            val code = """
                    fun f() {
                        val map = linkedMapOf<String, String>()
                        val value = map.get("key") 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `does not report calls on implicit receiver`() {
            val code = """
                fun f() {
                    val map = mapOf<String, Int>()
                    with(map) { get("a") }
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(0)
        }
    }

    @Nested
    inner class `Kotlin list` {
        @Test
        fun `reports list element access with get method`() {
            val code = """
                    fun f() {
                        val list = listOf<String>() 
                        val value = list.get(0) 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `reports mutable list element access with get method`() {
            val code = """
                    fun f() {
                        val list = mutableListOf<String>()
                        val value = list.get(0) 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        @Test
        @DisplayName("does not report element access with []")
        fun noReportElementAccessWithBrackets() {
            val code = """
                    fun f() {
                        val list = listOf<String>() 
                        val value = list[0] 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `reports element access with get method of non-abstract list`() {
            val code = """
                    fun f() {
                        val list = arrayListOf<String>() 
                        val value = list.get(0) 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `does not report calls on implicit receiver`() {
            val code = """
                fun f() {
                    val list = listOf<String>()
                    val value = with(list) { get(0) }
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(0)
        }
    }

    @Nested
    inner class `Java map` {

        @Test
        fun `reports map element access with get method`() {
            val code = """
                    fun f() {
                        val map = java.util.HashMap<String, String>() 
                        val value = map.get("key") 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `reports map set method usage with unused return value`() {
            val code = """
                    fun f() {
                        val map = java.util.HashMap<String, String>() 
                        map.set("key", "val") 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `reports map put method usage with unused return value`() {
            val code = """
                    fun f() {
                        val map = java.util.HashMap<String, String>() 
                        map.put("key", "val") 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        @Test
        @DisplayName("does not report map access with []")
        fun noReportMapAccessWithBrackets() {
            val code = """
                    fun f() {
                        val map = java.util.HashMap<String, String>() 
                        val value = map["key"] 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        @DisplayName("does not report map insert with []")
        fun noReportMapInsertWithBrackets() {
            val code = """
                    fun f() {
                        val map = java.util.HashMap<String, String>() 
                        map["key"] = "value" 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `reports map element access with get method from map in a chain`() {
            val code = """
                    fun f() {
                        val map = java.util.HashMap<String, String>() 
                        val value = listOf("1", "2").associateBy { it }.get("1") 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }
    }

    @Nested
    inner class `custom operators` {

        @Test
        fun `reports custom get operator`() {
            val code = """
                    class Custom { operator fun get(i: Int) = 42 }
                    fun f() {
                        val custom = Custom()
                        val value = custom.get(0)
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `does not report non-operator get method`() {
            val code = """
                    class Custom { fun get(i: Int) = 42 }
                    fun f() {
                        val custom = Custom()
                        val value = custom.get(0)
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `reports custom set operator with unused return value`() {
            val code = """
                    class Custom { operator fun set(key: String, value: String) {} }
                    fun f() {
                        val custom = Custom()
                        custom.set("key", "value")
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `does not report non-operator set method`() {
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

    @Nested
    inner class `Java list` {

        @Test
        fun `reports list element access with get method`() {
            val code = """
                    fun f() {
                        val list = java.util.ArrayList<String>() 
                        val value = list.get(0) 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        @Test
        @DisplayName("does not report element access with []")
        fun noReportElementAccessWithBrackets() {
            val code = """
                    fun f() {
                        val list = java.util.ArrayList<String>() 
                        val value = list[0] 
                    }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }
    }

    @Nested
    inner class `edge cases` {

        @Test
        fun `does not crash for getter`() {
            val code = """
                class A {
                    val i: Int get() = 1 + 2
                    val c: Char? get() = "".first() ?: throw IllegalArgumentException("getter")
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not crash for fluent api`() {
            val code = """
                val string = ""
                    .toString()
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report for unresolvable code`() {
            val code = """
                 fun f() {
                    val unknownType = UnknownType()
                    val value = unknownType.put("answer", 42)
                 }
            """
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report for put functions without caller`() {
            val code = """
                fun put() { }
                fun f() {
                    put()
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Nested
        inner class JavaSourceTests {

            private val environmentWrapper =
                createEnvironment(additionalJavaSourceRootPaths = listOf(resourceAsPath("java").toFile()))
            private val customEnv = environmentWrapper.env

            @AfterAll
            fun disposeEnvironment() {
                environmentWrapper.dispose()
            }

            @Test
            fun `reports setter from java with 2 or less parameters`() {
                // this test case ensures that the test environment are set up correctly.
                val code = """
                    import com.example.fromjava.Rect
    
                    fun foo() {
                        val rect = Rect()
                        rect.set(0, 1)
                    }
                """
                assertThat(subject.lintWithContext(customEnv, code)).hasSize(1)
            }

            @Test
            fun `does not report if the function has 3 or more arguments and it's defined in java - #4288`() {
                val code = """
                    import com.example.fromjava.Rect
    
                    fun foo() {
                        val rect = Rect()
                        rect.set(0, 1, 2)
                    }
                """
                assertThat(subject.lintWithContext(customEnv, code)).isEmpty()
            }
        }
    }
}
