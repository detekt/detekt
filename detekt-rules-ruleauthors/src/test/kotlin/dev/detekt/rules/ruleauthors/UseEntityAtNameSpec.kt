package dev.detekt.rules.ruleauthors

import dev.detekt.api.Config
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.lint
import org.junit.jupiter.api.Test

internal class UseEntityAtNameSpec {

    private val rule = UseEntityAtName(Config.Empty)

    @Test
    fun `should not report calls when there's no name involved`() {
        val code = """
            import dev.detekt.api.Finding
            import dev.detekt.api.Entity
            import dev.detekt.api.Rule
            import com.intellij.psi.PsiElement
            
            fun Rule.f(element: PsiElement) {
                report(Finding(Entity.from(element), "message"))
            }
        """.trimIndent()
        val findings = rule.lint(code, compile = false)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `should not report calls when atName is already used`() {
        val code = """
            import dev.detekt.api.Finding
            import dev.detekt.api.Entity
            import dev.detekt.api.Rule
            import org.jetbrains.kotlin.psi.KtNamedDeclaration
            
            fun Rule.f(element: KtNamedDeclaration) {
                report(Finding(Entity.atName(element), "message"))
            }
        """.trimIndent()
        val findings = rule.lint(code, compile = false)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `should report calls where nameIdentifier is used directly with bang`() {
        val code = """
            import dev.detekt.api.Finding
            import dev.detekt.api.Entity
            import dev.detekt.api.Rule
            import com.intellij.psi.PsiNameIdentifierOwner
            
            fun Rule.f(element: PsiNameIdentifierOwner) {
                report(Finding(Entity.from(element.nameIdentifier!!), "message"))
            }
        """.trimIndent()
        val findings = rule.lint(code, compile = false)
        assertThat(findings).singleElement()
            .hasMessage("Recommended to use Entity.atName(element) instead.")
            .hasTextLocation("from")
    }

    @Test
    fun `should report calls where nameIdentifier is used directly with double-bang`() {
        val code = """
            import dev.detekt.api.Finding
            import dev.detekt.api.Entity
            import dev.detekt.api.Rule
            import com.intellij.psi.PsiNameIdentifierOwner
            
            fun Rule.f(element: PsiNameIdentifierOwner) {
                report(Finding(Entity.from(element.nameIdentifier!!!!), "message"))
            }
        """.trimIndent()
        val findings = rule.lint(code, compile = false)
        assertThat(findings).singleElement()
            .hasMessage("Recommended to use Entity.atName(element) instead.")
            .hasTextLocation("from")
    }

    @Test
    fun `should report calls where nameIdentifier is used with elvis with same fallback`() {
        val code = """
            import dev.detekt.api.Finding
            import dev.detekt.api.Entity
            import dev.detekt.api.Rule
            import com.intellij.psi.PsiNameIdentifierOwner
            
            fun Rule.f(element: PsiNameIdentifierOwner) {
                report(Finding(Entity.from(element.nameIdentifier ?: element), "message"))
            }
        """.trimIndent()
        val findings = rule.lint(code, compile = false)
        assertThat(findings).singleElement()
            .hasMessage("Recommended to use Entity.atName(element) instead.")
            .hasTextLocation("from")
    }

    @Test
    fun `should report calls where nameIdentifier is used with elvis with complex fallback`() {
        val code = """
            import dev.detekt.api.Finding
            import dev.detekt.api.Entity
            import dev.detekt.api.Rule
            import com.intellij.psi.PsiExpression
            import com.intellij.psi.PsiNameIdentifierOwner
            import org.jetbrains.kotlin.psi.KtClass
            import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
            
            fun Rule.f(element: PsiExpression) {
                report(Finding(Entity.from(element.getStrictParentOfType<KtClass>()?.nameIdentifier ?: element), "message"))
            }
        """.trimIndent()
        val findings = rule.lint(code, compile = false)
        assertThat(findings).singleElement()
            .hasMessage("Recommended to use Entity.atName(element.getStrictParentOfType<KtClass>()) instead.")
            .hasTextLocation("from")
    }

    @Test
    fun `should report calls where nameIdentifier is used with elvis with other fallback`() {
        val code = """
            import dev.detekt.api.Finding
            import dev.detekt.api.Entity
            import dev.detekt.api.Rule
            import com.intellij.psi.PsiElement
            import com.intellij.psi.PsiNameIdentifierOwner
            
            fun Rule.f(element: PsiNameIdentifierOwner, element2: PsiElement) {
                report(Finding(Entity.from(element.nameIdentifier ?: element2), "message"))
            }
        """.trimIndent()
        val findings = rule.lint(code, compile = false)
        assertThat(findings).singleElement()
            .hasMessage("Recommended to use Entity.atName(element) instead.")
            .hasTextLocation("from")
    }

    @Test
    fun `should not report calls where from is used with multiple parameters`() {
        val code = """
            import dev.detekt.api.Finding
            import dev.detekt.api.Entity
            import dev.detekt.api.Rule
            import com.intellij.psi.PsiElement
            import com.intellij.psi.PsiNameIdentifierOwner
            
            fun Rule.f(element: PsiNameIdentifierOwner, element2: PsiElement) {
                report(Finding(Entity.from(element.nameIdentifier ?: element2, 0), "message"))
            }
        """.trimIndent()
        val findings = rule.lint(code, compile = false)
        assertThat(findings).isEmpty()
    }
}
