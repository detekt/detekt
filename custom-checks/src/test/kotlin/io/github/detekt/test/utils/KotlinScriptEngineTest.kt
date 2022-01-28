package io.github.detekt.test.utils

import org.junit.jupiter.api.RepeatedTest

class KotlinScriptEngineTest {

    @RepeatedTest(2)
    fun `can compile a script`() {
        val code = """
            package foo
            
            class A
        """.trimIndent()
        KotlinScriptEngine.compile(code)
    }
}
