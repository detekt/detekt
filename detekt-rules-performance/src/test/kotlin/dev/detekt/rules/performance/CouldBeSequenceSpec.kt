package dev.detekt.rules.performance

import dev.detekt.test.TestConfig
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinCoreEnvironmentTest
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

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
}
