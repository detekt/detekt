package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class CouldBeSequenceSpec(val env: KotlinCoreEnvironment) {
    val subject = CouldBeSequence(TestConfig("threshold" to 3))

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
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `one collection operation should not trigger rule`() {
        val code = """
        val myCollection = listOf(1, 2, 3, 4, 5, 6, 7, 8)
        val processed = myCollection.filter {
            it % 2 == 0
        }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
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
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `sequence should not trigger rule`() {
        val code = """
        val mySequence = sequenceOf(1,10,4,6,8,39)
        val processed = mySequence.filter {
            it % 2 == 0
        }.map {
            it*2
        }.filter {
            it > 5
        }.toList()
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }
}
