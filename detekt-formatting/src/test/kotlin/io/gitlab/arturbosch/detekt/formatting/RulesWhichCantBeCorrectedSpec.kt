package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.wrappers.EnumEntryNameCase
import io.gitlab.arturbosch.detekt.formatting.wrappers.Filename
import io.gitlab.arturbosch.detekt.formatting.wrappers.ImportOrdering
import io.gitlab.arturbosch.detekt.formatting.wrappers.Indentation
import io.gitlab.arturbosch.detekt.formatting.wrappers.MaximumLineLength
import io.gitlab.arturbosch.detekt.formatting.wrappers.NoWildcardImports
import io.gitlab.arturbosch.detekt.formatting.wrappers.PackageName
import io.gitlab.arturbosch.detekt.test.assertThat
import org.junit.jupiter.api.Test

class RulesWhichCantBeCorrectedSpec {

    @Test
    fun `Filename findings can't be corrected`() {
        assertThat(Filename(Config.empty).lint("class NotTheFilename"))
            .isNotEmpty
            .hasExactlyElementsOfTypes(CodeSmell::class.java)
    }

    @Test
    fun `PackageName findings can't be corrected`() {
        assertThat(PackageName(Config.empty).lint("package under_score"))
            .isNotEmpty
            .hasExactlyElementsOfTypes(CodeSmell::class.java)
    }

    @Test
    fun `ImportOrdering has a case with comments which is not correctable`() {
        assertThat(
            ImportOrdering(Config.empty).lint(
                """
                    import xyz.wrong_order
                    /*comment in between*/
                    import java.io.*
                """.trimIndent()
            )
        ).isNotEmpty
            .hasExactlyElementsOfTypes(CodeSmell::class.java)
    }

    @Test
    fun `NoWildcardImports can't be corrected`() {
        assertThat(NoWildcardImports(Config.empty).lint("import java.io.*"))
            .isNotEmpty
            .hasExactlyElementsOfTypes(CodeSmell::class.java)
    }

    @Test
    fun `MaximumLineLength can't be corrected`() {
        assertThat(
            MaximumLineLength(Config.empty).lint(
                """
                    class MaximumLeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeth
                """.trimIndent()
            )
        ).isNotEmpty
            .hasExactlyElementsOfTypes(CodeSmell::class.java)
    }

    @Test
    fun `EnumEntryNameCase can't be corrected`() {
        assertThat(EnumEntryNameCase(Config.empty).lint("enum class Enum { violation_triggering_name }"))
            .isNotEmpty
            .hasExactlyElementsOfTypes(CodeSmell::class.java)
    }

    @Test
    fun `Indentation finding inside string templates can't be corrected`() {
        val multilineQuote = "${'"'}${'"'}${'"'}"
        val code = """
            val foo = $multilineQuote
                  line1
            ${'\t'}line2
                $multilineQuote.trimIndent()
        """.trimIndent()

        assertThat(Indentation(Config.empty).lint(code))
            .isNotEmpty
            .hasExactlyElementsOfTypes(CodeSmell::class.java)
    }
}
