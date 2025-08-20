package dev.detekt.api

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Test
import kotlin.random.Random

class PropertiesAwareSpec {

    private val hash = Random(1).nextInt()
    private val store = object : PropertiesAware {
        override val properties: MutableMap<String, Any> = HashMap()
        override fun register(key: String, value: Any) {
            properties[key] = value
        }
    }.apply {
        register("bool", true)
        register("string", "test")
        register("number", 5)
        register("set", setOf(1, 2, 3))
        register(
            "any",
            object : Any() {
                override fun equals(other: Any?): Boolean = hashCode() == other.hashCode()
                override fun hashCode(): Int = hash
            }
        )
    }

    @Test
    fun `can retrieve the actual typed values`() {
        assertThat(store.getOrNull<Boolean>("bool")).isEqualTo(true)
        assertThat(store.getOrNull<String>("string")).isEqualTo("test")
        assertThat(store.getOrNull<Int>("number")).isEqualTo(5)
        assertThat(store.getOrNull<Set<Int>>("set")).isEqualTo(setOf(1, 2, 3))
        assertThat(store.getOrNull<Any>("any").hashCode()).isEqualTo(hash)
    }

    @Test
    fun `returns null on absent values`() {
        assertThat(store.getOrNull<Boolean>("absent")).isNull()
    }

    @Test
    fun `throws an error on wrong type`() {
        assertThatCode { store.getOrNull<Double>("bool") }
            .isInstanceOf(IllegalStateException::class.java)
    }
}
