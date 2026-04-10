package dev.detekt.rules.potentialbugs

import dev.detekt.test.junit.KotlinCoreEnvironmentTest
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

private val LIFETIME_OWNER_STUB = """
  package org.jetbrains.kotlin.analysis.api.lifetime

  interface KaLifetimeOwner
""".trimIndent()

private val ANALYZE_STUB = """
  package org.jetbrains.kotlin.analysis.api

  import org.jetbrains.kotlin.analysis.api.lifetime.KaLifetimeOwner

  interface KaSession : KaLifetimeOwner
  class KtElement
  fun <R> analyze(element: KtElement, action: KaSession.() -> R): R = TODO()
""".trimIndent()

private val SYMBOL_POINTER_STUB = """
  package org.jetbrains.kotlin.analysis.api.symbols.pointers

  class KaSymbolPointer<T>
""".trimIndent()

private val DEFINITIONS = """
  import org.jetbrains.kotlin.analysis.api.lifetime.KaLifetimeOwner
  import org.jetbrains.kotlin.analysis.api.KaSession
  import org.jetbrains.kotlin.analysis.api.KtElement
  import org.jetbrains.kotlin.analysis.api.analyze
  import org.jetbrains.kotlin.analysis.api.symbols.pointers.KaSymbolPointer

  open class KaType : KaLifetimeOwner
  class KaClassType : KaType()
  open class KaSymbol : KaLifetimeOwner
  val element = KtElement()
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
            KaType()
          }
            """.trimIndent(),
            LIFETIME_OWNER_STUB,
            ANALYZE_STUB,
        )

        assertThat(findings).isNotEmpty()
    }

    @Test
    fun `returning nullable KaType triggers the rule`() {
        val findings = rule.lintWithContext(
            env,
            """
          $DEFINITIONS

          fun go(condition: Boolean) {
            val result = analyze(element) {
              if (condition) KaType() else null
            }
          }
            """.trimIndent(),
            LIFETIME_OWNER_STUB,
            ANALYZE_STUB,
        )

        assertThat(findings).isNotEmpty()
    }

    @Test
    fun `returning List of KaType triggers the rule`() {
        val findings = rule.lintWithContext(
            env,
            """
          $DEFINITIONS

          val result = analyze(element) {
            listOf(KaType())
          }
            """.trimIndent(),
            LIFETIME_OWNER_STUB,
            ANALYZE_STUB,
        )

        assertThat(findings).isNotEmpty()
    }

    @Test
    fun `returning Set of nullable KaSymbol triggers the rule`() {
        val findings = rule.lintWithContext(
            env,
            """
          $DEFINITIONS

          val result = analyze(element) {
            setOf<KaSymbol?>(KaSymbol(), null)
          }
            """.trimIndent(),
            LIFETIME_OWNER_STUB,
            ANALYZE_STUB,
        )

        assertThat(findings).isNotEmpty()
    }

    @Test
    fun `returning an anonymous object extending KaLifetimeOwner triggers the rule`() {
        val findings = rule.lintWithContext(
            env,
            """
          $DEFINITIONS

          val result = analyze(element) {
            object : KaLifetimeOwner {}
          }
            """.trimIndent(),
            LIFETIME_OWNER_STUB,
            ANALYZE_STUB,
        )

        assertThat(findings).isNotEmpty()
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
            LIFETIME_OWNER_STUB,
            ANALYZE_STUB,
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
            LIFETIME_OWNER_STUB,
            ANALYZE_STUB,
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
            KaSymbolPointer<KaSymbol>()
          }
            """.trimIndent(),
            LIFETIME_OWNER_STUB,
            ANALYZE_STUB,
            SYMBOL_POINTER_STUB,
        )

        assertThat(findings).isEmpty()
    }

    @Test
    fun `returning Sequence of subtype from a different package triggers the rule`() {
        val symbolsStub = """
          package org.jetbrains.kotlin.analysis.api.symbols
          import org.jetbrains.kotlin.analysis.api.lifetime.KaLifetimeOwner
          open class KaCallableSymbol : KaLifetimeOwner
        """.trimIndent()
        val findings = rule.lintWithContext(
            env,
            """
          import org.jetbrains.kotlin.analysis.api.KtElement
          import org.jetbrains.kotlin.analysis.api.analyze
          import org.jetbrains.kotlin.analysis.api.symbols.KaCallableSymbol

          val element = KtElement()

          fun getSymbols(): Sequence<KaCallableSymbol>? =
            analyze(element) {
              sequence {
                yield(KaCallableSymbol())
              }
            }
            """.trimIndent(),
            LIFETIME_OWNER_STUB,
            ANALYZE_STUB,
            symbolsStub,
        )

        assertThat(findings).isNotEmpty()
    }

    @Test
    fun `returning a banned type from a non-analyze lambda does not trigger the rule`() {
        val findings = rule.lintWithContext(
            env,
            """
          $DEFINITIONS

          fun <R> notAnalyze(element: org.jetbrains.kotlin.analysis.api.KtElement, action: KaSession.() -> R): R = TODO()

          val result = notAnalyze(element) {
            KaType()
          }
            """.trimIndent(),
            LIFETIME_OWNER_STUB,
            ANALYZE_STUB,
        )

        assertThat(findings).isEmpty()
    }
}
