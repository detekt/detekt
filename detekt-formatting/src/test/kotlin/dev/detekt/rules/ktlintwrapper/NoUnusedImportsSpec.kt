package dev.detekt.rules.ktlintwrapper

import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.wrappers.NoUnusedImports
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

        val findings = NoUnusedImports(Config.empty).lint(code)

        assertThat(findings).hasSize(3)
        assertThat(findings).element(0)
            .hasStartSourceLocation(3, 1)
        assertThat(findings).element(1)
            .hasStartSourceLocation(4, 1)
        assertThat(findings).element(2)
            .hasStartSourceLocation(5, 1)
    }
}
