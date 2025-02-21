package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.formatting.wrappers.EnumEntryNameCase
import io.gitlab.arturbosch.detekt.formatting.wrappers.Filename
import io.gitlab.arturbosch.detekt.formatting.wrappers.ImportOrdering
import io.gitlab.arturbosch.detekt.formatting.wrappers.Indentation
import io.gitlab.arturbosch.detekt.formatting.wrappers.MaximumLineLength
import io.gitlab.arturbosch.detekt.formatting.wrappers.NoWildcardImports
import io.gitlab.arturbosch.detekt.formatting.wrappers.PackageName
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import org.junit.jupiter.api.Test

class RulesWhichCantBeCorrectedSpec {

    private val autoCorrectConfig = TestConfig("autoCorrect" to true)

    @Test
    fun `Filename findings can't be corrected`() {
        assertThat(Filename(autoCorrectConfig).lint("class NotTheFilename"))
            .singleElement()
            .noSuppress()
    }

    @Test
    fun `PackageName findings can't be corrected`() {
        assertThat(PackageName(autoCorrectConfig).lint("package under_score"))
            .singleElement()
            .noSuppress()
    }

    @Test
    fun `ImportOrdering has a case with comments which is not correctable`() {
        val code = """
            import xyz.wrong_order
            /*comment in between*/
            import java.io.*
        """.trimIndent()
        assertThat(ImportOrdering(autoCorrectConfig).lint(code))
            .singleElement()
            .noSuppress()
    }

    @Test
    fun `NoWildcardImports can't be corrected`() {
        assertThat(NoWildcardImports(autoCorrectConfig).lint("import java.io.*"))
            .singleElement()
            .noSuppress()
    }

    @Test
    fun `MaximumLineLength can't be corrected`() {
        val code =
            "class MaximumLeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeth"
        assertThat(MaximumLineLength(autoCorrectConfig).lint(code))
            .singleElement()
            .noSuppress()
    }

    @Test
    fun `EnumEntryNameCase can't be corrected`() {
        assertThat(EnumEntryNameCase(autoCorrectConfig).lint("enum class Enum { violation_triggering_name }"))
            .singleElement()
            .noSuppress()
    }

    @Test
    fun `Indentation finding inside string templates can't be corrected`() {
        val multilineQuote = "\"\"\""
        val code = """
            val foo = $multilineQuote
                  line1
            ${'\t'}line2
                $multilineQuote.trimIndent()
        """.trimIndent()

        assertThat(Indentation(autoCorrectConfig).lint(code))
            .singleElement()
            .noSuppress()
    }
}
