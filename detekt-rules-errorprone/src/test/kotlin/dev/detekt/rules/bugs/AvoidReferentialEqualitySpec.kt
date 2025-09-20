package dev.detekt.rules.bugs

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.junit.KotlinCoreEnvironmentTest
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class AvoidReferentialEqualitySpec(private val env: KotlinEnvironmentContainer) {

    @Nested
    inner class `ReferentialEquality with defaults` {
        private val subject = AvoidReferentialEquality(Config.empty)

        @Test
        fun `reports usage of === for strings`() {
            val code = """
                val s = "a string"
                val b = s === "something"
                fun f(other: String) = s === other
                fun g(other: String) = if (s === other) 1 else 2
            """.trimIndent()

            val actual = subject.lintWithContext(env, code)

            assertThat(actual).hasSize(3)
        }

        @Test
        fun `reports usage of === with nullable`() {
            val code = """
                var s: String? = "a string"
                val b1 = s === "something"
                val b2 = "something" === s
                fun f(other: String) = s === other
                fun g(other: String?) = s === other
            """.trimIndent()

            val actual = subject.lintWithContext(env, code)

            assertThat(actual).hasSize(4)
        }

        @Test
        fun `reports usage of !== for strings`() {
            val code = """
                var s: String = "a string"
                val b = s !== "something"
            """.trimIndent()

            val actual = subject.lintWithContext(env, code)

            assertThat(actual).hasSize(1)
        }

        @Test
        fun `ignores usage of === for non strings`() {
            val code = """
                val i = 42
                val l = 99L
                val c = 'a'
                val b = i === 1 || l === 100L || c === 'b'
            """.trimIndent()

            val actual = subject.lintWithContext(env, code)

            assertThat(actual).isEmpty()
        }

        @Test
        fun `ignores usage of == for strings`() {
            val code = """
                val s = "a string"
                val b = s == "something"
                fun f(other: String) = s == other
                fun g(other: String) = if (s == other) 1 else 2
            """.trimIndent()

            val actual = subject.lintWithContext(env, code)

            assertThat(actual).isEmpty()
        }

        @Test
        fun `ignores usage of === with generic parameters`() {
            val code = """
                fun <T : Any> same(one: T, two: T): Boolean = one === two
                val b = same("this", "that")
            """.trimIndent()

            val actual = subject.lintWithContext(env, code)

            assertThat(actual).isEmpty()
        }
    }

    @Nested
    inner class `ReferentialEquality enabled for all types` {
        private val subject = AvoidReferentialEquality(TestConfig("forbiddenTypePatterns" to listOf("*")))

        @Test
        fun `reports usage of === for strings`() {
            val code = """
                val s = "a string"
                val i = 1
                val list = listOf(1)
                val b = s === "other" || i === 42 || list === listOf(2)
            """.trimIndent()

            val actual = subject.lintWithContext(env, code)

            assertThat(actual).hasSize(3)
        }
    }

    @Nested
    inner class `ReferentialEquality enabled for all lists` {
        private val subject = AvoidReferentialEquality(
            TestConfig("forbiddenTypePatterns" to listOf("kotlin.collections.*List"))
        )

        @Test
        fun `reports usage of ===`() {
            val code = """
                val listA = listOf(1)
                val listB = listOf(1)
                val mutableList = mutableListOf(1)
                val b = listOf(2) === listA || listA === listB || mutableList === mutableListOf(2)
            """.trimIndent()

            val actual = subject.lintWithContext(env, code)

            assertThat(actual).hasSize(3)
        }

        @Test
        fun `ignores usage of ==`() {
            val code = """
                val listA = listOf(1)
                val listB = listOf(1)
                val mutableList = mutableListOf(1)
                val b = listOf(2) == listA || listA == listB || mutableList == mutableListOf(2)
            """.trimIndent()

            val actual = subject.lintWithContext(env, code)

            assertThat(actual).isEmpty()
        }
    }
}
