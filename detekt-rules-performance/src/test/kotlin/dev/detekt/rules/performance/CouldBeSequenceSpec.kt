package dev.detekt.rules.performance

import dev.detekt.test.TestConfig
import dev.detekt.test.junit.KotlinCoreEnvironmentTest
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@KotlinCoreEnvironmentTest
class CouldBeSequenceSpec(val env: KotlinEnvironmentContainer) {
    private val subject = CouldBeSequence(TestConfig("allowedOperations" to 2))

    @Test
    fun `long collection chain should be sequence`() {
        val code = """
            val myCollection = listOf(1, 2, 3, 4, 5, 6, 7, 8)
            val processed = myCollection.filter {
                it % 2 == 0
            }.map {
                it*2
            }.filter {
                it > 5
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `long collection chain should not report multiple violations`() {
        val code = """
            val myCollection = listOf(1, 2, 3, 4, 5, 6, 7, 8)
            val processed = myCollection.filter {
                it % 2 == 0
            }.map {
                it*2
            }.filter {
                it > 5
            }.filterNot {
                it > 5
            }.mapNotNull {
                if (kotlin.random.Random.nextBoolean()) null else it
            }.map {
                if (kotlin.random.Random.nextBoolean()) null else it
            }.filterIndexed { index, _ ->
                index % 2 == 0
            }.filterIsInstance<Int>()
            .take(10)
            .takeWhile { true }
            .drop(1)
            .toList()
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report issue for amount of operations that are exactly the allowed number`() {
        val code = """
            val myCollection = listOf(1, 2, 3, 4, 5, 6, 7, 8)
            val processed = myCollection.filter {
                it % 2 == 0
            }.map {
                it*2
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `one collection operation should not trigger rule`() {
        val code = """
            val myCollection = listOf(1, 2, 3, 4, 5, 6, 7, 8)
            val processed = myCollection.filter {
                it % 2 == 0
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `sequence operations should not trigger rule`() {
        val code = """
            val myCollection = listOf(1, 2, 3, 4, 5, 6, 7, 8)
            val processed = myCollection.asSequence().filter {
                it % 2 == 0
            }.map {
                it*2
            }.filter {
                it > 5
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `sequence should not trigger rule`() {
        val code = """
            val mySequence = sequenceOf(1, 10, 4, 6, 8, 39)
            val processed = mySequence.filter {
                it % 2 == 0
            }.map {
                it*2
            }.filter {
                it > 5
            }.toList()
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            ".filterIsInstance<String>()",
            ".filterNot { true }",
            ".filterNotNull()",
            ".take(1)",
            ".drop(1)",
            ".takeWhile { true }",
            ".mapNotNull { it }",
        ]
    )
    fun `#8190 - all functions should be accounted for`(additionalCall: String) {
        val code = """
        val bar = listOf<String>("bar")
            .filter { it.length > 1}
            .map { "text" }
            $additionalCall
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `use of sequence then toList call preceded by the allowed count should not trigger rule`() {
        val code = """
            val bar = listOf<String?>("bar")
                .asSequence()
                .filterIsInstance<String>()
                .filterNot { true }
                .filterNotNull()
                .take(1)
                .drop(1)
                .takeWhile { true }
                .toList()
                .takeLast(2)
                .mapNotNull { it }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `two long chains separated by methods not present in sequence package should trigger rule two times`() {
        val code = """
            val myCollection = listOf(1, 2, 3, 4, 5, 6, 7, 8)
            val processed = myCollection
                .filter {
                    it % 2 == 0
                }
                .map {
                    it * 2
                }
                .filter {
                    it > 5
                }
                .takeLast(2)
                .mapNotNull {
                    if (kotlin.random.Random.nextBoolean()) null else it
                }.map {
                    if (kotlin.random.Random.nextBoolean()) null else it
                }.filterIndexed { index, _ ->
                    index % 2 == 0
                }
                .takeLast(10)
                .toList()
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(2)
    }

    @Test
    fun `using method not allowed on sequence should not trigger rule`() {
        val code = """
            val bar = listOf<String?>("bar")
                .takeLast(2)
                .takeLastWhile { true }
                .asReversed()
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }
}
