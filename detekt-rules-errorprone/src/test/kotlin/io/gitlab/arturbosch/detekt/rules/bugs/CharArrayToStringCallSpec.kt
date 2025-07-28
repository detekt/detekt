package io.gitlab.arturbosch.detekt.rules.bugs

import dev.detekt.test.utils.KotlinEnvironmentContainer
import dev.detekt.api.Config
import dev.detekt.test.utils.KotlinCoreEnvironmentTest
import dev.detekt.test.assertThat
import dev.detekt.test.lintWithContext
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class CharArrayToStringCallSpec(private val env: KotlinEnvironmentContainer) {
    private val subject = CharArrayToStringCall(Config.empty)

    @Test
    fun `toString() calls`() {
        val code = """
            fun main() {
                val s = ""
                val charArray = "hello".toCharArray()
            
                println("${'$'}s${'$'}charArray")
                println(charArray.toString())
                println(s + charArray)

                println("${'$'}s${'$'}{"hello".toCharArray()}")
                println("hello".toCharArray().toString())
                println(s + "hello".toCharArray())
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(6)
    }

    @Test
    fun `not toString() calls`() {
        val code = """
            fun main() {
                val charArray = "hello".toCharArray()
                println(charArray)
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).isEmpty()
    }

    @Test
    fun `concatToString() calls`() {
        val code = """
            fun main() {
                val s = ""
                val charArray = "hello".toCharArray()
            
                println("${'$'}s${'$'}{charArray.concatToString()}")
                println(charArray.concatToString())
                println(s + charArray.concatToString())
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).isEmpty()
    }
}
