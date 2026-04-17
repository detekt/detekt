package dev.detekt.rules.potentialbugs

import dev.detekt.test.junit.KotlinCoreEnvironmentTest
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

private val DEFINITIONS = """
    import org.jetbrains.kotlin.analysis.api.KaSession
    import org.jetbrains.kotlin.analysis.api.analyze
    import org.jetbrains.kotlin.analysis.api.lifetime.KaLifetimeOwner
    import org.jetbrains.kotlin.analysis.api.lifetime.KaLifetimeToken
    import org.jetbrains.kotlin.analysis.api.types.KaType
    import org.jetbrains.kotlin.analysis.api.symbols.KaCallableSymbol
    import org.jetbrains.kotlin.analysis.api.symbols.KaSymbol
    import org.jetbrains.kotlin.analysis.api.symbols.pointers.KaSymbolPointer
    import org.jetbrains.kotlin.psi.KtElement

    val element: KtElement = TODO()
""".trimIndent()

@KotlinCoreEnvironmentTest
class AvoidLeakingAnalysisApiTypesFromSessionsSpec(private val env: KotlinEnvironmentContainer) {
    private lateinit var rule: AvoidLeakingAnalysisApiTypesFromSessions

    @BeforeEach
    fun setup() {
        rule = AvoidLeakingAnalysisApiTypesFromSessions()
    }

    @Test
    fun `returning a subtype of KaLifetimeOwner triggers the rule`() {
        val findings = rule.lintWithContext(
            env,
            """
                $DEFINITIONS

                val result = analyze(element) {
                    Unit as KaType
                }
            """.trimIndent(),
        )

        assertThat(findings).hasSize(1)
    }

    @Test
    fun `returning nullable KaType triggers the rule`() {
        val findings = rule.lintWithContext(
            env,
            """
                $DEFINITIONS

                fun go(condition: Boolean) {
                    val result = analyze(element) {
                        if (condition) Unit as KaType else null
                    }
                }
            """.trimIndent(),
        )

        assertThat(findings).hasSize(1)
    }

    @Test
    fun `returning List of KaType triggers the rule`() {
        val findings = rule.lintWithContext(
            env,
            """
                $DEFINITIONS

                val result = analyze(element) {
                    listOf(Unit as KaType)
                }
            """.trimIndent(),
        )

        assertThat(findings).hasSize(1)
    }

    @Test
    fun `returning Set of nullable KaSymbol triggers the rule`() {
        val findings = rule.lintWithContext(
            env,
            """
                $DEFINITIONS

                val result = analyze(element) {
                    setOf<KaSymbol?>(Unit as KaSymbol, null)
                }
            """.trimIndent(),
        )

        assertThat(findings).hasSize(1)
    }

    @Test
    fun `returning an anonymous object extending KaLifetimeOwner triggers the rule`() {
        val findings = rule.lintWithContext(
            env,
            """
                $DEFINITIONS

                val result = analyze(element) {
                    object : KaLifetimeOwner {
                        override val token: KaLifetimeToken
                            get() = TODO("Not yet implemented")
                    }
                }
            """.trimIndent(),
        )

        assertThat(findings).hasSize(1)
    }

    @Test
    fun `returning a plain data class does not trigger the rule`() {
        val findings = rule.lintWithContext(
            env,
            """
               $DEFINITIONS

               data class SafeResult(val name: String)

               val result = analyze(element) {
                   SafeResult("hello")
               }
            """.trimIndent(),
        )

        assertThat(findings).isEmpty()
    }

    @Test
    fun `returning Unit does not trigger the rule`() {
        val findings = rule.lintWithContext(
            env,
            """
                $DEFINITIONS

                fun go() {
                  analyze(element) {
                      println("side effect only")
                  }
                }
            """.trimIndent(),
        )

        assertThat(findings).isEmpty()
    }

    @Test
    fun `returning KaSymbolPointer of a banned type does not trigger the rule`() {
        val findings = rule.lintWithContext(
            env,
            """
                $DEFINITIONS

                val result = analyze(element) {
                    Unit as KaSymbolPointer<KaSymbol>
                }
            """.trimIndent(),
        )

        assertThat(findings).isEmpty()
    }

    @Test
    fun `returning Sequence of subtype from a different package triggers the rule`() {
        val findings = rule.lintWithContext(
            env,
            """
                $DEFINITIONS

                fun getSymbols(): Sequence<KaCallableSymbol>? =
                    analyze(element) {
                        sequence {
                            yield(Unit as KaCallableSymbol)
                        }
                    }
            """.trimIndent(),
        )

        assertThat(findings).hasSize(1)
    }

    @Test
    fun `returning a banned type from a non-analyze lambda does not trigger the rule`() {
        val findings = rule.lintWithContext(
            env,
            """
                $DEFINITIONS

                fun <R> notAnalyze(element: KtElement, action: KaSession.() -> R): R = TODO()

                val result = notAnalyze(element) {
                    Unit as KaType
                }
            """.trimIndent(),
        )

        assertThat(findings).isEmpty()
    }
}
