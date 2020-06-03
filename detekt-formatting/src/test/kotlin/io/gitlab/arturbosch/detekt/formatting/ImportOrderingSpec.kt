package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.wrappers.ImportOrdering
import io.gitlab.arturbosch.detekt.test.TestConfig
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * Some test cases were used directly from KtLint to verify the wrapper rule:
 *
 * https://github.com/pinterest/ktlint/blob/cdf871b6f015359f9a6f02e15ef1b85a6c442437/ktlint-ruleset-standard/src/test/kotlin/com/pinterest/ktlint/ruleset/standard/importordering/ImportOrderingRuleAsciiTest.kt
 * https://github.com/pinterest/ktlint/blob/6bdd345f204e6edcc3dec5e1b139c2d573227dad/ktlint-ruleset-standard/src/test/kotlin/com/pinterest/ktlint/ruleset/standard/importordering/ImportOrderingRuleCustomTest.kt
 * https://github.com/pinterest/ktlint/blob/cdf871b6f015359f9a6f02e15ef1b85a6c442437/ktlint-ruleset-standard/src/test/kotlin/com/pinterest/ktlint/ruleset/standard/importordering/ImportOrderingRuleIdeaTest.kt
 */
class ImportOrderingSpec : Spek({

    describe("different import ordering layouts") {

        it("defaults to the idea layout") {
            val findings = ImportOrdering(Config.empty).lint("""
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
            """.trimIndent())

            assertThat(findings).isEmpty()
        }

        describe("can be configured to use the ascii one") {

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

            it("passes for alphabetical order") {
                val findings = ImportOrdering(TestConfig("layout" to "ascii")).lint(negativeCase)

                assertThat(findings).isEmpty()
            }

            it("fails for non alphabetical order") {
                val findings = ImportOrdering(TestConfig("layout" to "ascii")).lint(positiveCase)

                assertThat(findings).hasSize(1)
            }

            describe("defaults to ascii if 'android'' property is set to true") {

                it("passes for alphabetical order") {
                    assertThat(ImportOrdering(TestConfig("android" to "true")).lint(negativeCase)).isEmpty()
                }

                it("fails for non alphabetical order") {
                    assertThat(ImportOrdering(TestConfig("android" to "true")).lint(positiveCase)).hasSize(1)
                }
            }
        }

        describe("supports custom patterns") {

            it("misses a empty line between aliases and other imports") {
                val findings = ImportOrdering(TestConfig("layout" to "*,|,^*")).lint("""
                    import android.app.Activity
                    import android.view.View
                    import android.view.ViewGroup
                    import java.util.List
                    import kotlin.concurrent.Thread
                    import android.content.Context as Ctx
                    import androidx.fragment.app.Fragment as F
                """.trimIndent())

                assertThat(findings).hasSize(1)
            }
            it("passes for empty line between aliases and other imports") {
                val findings = ImportOrdering(TestConfig("layout" to "*,|,^*")).lint("""
                    import android.app.Activity
                    import android.view.View
                    import android.view.ViewGroup
                    import java.util.List
                    import kotlin.concurrent.Thread

                    import android.content.Context as Ctx
                    import androidx.fragment.app.Fragment as F
                """.trimIndent())

                assertThat(findings).isEmpty()
            }
        }
    }
})
