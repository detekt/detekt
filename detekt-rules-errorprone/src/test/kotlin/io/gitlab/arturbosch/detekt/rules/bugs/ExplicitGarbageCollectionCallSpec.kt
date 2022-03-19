package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ExplicitGarbageCollectionCallSpec {
    private val subject = ExplicitGarbageCollectionCall(Config.empty)

    @Nested
    inner class `ExplicitGarbageCollectionCall rule` {

        @Test
        fun `reports garbage collector calls`() {
            val code = """
                fun f() {
                    System.gc()
                    Runtime.getRuntime().gc()
                    System.runFinalization()
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(3)
        }
    }
}
