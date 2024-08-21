package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import io.gitlab.arturbosch.detekt.test.lintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ExplicitCollectionElementAccessMethodSpec {
    val subject = ExplicitCollectionElementAccessMethod(Config.empty)

    @Nested
    @KotlinCoreEnvironmentTest
    inner class WithDefaultSources(val env: KotlinCoreEnvironment) {
        @Nested
        inner class `Kotlin map` {

            @Test
            fun `reports map element access with get method`() {
                val code = """
                    fun f() {
                        val map = mapOf<String, String>()
                        val value = map.get("key")
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
            }

            @Test
            fun `does not report safe map element access`() {
                val code = """
                    fun f() {
                        val map = mapOf<String, String>()
                        val value = map?.get("key")
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `reports map set method usage with unused return value`() {
                val code = """
                    fun f() {
                        val map = mutableMapOf<String, String>()
                        map.set("key", "value")
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
            }

            @Test
            fun `reports map put method usage with unused return value`() {
                val code = """
                    fun f() {
                        val map = mutableMapOf<String, String>()
                        map.put("key", "val")
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
            }

            @Test
            fun `does not report map put method usage with variable assignment`() {
                val code = """
                    fun f() {
                        val map = mutableMapOf<String, String>()
                        val oldValue = map.put("key", "val")
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `does not report map put method usage with three arguments`() {
                val code = """
                    class MyMap : java.util.HashMap<String, Int>() {
                        fun put(key: String, key2: String, value: Int): Int {
                            return value
                        }
                    }
                    fun main() {
                        val map = MyMap()
                        map.put("a", "b", 1)
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `does not report map put method with used return value`() {
                val code = """
                    fun f(): Boolean {
                        val map = mutableMapOf<String, String>()
                        return map.put("key", "val") == null
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `reports map element access with get method of non-abstract map`() {
                val code = """
                    fun f() {
                        val map = hashMapOf<String, String>()
                        val value = map.get("key")
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
            }

            @Test
            fun `reports map element insert with put method of non-abstract map`() {
                val code = """
                    fun f() {
                        val map = hashMapOf<String, String>()
                        map.put("key", "value")
                    }
                """.trimIndent()
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
                """.trimIndent()
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
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `reports map element access with get method from map in a chain`() {
                val code = """
                    fun f() {
                        val map = mapOf<String, String>()
                        val value = listOf("1", "2").associateBy { it }.get("1")
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
            }

            @Test
            fun `reports map element access with get method from non-abstract map`() {
                val code = """
                    fun f() {
                        val map = linkedMapOf<String, String>()
                        val value = map.get("key")
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
            }

            @Test
            fun `does not report calls on implicit receiver`() {
                val code = """
                    fun f() {
                        val map = mapOf<String, Int>()
                        with(map) { get("a") }
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
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
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
            }

            @Test
            fun `reports mutable list element access with get method`() {
                val code = """
                    fun f() {
                        val list = mutableListOf<String>()
                        val value = list.get(0)
                    }
                """.trimIndent()
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
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `reports element access with get method of non-abstract list`() {
                val code = """
                    fun f() {
                        val list = arrayListOf<String>()
                        val value = list.get(0)
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
            }

            @Test
            fun `does not report calls on implicit receiver`() {
                val code = """
                    fun f() {
                        val list = listOf<String>()
                        val value = with(list) { get(0) }
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
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
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
            }

            @Test
            fun `reports map set method usage with unused return value`() {
                val code = """
                    fun f() {
                        val map = java.util.HashMap<String, String>()
                        map.set("key", "val")
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
            }

            @Test
            fun `reports map put method usage with unused return value`() {
                val code = """
                    fun f() {
                        val map = java.util.HashMap<String, String>()
                        map.put("key", "val")
                    }
                """.trimIndent()
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
                """.trimIndent()
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
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `reports map element access with get method from map in a chain`() {
                val code = """
                    fun f() {
                        val map = java.util.HashMap<String, String>()
                        val value = listOf("1", "2").associateBy { it }.get("1")
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
            }
        }

        @Nested
        inner class `Java non-collection types` {
            @Test
            fun `does not report ByteBuffer get`() {
                val code = """
                    fun f() {
                        val buffer = java.nio.ByteBuffer()
                        buffer.get(byteArrayOf(0x42))
                    }
                """.trimIndent()
                assertThat(subject.lintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `does not report Field get`() {
                val code = """
                    fun f(field: java.lang.reflect.Field) {
                        val value = field.get(null) // access static field
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
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
                """.trimIndent()
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
                """.trimIndent()
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
                """.trimIndent()
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
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `does not report custom get operator with type parameters`() {
                val code = """
                    class C {
                        operator fun <T> get(key: String): List<T>? = null
                    }
                    fun test(c: C) {
                        c.get<Int>("key")
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            @Nested
            inner class `with vararg parameter` {
                @Test
                fun `does not report with spread operator to spread the vararg`() {
                    val code = """
                        class C {
                            operator fun get(key: String, vararg objects: Int): String = ""
                        }
                        fun test(c: C) {
                            val objects = listOf(0, 1).toIntArray()
                            c.get("key", *objects)
                        }
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
                }

                @Test
                fun `does not report with spread operator to spread the vararg and normal parameter`() {
                    val code = """
                        class C {
                            operator fun get(key: String, vararg objects: Int): String = ""
                        }
                        fun test(c: C) {
                            val objects = listOf(0, 1).toIntArray()
                            c.get("key", 1, *objects)
                        }
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
                }

                @Test
                fun `does not report with spread operator to spread the vararg as key`() {
                    val code = """
                        class C {
                            operator fun get(vararg objects: Int): String = objects.toString()
                        }
                        fun test(c: C) {
                            val objects = listOf(0, 1).toIntArray()
                            c.get(*objects)
                        }
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
                }

                @Test
                fun `does not report with normal parameter and spread operator to spread the vararg`() {
                    val code = """
                        class C {
                            operator fun get(key: String, vararg objects: Int): String = ""
                        }
                        fun test(c: C) {
                            val objects = listOf(0, 1).toIntArray()
                            c.get("key", *objects, 1)
                        }
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
                }

                @Test
                fun `does report with no value is passed for vararg parameter`() {
                    val code = """
                        class C {
                            operator fun get(key: String, vararg objects: Int): String = ""
                        }
                        fun test(c: C) {
                            c.get("key")
                        }
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
                }

                @Test
                fun `does report with 1 value is passed for vararg parameter`() {
                    val code = """
                        class C {
                            operator fun get(key: String, vararg objects: Int): String = ""
                        }
                        fun test(c: C) {
                            c.get("key", 1)
                        }
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
                }

                @Test
                fun `does report with 1 value is passed for vararg parameter as key`() {
                    val code = """
                        class C {
                            operator fun get(vararg objects: Int): String = objects.toString()
                        }
                        fun test(c: C) {
                            c.get(0)
                        }
                    """.trimIndent()
                    assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
                }
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
                """.trimIndent()
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
                """.trimIndent()
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
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `does not crash for fluent api`() {
                val code = """
                    val string = ""
                        .toString()
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `does not report for unresolvable code`() {
                val code = """
                    fun f() {
                       val unknownType = UnknownType()
                       val value = unknownType.put("answer", 42)
                    }
                """.trimIndent()
                assertThat(subject.lintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `does not report for put functions without caller`() {
                val code = """
                    fun put() { }
                    fun f() {
                        put()
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }
        }
    }

    @Nested
    @KotlinCoreEnvironmentTest(additionalJavaSourcePaths = ["java"])
    inner class WithAdditionalJavaSources(val env: KotlinCoreEnvironment) {
        @Test
        fun `does not report setters defined in java which are unlikely to be collection accessors`() {
            val code = """
                import com.example.fromjava.Rect
                
                fun foo() {
                    val rect = Rect()
                    rect.set(0, 1)
                    rect.set(0, 1, 2)
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }
}
