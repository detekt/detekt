package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Metric
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.Configuration
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
 * This rule reports methods with Feature Envy.
 * Methods with Feature Envy access a lot of data of other classes, which might be a sign that the method was displaced
 * and should be moved to another class.
 *
 * Methods with Feature Envy uses more than a few attributes of other classes, use more attributes from other classes
 * than its own and the used foreign data belongs to few other classes.
 *
 * For more information see: Object-Oriented Metrics in Practice - Michele Lanza & Radu Marinescu
 */
@RequiresTypeResolution
class FeatureEnvy(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        id = "FeatureEnvy",
        severity = Severity.Maintainability,
        description = "Feature Envy is a sign of a displaced method",
        debt = Debt.TWENTY_MINS
    )

    // @Configuration("Locality of Attribute Accesses - The threshold-ratio of attributes used from the methods class to attributes used from other classes")
    private val localityOfDataAccessThreshold = 0.33f

    @Configuration("The maximum number of attributes from other classes that may be used.")
    private val atfdThreshold: Int by config(defaultValue = 2)

    @Configuration(
        "The minimum number of foreign data providers which " +
            "must be used so that no Feature Envy is present."
    )
    private val fdpThreshold: Int by config(defaultValue = 2)

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
        val numOfForeignDataAccesses = getFqNamesOfAllDataAccessedInBlock(funBlock).filter {
            !localPropertyFqNames.contains(it)
        }.groupingBy {
            it
        }.eachCount().values.sum()

        // if there is not ATFD, there can't be feature envy
        if (numOfForeignDataAccesses == 0) return

        val numOfLocalDataAccesses = getFqNameToNumOfUsagesOfLocalDataInFunction(function).values.sum()

        val localityOfAttributeAccess = numOfLocalDataAccesses / numOfForeignDataAccesses.toFloat()

        if (
            numOfForeignDataAccesses > atfdThreshold &&
            localityOfAttributeAccess < localityOfDataAccessThreshold &&
            foreignDataProviderCount <= fdpThreshold
        ) {
            report(
                ThresholdedCodeSmell(
                    issue = issue,
                    entity = Entity.from(function),
                    metric = Metric(
                        type = "Locality of Data access",
                        value = localityOfDataAccessThreshold.toDouble(),
                        threshold = localityOfDataAccessThreshold.toDouble()
                    ),
                    message = issue.description
                )
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
                container.body?.let { it.properties.mapNotNull { property -> property.fqName } }.orEmpty()
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
        return function.findDescendantOfType<KtBlockExpression>()?.collectDescendantsOfType<KtNameReferenceExpression>()
            ?.filter {
                it.parent !is KtDotQualifiedExpression
            }
            ?.mapNotNull { reference ->
                // This removes all ReferenceExpressions to functions etc.
                reference.getResolvedCall(bindingContext)?.resultingDescriptor?.referencedProperty
            }
            ?.filter {
                // Constants not considered ATFD
                !it.isConst
            }
            ?.map {
                it.fqNameSafe
            }
            ?.filter {
                localProperties.contains(it)
            }
            ?.groupingBy {
                it
            }
            ?.eachCount()
            .orEmpty()
    }
}
