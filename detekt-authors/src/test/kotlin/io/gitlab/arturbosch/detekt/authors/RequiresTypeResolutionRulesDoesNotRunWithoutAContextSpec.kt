package io.gitlab.arturbosch.detekt.authors

import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.Test

class RequiresTypeResolutionRulesDoesNotRunWithoutAContextSpec {

    private val rule = RequiresTypeResolutionRulesDoesNotRunWithoutAContext()

    @Test
    fun `should not report no annotated classes`() {
        val code = """
            import io.gitlab.arturbosch.detekt.api.Config
            import io.gitlab.arturbosch.detekt.api.Rule

            class A(config: Config = Config.empty) : Rule(config) {
                override val issue = error("I don't care")
            }
        """
        val findings = rule.compileAndLint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `should report annotated classes without visitCondition()`() {
        val code = """
            import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
            import io.gitlab.arturbosch.detekt.api.Config
            import io.gitlab.arturbosch.detekt.api.Rule

            @RequiresTypeResolution
            class A(config: Config = Config.empty) : Rule(config) {
                override val issue = error("I don't care")
            }
        """
        val findings = rule.compileAndLint(code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `should report annotated classes with visitCondition() but no check for bindingContext`() {
        val code = """
            import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
            import io.gitlab.arturbosch.detekt.api.Config
            import io.gitlab.arturbosch.detekt.api.Rule
            import org.jetbrains.kotlin.psi.KtFile

            @RequiresTypeResolution
            class A(config: Config = Config.empty) : Rule(config) {
                override val issue = error("I don't care")

                override fun visitCondition(root: KtFile) = super.visitCondition(root)
            }
        """
        val findings = rule.compileAndLint(code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `should not report annotated classes with visitCondition() and check for bindingContext first line`() {
        val code = """
            import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
            import io.gitlab.arturbosch.detekt.api.Config
            import io.gitlab.arturbosch.detekt.api.Rule
            import org.jetbrains.kotlin.psi.KtFile
            import org.jetbrains.kotlin.resolve.BindingContext

            @RequiresTypeResolution
            class A(config: Config = Config.empty) : Rule(config) {
                override val issue = error("I don't care")

                override fun visitCondition(root: KtFile) =
                    bindingContext != BindingContext.EMPTY && super.visitCondition(root)
            }
        """
        val findings = rule.compileAndLint(code)
        assertThat(findings).isEmpty()
    }
}
