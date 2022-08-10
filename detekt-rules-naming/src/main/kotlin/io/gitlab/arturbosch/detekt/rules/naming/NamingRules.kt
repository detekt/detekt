package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty

class NamingRules(config: Config = Config.empty) :
    @Suppress("DEPRECATION")
    io.gitlab.arturbosch.detekt.api.MultiRule() {

    private val variableMinNameLengthRule = VariableMinLength(config)
    private val variableMaxNameLengthRule = VariableMaxLength(config)
    private val nonBooleanPropertyPrefixedWithIsRule = NonBooleanPropertyPrefixedWithIs(config)
    private val functionMaxNameLengthRule = FunctionMaxLength(config)
    private val functionMinNameLengthRule = FunctionMinLength(config)

    override val rules: List<Rule> = listOf(
        variableMinNameLengthRule,
        variableMaxNameLengthRule,
        nonBooleanPropertyPrefixedWithIsRule,
        functionMaxNameLengthRule,
        functionMinNameLengthRule,
    )

    override fun visitNamedDeclaration(declaration: KtNamedDeclaration) {
        if (declaration.nameAsSafeName.isSpecial) {
            return
        }
        if (declaration.nameIdentifier?.parent?.javaClass != null) {
            when (declaration) {
                is KtProperty -> handleProperty(declaration)
                is KtNamedFunction -> handleFunction(declaration)
            }
        }
        super.visitNamedDeclaration(declaration)
    }

    private fun handleFunction(declaration: KtNamedFunction) {
        functionMaxNameLengthRule.runIfActive { visitNamedFunction(declaration) }
        functionMinNameLengthRule.runIfActive { visitNamedFunction(declaration) }
    }

    private fun handleProperty(declaration: KtProperty) {
        variableMaxNameLengthRule.runIfActive { visitProperty(declaration) }
        variableMinNameLengthRule.runIfActive { visitProperty(declaration) }
        nonBooleanPropertyPrefixedWithIsRule.runIfActive { visitProperty(declaration) }
    }
}
