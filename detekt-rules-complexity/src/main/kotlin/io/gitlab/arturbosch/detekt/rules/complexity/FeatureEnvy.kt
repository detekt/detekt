package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import org.jetbrains.kotlin.descriptors.impl.referencedProperty
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.getValueParameters
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.getOwnerForEffectiveDispatchReceiverParameter

/**
 * This rule is about feature envy :)
 *
 * <noncompliant>
 * </noncompliant>
 *
 * <compliant>
 * </compliant>
 */
@RequiresTypeResolution
class FeatureEnvy(config: Config = Config.empty) : Rule(config) {

    // Does not work with extension functions yet
    // Does not work with functions outside of classes
    // Does not work with functions without block body

    override val issue = Issue(
        "FeatureEnvy", Severity.Maintainability, "Feature Envy Description.", Debt(hours = 1)
    )

    override fun visitNamedFunction(function: KtNamedFunction) {
        if (bindingContext == BindingContext.EMPTY) {
            return
        }

        val funBlock = function.findDescendantOfType<KtBlockExpression>() ?: return
        val containerFqName = function.containingClassOrObject?.fqName ?: return

        val localPropertyFqNames: List<FqName> = getFqNamesOfLocalPropertiesOfClassFunIsIn(function)

        // this is the FDP measure
        val foreignDataProviderCount = getForeignDataProvidersUsedInBlock(funBlock, containerFqName).count()

        // this is the ATFD measure
        val numOfForeignDataAccesses =
            getFqNamesOfAllDataAccessedInBlock(funBlock).filter {
                !localPropertyFqNames.contains(it)
            }.groupingBy {
                it
            }.eachCount().values.sum()

        // if there is not ATFD, there can't be feature envy
        if(numOfForeignDataAccesses == 0) return

        val numOfLocalDataAccesses = getFqNameToNumOfUsagesOfLocalDataInFunction(function).values.sum()

        val localityOfAttributeAccess = numOfLocalDataAccesses / numOfForeignDataAccesses.toFloat()

        if (numOfForeignDataAccesses > 2 &&
            localityOfAttributeAccess < 1f / 3f &&
            foreignDataProviderCount <= 2
        ) {
            report(
                CodeSmell(issue, Entity.from(function), issue.description)
            )
        }
    }

    private fun getFqNamesOfLocalPropertiesOfClassFunIsIn(function: KtNamedFunction): List<FqName> =
        when (val container = function.containingClassOrObject) {
            is KtClass -> {
                val propertiesFqNames = container.getProperties().mapNotNull { it.fqName }
                val valueParamsFqNames = container.getValueParameters().mapNotNull { it.fqName }
                propertiesFqNames.toMutableList().plus(valueParamsFqNames)
            }

            is KtObjectDeclaration -> {
                container.body?.let { it.properties.mapNotNull { property -> property.fqName } } ?: listOf()
            }

            else -> {
                emptyList()
            }
        }

    private fun getForeignDataProvidersUsedInBlock(block: KtBlockExpression, containerName: FqName): List<FqName> =
        block.collectDescendantsOfType<KtNameReferenceExpression>().mapNotNull { reference ->
            // This removes all ReferenceExpressions to functions etc.
            reference.getResolvedCall(bindingContext)?.resultingDescriptor?.referencedProperty
        }.filter {
            // Constants not considered ATFD
            !it.isConst
        }.map {
            it.getOwnerForEffectiveDispatchReceiverParameter()?.fqNameSafe
        }.filter {
            it != containerName
        }.filterNotNull().distinct()

    private fun getFqNamesOfAllDataAccessedInBlock(block: KtBlockExpression): List<FqName> =
        block.collectDescendantsOfType<KtNameReferenceExpression>().mapNotNull { reference ->
            // This removes all ReferenceExpressions to functions etc.
            reference.getResolvedCall(bindingContext)?.resultingDescriptor?.referencedProperty
        }.filter {
            // Constants not considered ATFD
            !it.isConst
        }.map {
            it.fqNameSafe
        }

    private fun getFqNameToNumOfUsagesOfLocalDataInFunction(function: KtNamedFunction): Map<FqName, Int> {
        val localProperties = getFqNamesOfLocalPropertiesOfClassFunIsIn(function)
        return function.findDescendantOfType<KtBlockExpression>()?.let { block ->
            block.collectDescendantsOfType<KtNameReferenceExpression>().filter {
                it.parent !is KtDotQualifiedExpression
            }.mapNotNull { reference ->
                // This removes all ReferenceExpressions to functions etc.
                reference.getResolvedCall(bindingContext)?.resultingDescriptor?.referencedProperty
            }.filter {
                // Constants not considered ATFD
                !it.isConst
            }.map {
                it.fqNameSafe
            }.filter {
                localProperties.contains(it)
            }.groupingBy {
                it
            }.eachCount()
        } ?: emptyMap()
    }
}
