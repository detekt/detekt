package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.lexer.KtTokens.ABSTRACT_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.ACTUAL_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.ANNOTATION_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.COMPANION_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.CONST_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.DATA_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.ENUM_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.EXPECT_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.EXTERNAL_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.FINAL_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.FUN_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.INFIX_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.INLINE_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.INNER_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.INTERNAL_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.LATEINIT_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.OPEN_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.OPERATOR_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.OVERRIDE_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.PRIVATE_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.PROTECTED_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.PUBLIC_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.SEALED_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.SUSPEND_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.TAILREC_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.VARARG_KEYWORD
import org.jetbrains.kotlin.psi.KtModifierList
import org.jetbrains.kotlin.psi.psiUtil.allChildren

/**
 * This rule reports cases in the code where modifiers are not in the correct order. The default modifier order is
 * taken from: http://kotlinlang.org/docs/reference/coding-conventions.html#modifiers
 *
 * <noncompliant>
 * lateinit internal private val str: String
 * </noncompliant>
 *
 * <compliant>
 * private internal lateinit val str: String
 * </compliant>
 *
 * @active since v1.0.0
 */
class ModifierOrder(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(javaClass.simpleName,
            Severity.Style,
            "Modifiers are not in the correct order.",
            Debt.FIVE_MINS)

    // subset of KtTokens.MODIFIER_KEYWORDS_ARRAY
    private val order = arrayOf(
            PUBLIC_KEYWORD, PROTECTED_KEYWORD, PRIVATE_KEYWORD, INTERNAL_KEYWORD,
            EXPECT_KEYWORD, ACTUAL_KEYWORD,
            FINAL_KEYWORD, OPEN_KEYWORD, ABSTRACT_KEYWORD, SEALED_KEYWORD, CONST_KEYWORD,
            EXTERNAL_KEYWORD,
            OVERRIDE_KEYWORD,
            LATEINIT_KEYWORD,
            TAILREC_KEYWORD,
            VARARG_KEYWORD,
            SUSPEND_KEYWORD,
            INNER_KEYWORD,
            ENUM_KEYWORD, ANNOTATION_KEYWORD, FUN_KEYWORD,
            COMPANION_KEYWORD,
            INLINE_KEYWORD,
            INFIX_KEYWORD,
            OPERATOR_KEYWORD,
            DATA_KEYWORD
    )

    override fun visitModifierList(list: KtModifierList) {
        super.visitModifierList(list)

        val modifiers = list.allChildren
            .filter { it !is PsiWhiteSpace }
            .toList()

        val sortedModifiers = modifiers.sortedWith(compareBy { order.indexOf(it.node.elementType) })

        if (modifiers != sortedModifiers) {
            val modifierString = sortedModifiers.joinToString(" ") { it.text }

            report(CodeSmell(Issue(javaClass.simpleName, Severity.Style,
                    "Modifier order should be: $modifierString", Debt(mins = 1)), Entity.from(list),
                    "Modifier order should be: $modifierString"))
        }
    }
}
