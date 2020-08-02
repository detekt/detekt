package io.gitlab.arturbosch.detekt.api

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.random.Random

@OptIn(UnstableApi::class)
class PropertiesAwareSpec : Spek({

    describe("PropertiesAware") {

        context("Implementations can store and retrieve properties") {

            val hash by memoized { Random(1).nextInt() }
            val store by memoized {
                object : PropertiesAware {
                    override val properties: MutableMap<String, Any> = HashMap()
                    override fun register(key: String, value: Any) {
                        properties[key] = value
                    }
                }.apply {
                    register("bool", true)
                    register("string", "test")
                    register("number", 5)
                    register("set", setOf(1, 2, 3))
                    register("any", object : Any() {
                        override fun equals(other: Any?): Boolean = hashCode() == other.hashCode()
                        override fun hashCode(): Int = hash
                    })
                }
            }

            it("can retrieve the actual typed values") {
                assertThat(store.getOrNull<Boolean>("bool")).isEqualTo(true)
                assertThat(store.getOrNull<String>("string")).isEqualTo("test")
                assertThat(store.getOrNull<Int>("number")).isEqualTo(5)
                assertThat(store.getOrNull<Set<Int>>("set")).isEqualTo(setOf(1, 2, 3))
                assertThat(store.getOrNull<Any>("any").hashCode()).isEqualTo(hash)
            }

            it("returns null on absent values") {
                assertThat(store.getOrNull<Boolean>("absent")).isEqualTo(null)
            }

            it("throws an error on wrong type") {
                assertThatCode { store.getOrNull<Double>("bool") }
                    .isInstanceOf(IllegalStateException::class.java)
            }
        }
    }
})
