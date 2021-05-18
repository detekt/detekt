package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.identifierName
import io.gitlab.arturbosch.detekt.rules.isConstant
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.typeBinding.createTypeBindingForReturnType

/**
 * Reports when a boolean property doesn't have one of the following prefixes:
 * is/has/should/need/noNeed/was/are/may/can/had/for/with.
 *
 * @requiresTypeResolution
 */
class BooleanPropertyPrefixedWithAllowedWords(config: Config = Config.empty) : Rule(config) {

    private val description = "Boolean property name should starts with " +
        "'is/has/should/need/noNeed/was/are/may/can/had/for/with' prefix."

    private val regex = Regex("^(is|has|should|need|noNeed|was|are|may|can|had|for|with)")

    override val issue = Issue(
        javaClass.simpleName, Severity.CodeSmell,
        description,
        Debt.FIVE_MINS
    )

    override fun visitParameter(parameter: KtParameter) {
        super.visitParameter(parameter)
        val name = parameter.identifierName()

        if (name in listOf(VALUE_CONDITION, VALUE_PREDICATE, VALUE_IT, UNUSED_PARAM_NAME)) {
            return
        }

        if (parameter.hasValOrVar()) {
            validateDeclaration(parameter)
        }
    }

    override fun visitProperty(property: KtProperty) {
        super.visitProperty(property)
        if (property.isConstant()) return

        validateDeclaration(property)
    }

    private fun validateDeclaration(declaration: KtCallableDeclaration) {
        if (bindingContext == BindingContext.EMPTY) {
            return
        }

        val name = declaration.identifierName()
        val typeName = getTypeName(declaration)

        if (
            (typeName == KOTLIN_BOOLEAN_TYPE_NAME || typeName == JAVA_BOOLEAN_TYPE_NAME) && !name.contains(regex)
        ) {
            report(reportCodeSmell(declaration, name, typeName.toString()))
        }
    }

    private fun reportCodeSmell(
        declaration: KtCallableDeclaration,
        name: String,
        typeName: String
    ): CodeSmell {
        return CodeSmell(
            issue,
            Entity.from(declaration),
            message = "$description Actual type of $name: $typeName"
        )
    }

    private fun getTypeName(parameter: KtCallableDeclaration): String? {
        return parameter.createTypeBindingForReturnType(bindingContext)
            ?.type
            ?.getJetTypeFqName(false)
    }

    companion object {
        const val KOTLIN_BOOLEAN_TYPE_NAME = "kotlin.Boolean"
        const val JAVA_BOOLEAN_TYPE_NAME = "java.lang.Boolean"
        const val VALUE_CONDITION = "condition"
        const val VALUE_PREDICATE = "predicate"
        const val VALUE_IT = "it"
        const val UNUSED_PARAM_NAME = "_"
    }
}
