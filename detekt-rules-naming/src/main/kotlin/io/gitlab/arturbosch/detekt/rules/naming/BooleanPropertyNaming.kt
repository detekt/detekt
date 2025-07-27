package io.gitlab.arturbosch.detekt.rules.naming

import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.api.config
import io.gitlab.arturbosch.detekt.rules.isConstant
import io.gitlab.arturbosch.detekt.rules.isOverride
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty

/**
 * Reports boolean property names that do not follow the specified naming convention.
 *
 * <noncompliant>
 * val progressBar: Boolean = true
 * </noncompliant>
 *
 * <compliant>
 * val hasProgressBar: Boolean = true
 * </compliant>
 */
class BooleanPropertyNaming(config: Config) :
    Rule(
        config,
        "Boolean property name should follow the naming convention set in detekt's configuration."
    ),
    RequiresAnalysisApi {

    @Configuration("naming pattern")
    private val allowedPattern: Regex by config("^(is|has|are)", String::toRegex)

    override fun visitParameter(parameter: KtParameter) {
        super.visitParameter(parameter)

        if (parameter.hasValOrVar()) {
            validateDeclaration(parameter)
        }
    }

    override fun visitProperty(property: KtProperty) {
        super.visitProperty(property)

        validateDeclaration(property)
    }

    private fun validateDeclaration(declaration: KtCallableDeclaration) {
        val name = declaration.name ?: return
        val isBooleanType = declaration.isKotlinBooleanType() || declaration.isJavaBooleanType()
        val isNonConstantBooleanType = isBooleanType && !declaration.isConstant()

        if (isNonConstantBooleanType && !name.contains(allowedPattern) && !declaration.isOverride()) {
            report(reportFinding(declaration, name))
        }
    }

    private fun reportFinding(
        declaration: KtCallableDeclaration,
        name: String,
    ): Finding {
        val description = "Boolean property name should match a $allowedPattern pattern."
        return Finding(
            Entity.atName(declaration),
            message = "$description Actual name is $name"
        )
    }

    private fun KtCallableDeclaration.isKotlinBooleanType(): Boolean = analyze(this) {
        returnType.isBooleanType
    }

    private fun KtCallableDeclaration.isJavaBooleanType(): Boolean = analyze(this) {
        returnType.isClassType(ClassId(FqName("java.lang"), Name.identifier("Boolean")))
    }
}
