package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.MultiRule
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtConstructor
import org.jetbrains.kotlin.psi.KtEnumEntry
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtPackageDirective
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtVariableDeclaration
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType

class NamingRules(config: Config = Config.empty) : MultiRule() {

    private val variableNamingRule = VariableNaming(config)
    private val variableMinNameLengthRule = VariableMinLength(config)
    private val variableMaxNameLengthRule = VariableMaxLength(config)
    private val topLevelPropertyRule = TopLevelPropertyNaming(config)
    private val objectConstantNamingRule = ObjectPropertyNaming(config)
    private val nonBooleanPropertyPrefixedWithIsRule = NonBooleanPropertyPrefixedWithIs(config)
    private val packageNamingRule = PackageNaming(config)
    private val classOrObjectNamingRule = ClassNaming(config)
    private val enumEntryNamingRule = EnumNaming(config)
    private val functionNamingRule = FunctionNaming(config)
    private val functionMaxNameLengthRule = FunctionMaxLength(config)
    private val functionMinNameLengthRule = FunctionMinLength(config)
    private val forbiddenClassNameRule = ForbiddenClassName(config)
    private val constructorParameterNamingRule = ConstructorParameterNaming(config)
    private val functionParameterNamingRule = FunctionParameterNaming(config)

    override val rules: List<Rule> = listOf(
            variableNamingRule,
            variableMinNameLengthRule,
            variableMaxNameLengthRule,
            topLevelPropertyRule,
            objectConstantNamingRule,
            nonBooleanPropertyPrefixedWithIsRule,
            packageNamingRule,
            classOrObjectNamingRule,
            enumEntryNamingRule,
            functionNamingRule,
            functionMaxNameLengthRule,
            functionMinNameLengthRule,
            forbiddenClassNameRule,
            constructorParameterNamingRule,
            functionParameterNamingRule
    )

    override fun visitPackageDirective(directive: KtPackageDirective) {
        super.visitPackageDirective(directive)
        packageNamingRule.runIfActive { visitPackageDirective(directive) }
    }

    override fun visitNamedDeclaration(declaration: KtNamedDeclaration) {
        if (declaration.nameAsSafeName.isSpecial) {
            return
        }
        declaration.nameIdentifier?.parent?.javaClass?.let {
            when (declaration) {
                is KtProperty -> handleProperty(declaration)
                is KtNamedFunction -> handleFunction(declaration)
                is KtEnumEntry -> enumEntryNamingRule.runIfActive { visitEnumEntry(declaration) }
                is KtClassOrObject -> handleClassOrObject(declaration)
                is KtParameter -> handleParameter(declaration)
            }
        }
        super.visitNamedDeclaration(declaration)
    }

    private fun handleClassOrObject(declaration: KtClassOrObject) {
        classOrObjectNamingRule.runIfActive { visitClassOrObject(declaration) }
        forbiddenClassNameRule.runIfActive { visitClassOrObject(declaration) }
    }

    private fun handleFunction(declaration: KtNamedFunction) {
        functionNamingRule.runIfActive { visitNamedFunction(declaration) }
        functionMaxNameLengthRule.runIfActive { visitNamedFunction(declaration) }
        functionMinNameLengthRule.runIfActive { visitNamedFunction(declaration) }
    }

    private fun handleProperty(declaration: KtProperty) {
        variableMaxNameLengthRule.runIfActive { visitProperty(declaration) }
        variableMinNameLengthRule.runIfActive { visitProperty(declaration) }
        nonBooleanPropertyPrefixedWithIsRule.runIfActive { visitProperty(declaration) }

        when {
            declaration.isTopLevel -> topLevelPropertyRule.runIfActive { visitProperty(declaration) }
            declaration.withinObjectDeclaration() -> objectConstantNamingRule.runIfActive { visitProperty(declaration) }
            else -> variableNamingRule.runIfActive { visitProperty(declaration) }
        }
    }

    private fun handleParameter(declaration: KtParameter) {
        when (declaration.ownerFunction) {
            is KtConstructor<*> -> constructorParameterNamingRule.runIfActive { visitParameter(declaration) }
            is KtNamedFunction -> functionParameterNamingRule.runIfActive { visitParameter(declaration) }
        }
    }

    private fun KtVariableDeclaration.withinObjectDeclaration(): Boolean =
            this.getNonStrictParentOfType<KtObjectDeclaration>() != null
}
