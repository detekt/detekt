package io.github.detekt.test.utils

import org.junit.jupiter.api.RepeatedTest

class KotlinScriptEngineTest {

    @RepeatedTest(10)
    fun `can compile a script`() {
        val code = """
            package foo
            
            class A
        """.trimIndent()
        KotlinScriptEngine.compile(code)
    }
}
