package dev.detekt.rules.potentialbugs

import dev.detekt.api.Config
import dev.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ExplicitGarbageCollectionCallSpec {
    private val subject = ExplicitGarbageCollectionCall(Config.empty)

    @Test
    fun `reports garbage collector calls`() {
        val code = """
            fun f() {
                System.gc()
                Runtime.getRuntime().gc()
                System.runFinalization()
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(3)
    }
}
