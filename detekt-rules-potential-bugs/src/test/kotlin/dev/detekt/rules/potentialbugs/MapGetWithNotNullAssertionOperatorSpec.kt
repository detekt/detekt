package dev.detekt.rules.potentialbugs

import dev.detekt.api.Config
import dev.detekt.test.junit.KotlinCoreEnvironmentTest
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class MapGetWithNotNullAssertionOperatorSpec(private val env: KotlinEnvironmentContainer) {
    private val subject = MapGetWithNotNullAssertionOperator(Config.empty)

    @Test
    fun `reports map get operator function with not null assertion when assigned`() {
        val code = """
            fun f() {
                val map = emptyMap<Any, Any>()
                val value = map["key"]!!
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports map_get() with not null assertion`() {
        val code = """
            fun f() {
                val map = emptyMap<Any, Any>()
                val value = map.get("key")!!
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report map get operator function call without not-null assert`() {
        val code = """
            fun f() {
                val map = emptyMap<String, String>()
                map["key"]
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report map_getValue() call`() {
        val code = """
            fun f() {
                val map = emptyMap<String, String>()
                map.getValue("key")
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report map_getOrDefault() call`() {
        val code = """
            fun f() {
                val map = emptyMap<String, String>()
                map.getOrDefault("key", "")
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report map_getOrElse() call`() {
        val code = """
            fun f() {
                val map = emptyMap<String, String>()
                map.getOrElse("key", { "" })
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports mutablemap_get() with not null assertion`() {
        val code = """
            fun f() {
                val map = mutableMapOf<String, String>()
                map.get("key")!!
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports mutablemap get operator function with not null assertion`() {
        val code = """
            fun f() {
                val map = mutableMapOf<String, String>()
                map["key"]!!
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports map_get() with not null assertion given a map implementation`() {
        val code = """
            fun f() {
                val map = LinkedHashMap<String, String>()
                map.get("key")!!
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports map get operator function with not null assertion given a map implementation`() {
        val code = """
            fun f() {
                val map = LinkedHashMap<String, String>()
                map["key"]!!
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports map_get()) call on constructor invocation`() {
        val code = """
            fun f() {
                LinkedHashMap<String, String>().get("key")!!
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports map get operator function call on constructor invocation`() {
        val code = """
            fun f() {
                LinkedHashMap<String, String>()["key"]!!
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports map_get()) call on function invocation`() {
        val code = """
            fun g(): Map<String, String> {
                return mapOf()
            }
            fun f() {
                g().get("key")!!
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports map get operator function call on function invocation`() {
        val code = """
            fun g(): Map<String, String> {
                return mapOf()
            }
            fun f() {
                g()["key"]!!
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report get function calls with the same signature on non map types`() {
        val code = """
            class Container<K, V> {
                fun get(key: K): V? = null
            }
            fun f() {
                val map = Container<String, String>()
                map.get("key")!!
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report get operator function calls with the same signature on non map types`() {
        val code = """
            class Container<K, V> {
                operator fun get(key: K): V? = null
            }
            fun f() {
                val map = Container<String, String>()
                map["key"]!!
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report get function calls with the different signature on map`() {
        val code = """
            fun Map<*,*>.get(index: Int, offset: Int): Int? = null
            fun f() {
                val map = mapOf<String, String>()
                map.get(0, 0)!!
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report get operator function calls with the different signature on map`() {
        val code = """
            operator fun Map<*,*>.get(one: Int): Int? = null
            fun f() {
                val map = mapOf<String, String>()
                map[0]!!
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }
}
