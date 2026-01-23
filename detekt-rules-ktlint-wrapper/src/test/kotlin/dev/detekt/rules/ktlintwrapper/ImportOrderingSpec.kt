package dev.detekt.rules.ktlintwrapper

import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.wrappers.ImportOrdering
import dev.detekt.test.TestConfig
import dev.detekt.test.assertj.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * Some test cases were used directly from KtLint to verify the wrapper rule:
 *
 * https://github.com/pinterest/ktlint/blob/0.37.0/ktlint-ruleset-standard/src/test/kotlin/com/pinterest/ktlint/ruleset/standard/importordering/ImportOrderingRuleAsciiTest.kt
 * https://github.com/pinterest/ktlint/blob/0.37.0/ktlint-ruleset-standard/src/test/kotlin/com/pinterest/ktlint/ruleset/standard/importordering/ImportOrderingRuleCustomTest.kt
 * https://github.com/pinterest/ktlint/blob/0.37.0/ktlint-ruleset-standard/src/test/kotlin/com/pinterest/ktlint/ruleset/standard/importordering/ImportOrderingRuleIdeaTest.kt
 */
class ImportOrderingSpec {

    @Test
    fun `defaults to the idea layout`() {
        val findings = ImportOrdering(Config.Empty).lint(
            """
                import android.app.Activity
                import android.view.View
                import android.view.ViewGroup
                import kotlinx.coroutines.CoroutineDispatcher
                import ru.example.a
                import java.util.List
                import javax.net.ssl.SSLHandshakeException
                import kotlin.concurrent.Thread
                import kotlin.io.Closeable
                import android.content.Context as Ctx
                import androidx.fragment.app.Fragment as F
            """.trimIndent()
        )

        assertThat(findings).isEmpty()
    }

    @Nested
    inner class `can be configured to use the ascii one` {

        val negativeCase = """
            import a.A
            import a.AB
            import b.C
        """.trimIndent()

        val positiveCase = """
            import a.A
            import java.util.ArrayList
            import a.AB
        """.trimIndent()

        @Test
        fun `passes for alphabetical order`() {
            val findings = ImportOrdering(TestConfig("layout" to ImportOrdering.ASCII_PATTERN))
                .lint(negativeCase)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `fails for non alphabetical order`() {
            val findings = ImportOrdering(TestConfig("layout" to ImportOrdering.ASCII_PATTERN))
                .lint(positiveCase)

            assertThat(findings).hasSize(1)
        }

        @Nested
        inner class `defaults to ascii if 'android'' property is set to true` {

            @Test
            fun `passes for alphabetical order`() {
                assertThat(ImportOrdering(TestConfig("android" to true)).lint(negativeCase)).isEmpty()
            }

            @Test
            fun `fails for non alphabetical order`() {
                assertThat(ImportOrdering(TestConfig("android" to true)).lint(positiveCase)).hasSize(1)
            }
        }
    }

    @Nested
    inner class `supports custom patterns` {

        @Test
        fun `misses a empty line between aliases and other imports`() {
            val findings = ImportOrdering(TestConfig("layout" to "*,|,^*")).lint(
                """
                    import android.app.Activity
                    import android.view.View
                    import android.view.ViewGroup
                    import java.util.List
                    import kotlin.concurrent.Thread
                    import android.content.Context as Ctx
                    import androidx.fragment.app.Fragment as F
                """.trimIndent()
            )

            assertThat(findings).hasSize(1)
        }

        @Test
        fun `passes for empty line between aliases and other imports`() {
            val findings = ImportOrdering(TestConfig("layout" to "*,|,^*")).lint(
                """
                    import android.app.Activity
                    import android.view.View
                    import android.view.ViewGroup
                    import java.util.List
                    import kotlin.concurrent.Thread
                    
                    import android.content.Context as Ctx
                    import androidx.fragment.app.Fragment as F
                """.trimIndent()
            )

            assertThat(findings).isEmpty()
        }
    }
}
