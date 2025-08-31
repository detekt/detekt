package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinCoreEnvironmentTest
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UseEmptyCounterpartSpec(val env: KotlinEnvironmentContainer) {
    val rule = UseEmptyCounterpart(Config.empty)

    @Test
    fun `reports no-arg instantiation`() {
        val code = """
            val array = arrayOf<Any>()
            val list = listOf<Any>()
            val nonNullList = listOfNotNull<Any>()
            val map = mapOf<Any, Any>()
            val sequence = sequenceOf<Any>()
            val set = setOf<Any>()
        """.trimIndent()
        assertThat(rule.lintWithContext(env, code)).hasSize(6)
    }

    @Test
    fun `reports no-arg instantiation with inferred type parameters`() {
        val code = """
            val array: Array<Any> = arrayOf()
            val list: List<Any> = listOf()
            val list2: List<Any> = listOfNotNull()
            val map: Map<Any, Any> = mapOf()
            val sequence: Sequence<Any> = sequenceOf()
            val set: Set<Any> = setOf()
        """.trimIndent()
        assertThat(rule.lintWithContext(env, code)).hasSize(6)
    }

    @Test
    fun `does not report empty instantiation`() {
        val code = """
            val array = emptyArray<Any>()
            val list = emptyList<Any>()
            val map = emptyMap<Any, Any>()
            val sequence = emptySequence<Any>()
            val set = emptySet<Any>()
        """.trimIndent()
        assertThat(rule.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report instantiation with arguments`() {
        val code = """
            val array = arrayOf(0)
            val list = listOf(0)
            val nonNullList = listOfNotNull(0)
            val map = mapOf(0 to 0)
            val sequence = sequenceOf(0)
            val set = setOf(0)
        """.trimIndent()
        assertThat(rule.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report no-arg custom function with same name as function with empty counterpart`() {
        val code = """
            fun <T> arrayOf(): Array<T> = TODO()
            fun <T> listOf(): List<T> = TODO()
            fun <T> listOfNotNull(): List<T> = TODO()
            fun <K, V> mapOf(): Map<K, V> = TODO()
            fun <T> sequenceOf(): Sequence<T> = TODO()
            fun <T> setOf(): Set<T> = TODO()
            
            val array = arrayOf<Any>()
            val list = listOf<Any>()
            val nonNullList = listOfNotNull<Any>()
            val map = mapOf<Any, Any>()
            val sequence = sequenceOf<Any>()
            val set = setOf<Any>()
        """.trimIndent()
        assertThat(rule.lintWithContext(env, code)).isEmpty()
    }
}
