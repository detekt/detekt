package dev.detekt.rules.ktlintwrapper

import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.wrappers.NoLineBreakBeforeAssignment
import dev.detekt.test.FakeLanguageVersionSettings
import dev.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class KtlintEngineSpec {

    @Test
    fun `context is shared across visits on the same KtFile`() {
        val ktFile = compileContentForTest("fun main()\n= Unit", "EngineSpecTest.kt")
        NoLineBreakBeforeAssignment(Config.empty).visitFile(ktFile, FakeLanguageVersionSettings())

        val first = KtlintEngine.contextFor(ktFile)
        val second = KtlintEngine.contextFor(ktFile)

        assertThat(first).isSameAs(second)
    }

    @Test
    fun `different KtFiles yield independent contexts`() {
        val fileA = compileContentForTest("fun main()\n= Unit", "EngineSpecA.kt")
        val fileB = compileContentForTest("fun main()\n= Unit", "EngineSpecB.kt")
        val rule = NoLineBreakBeforeAssignment(Config.empty)
        rule.visitFile(fileA, FakeLanguageVersionSettings())
        rule.visitFile(fileB, FakeLanguageVersionSettings())

        val contextA = KtlintEngine.contextFor(fileA)
        val contextB = KtlintEngine.contextFor(fileB)

        assertThat(contextA).isNotSameAs(contextB)
    }

    @Test
    fun `findings reach the rule via the shared engine walk`() {
        // Smoke test for the whole pipeline: KtlintRule.visit -> KtlintEngine.contextFor ->
        // single shared walk -> emit findings tagged with the rule's RuleId. The wrapped
        // ktlint rule reports a single violation on `fun main()\n= Unit`.
        val findings = NoLineBreakBeforeAssignment(Config.empty).lint("fun main()\n= Unit")

        assertThat(findings).hasSize(1)
    }

    @Test
    fun `ruleDoneWithFile evicts the per-file context once remaining visits reach zero`() {
        val ktFile = compileContentForTest("fun main()\n= Unit", "EngineSpecEviction.kt")
        NoLineBreakBeforeAssignment(Config.empty).visitFile(ktFile, FakeLanguageVersionSettings())

        val context = KtlintEngine.contextFor(ktFile)
        while (context.remainingVisits.get() > 0) {
            KtlintEngine.ruleDoneWithFile(ktFile, context)
        }

        // After draining, a subsequent contextFor must build a fresh context (different identity).
        val rebuilt = KtlintEngine.contextFor(ktFile)
        assertThat(rebuilt).isNotSameAs(context)
    }
}
