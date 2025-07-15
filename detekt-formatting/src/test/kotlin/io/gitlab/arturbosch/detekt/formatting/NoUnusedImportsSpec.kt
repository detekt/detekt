package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.formatting.wrappers.NoUnusedImports
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
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

        assertThat(findings)
            .hasSize(3)
            .hasStartSourceLocations(
                SourceLocation(3, 1),
                SourceLocation(4, 1),
                SourceLocation(5, 1)
            )
    }
}
