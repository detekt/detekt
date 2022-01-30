package io.github.detekt.test.utils

import org.assertj.core.api.Assertions.assertThatThrownBy
import org.jetbrains.kotlin.util.KotlinFrontEndException
import org.junit.jupiter.api.Test
import javax.script.ScriptException

internal class PooledScriptEngineTest {

    @Test
    fun `invalid code fails with ScriptException`() {
        val invalidCode = """
            val unknownType: Foo
        """.trimIndent()

        val engine = KotlinScriptEnginePool.borrowEngine()

        assertThatThrownBy { engine.compile(invalidCode) }
            .isInstanceOf(ScriptException::class.java)
    }

    @Test
    fun `compiling the same type twice leads to a compiler error`() {
        val validCode = """
            package pooled
            
            class A
        """.trimIndent()

        val engine = KotlinScriptEnginePool.borrowEngine()

        engine.compile(validCode)
        assertThatThrownBy { engine.compile(validCode) }
            .isInstanceOf(KotlinFrontEndException::class.java)
    }

    @Test
    fun `can be reused after failing to compile an invalid script`() {
        val invalidCode = """
            val unknownType: Foo
        """.trimIndent()

        val validCode = """
            package pooled.c
            
            class A
        """.trimIndent()

        val engine = KotlinScriptEnginePool.borrowEngine()

        assertThatThrownBy { engine.compile(invalidCode) }
            .isInstanceOf(ScriptException::class.java)

        engine.compile(validCode)
    }
}
