package dev.detekt.rules.style

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.lexer.KtModifierKeywordToken
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
import org.jetbrains.kotlin.lexer.KtTokens.VALUE_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.VARARG_KEYWORD
import org.jetbrains.kotlin.psi.KtModifierList
import org.jetbrains.kotlin.psi.psiUtil.allChildren

/**
 * This rule reports cases in the code where modifiers are not in the correct order. The default modifier order is
 * taken from: [Modifiers order](https://kotlinlang.org/docs/coding-conventions.html#modifiers-order)
 *
 * <noncompliant>
 * lateinit internal val str: String
 * </noncompliant>
 *
 * <compliant>
 * internal lateinit val str: String
 * </compliant>
 */
@ActiveByDefault(since = "1.0.0")
class ModifierOrder(config: Config) :
    Rule(config, "Modifiers are not in the correct order. Consider to reorder these modifiers.") {

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
        VALUE_KEYWORD,
        INFIX_KEYWORD,
        OPERATOR_KEYWORD,
        DATA_KEYWORD
    )

    override fun visitModifierList(list: KtModifierList) {
        super.visitModifierList(list)

        val modifiers = list.allChildren
            .mapNotNull { it.node.elementType as? KtModifierKeywordToken }
            .toList()

        val sortedModifiers = modifiers.sortedWith(compareBy { order.indexOf(it) })

        if (modifiers != sortedModifiers) {
            val modifierString = sortedModifiers.joinToString(" ") { it.value }

            report(Finding(Entity.from(list), "Modifier order should be: $modifierString"))
        }
    }
}
