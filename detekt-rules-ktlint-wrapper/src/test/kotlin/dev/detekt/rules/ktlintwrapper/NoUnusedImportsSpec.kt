package dev.detekt.rules.ktlintwrapper

import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.wrappers.NoUnusedImports
import dev.detekt.test.TestConfig
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.lint
import org.junit.jupiter.api.Test

class NoUnusedImportsSpec {

    @Test
    fun `regression - findings are reported at the import not the file node`() {
        val code = """
            package testData

            import java.util.HashMap
            import java.util.HashSet
            import java.util.ArrayList

            class Poko(

            ) {


            }

            fun f() = 5
        """.trimIndent()

        // autoCorrect is off so that the reported locations are not shifted by the correction itself
        val findings = NoUnusedImports(TestConfig(Config.AUTO_CORRECT_KEY to false)).lint(code)

        assertThat(findings).satisfiesExactlyInAnyOrder(
            { assertThat(it).hasStartSourceLocation(3, 1) },
            { assertThat(it).hasStartSourceLocation(4, 1) },
            { assertThat(it).hasStartSourceLocation(5, 1) },
        )
    }
}
