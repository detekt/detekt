package io.gitlab.arturbosch.detekt.authors

import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
internal class UseNamedLocationSpec(private val env: KotlinCoreEnvironment) {

    private val rule = UseNamedLocation()

    @Test
    fun `should not report calls when there's no name involved`() {
        val code = """
            import io.gitlab.arturbosch.detekt.api.CodeSmell
            import io.gitlab.arturbosch.detekt.api.Entity
            import io.gitlab.arturbosch.detekt.api.Rule
            import org.jetbrains.kotlin.com.intellij.psi.PsiElement
            
            fun Rule.f(element: PsiElement) {
                report(CodeSmell(issue, Entity.from(element), "message"))
            }
        """.trimIndent()
        val findings = rule.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `should not report calls when atName is already used`() {
        val code = """
            import io.gitlab.arturbosch.detekt.api.CodeSmell
            import io.gitlab.arturbosch.detekt.api.Entity
            import io.gitlab.arturbosch.detekt.api.Rule
            import org.jetbrains.kotlin.psi.KtNamedDeclaration
            
            fun Rule.f(element: KtNamedDeclaration) {
                report(CodeSmell(issue, Entity.atName(element), "message"))
            }
        """.trimIndent()
        val findings = rule.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `should report calls where nameIdentifier is used directly with bang`() {
        val code = """
            import io.gitlab.arturbosch.detekt.api.CodeSmell
            import io.gitlab.arturbosch.detekt.api.Entity
            import io.gitlab.arturbosch.detekt.api.Rule
            import org.jetbrains.kotlin.com.intellij.psi.PsiNameIdentifierOwner
            
            fun Rule.f(element: PsiNameIdentifierOwner) {
                report(CodeSmell(issue, Entity.from(element.nameIdentifier!!), "message"))
            }
        """.trimIndent()
        val findings = rule.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1).hasTextLocations("from")
    }

    @Test
    fun `should report calls where nameIdentifier is used directly with double-bang`() {
        val code = """
            import io.gitlab.arturbosch.detekt.api.CodeSmell
            import io.gitlab.arturbosch.detekt.api.Entity
            import io.gitlab.arturbosch.detekt.api.Rule
            import org.jetbrains.kotlin.com.intellij.psi.PsiNameIdentifierOwner
            
            fun Rule.f(element: PsiNameIdentifierOwner) {
                report(CodeSmell(issue, Entity.from(element.nameIdentifier!!!!), "message"))
            }
        """.trimIndent()
        val findings = rule.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1).hasTextLocations("from")
    }

    @Test
    fun `should report calls where nameIdentifier is used with elvis with same fallback`() {
        val code = """
            import io.gitlab.arturbosch.detekt.api.CodeSmell
            import io.gitlab.arturbosch.detekt.api.Entity
            import io.gitlab.arturbosch.detekt.api.Rule
            import org.jetbrains.kotlin.com.intellij.psi.PsiNameIdentifierOwner
            
            fun Rule.f(element: PsiNameIdentifierOwner) {
                report(CodeSmell(issue, Entity.from(element.nameIdentifier ?: element), "message"))
            }
        """.trimIndent()
        val findings = rule.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1).hasTextLocations("from")
    }

    @Test
    fun `should report calls where nameIdentifier is used with elvis with other fallback`() {
        val code = """
            import io.gitlab.arturbosch.detekt.api.CodeSmell
            import io.gitlab.arturbosch.detekt.api.Entity
            import io.gitlab.arturbosch.detekt.api.Rule
            import org.jetbrains.kotlin.com.intellij.psi.PsiElement
            import org.jetbrains.kotlin.com.intellij.psi.PsiNameIdentifierOwner
            
            fun Rule.f(element: PsiNameIdentifierOwner, element2: PsiElement) {
                report(CodeSmell(issue, Entity.from(element.nameIdentifier ?: element2), "message"))
            }
        """.trimIndent()
        val findings = rule.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1).hasTextLocations("from")
    }

    @Test
    fun `should not report calls where from is used with multiple parameters`() {
        val code = """
            import io.gitlab.arturbosch.detekt.api.CodeSmell
            import io.gitlab.arturbosch.detekt.api.Entity
            import io.gitlab.arturbosch.detekt.api.Rule
            import org.jetbrains.kotlin.com.intellij.psi.PsiElement
            import org.jetbrains.kotlin.com.intellij.psi.PsiNameIdentifierOwner
            
            fun Rule.f(element: PsiNameIdentifierOwner, element2: PsiElement) {
                report(CodeSmell(issue, Entity.from(element.nameIdentifier ?: element2, 0), "message"))
            }
        """.trimIndent()
        val findings = rule.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }
}
