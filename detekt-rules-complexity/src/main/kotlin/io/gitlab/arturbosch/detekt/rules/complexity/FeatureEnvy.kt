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
        id = "FeatureEnvy",
        severity = Severity.Maintainability,
        description = "Feature Envy Description.",
        debt = Debt.TWENTY_MINS
    )

    /*
    TODO: Offenen Fragen:

    RuleVisitor.kt getArgument
    Macht faxen, wenn man eigene Debts definiert:
    if (text.startsWith(name, true) && type.size == 2) {
    type.size ist nicht == 2, weil
    val type = text.split('.')
    Hier gibts aber nichts zu splitten -> type.size ist immer 1

     Float Configuration Values scheinen nicht möglich zu sein, wieso?

Exception in thread "main" java.lang.IllegalStateException: 0.33f is neither a literal nor a constant
	at io.gitlab.arturbosch.detekt.generator.collection.ConfigurationCollector$DefaultValueSupport.toDefaultValue(ConfigurationCollector.kt:144)
	at io.gitlab.arturbosch.detekt.generator.collection.ConfigurationCollector$DefaultValueSupport.getDefaultValue(ConfigurationCollector.kt:123)
	at io.gitlab.arturbosch.detekt.generator.collection.ConfigurationCollector.toConfiguration(ConfigurationCollector.kt:99)
	at io.gitlab.arturbosch.detekt.generator.collection.ConfigurationCollector.parseConfigurationAnnotation(ConfigurationCollector.kt:80)
	at io.gitlab.arturbosch.detekt.generator.collection.ConfigurationCollector.getConfiguration(ConfigurationCollector.kt:41)
	at io.gitlab.arturbosch.detekt.generator.collection.RuleVisitor.getRule(RuleVisitor.kt:44)
	at io.gitlab.arturbosch.detekt.generator.collection.RuleCollector.visit(RuleCollector.kt:13)
	at io.gitlab.arturbosch.detekt.generator.collection.DetektCollector.visit(DetektCollector.kt:60)
	at io.gitlab.arturbosch.detekt.generator.Generator.execute(Generator.kt:33)
	at io.gitlab.arturbosch.detekt.generator.Main.main(Main.kt:28)

     */


    @Configuration("LAA")
    private val localityOfDataAccessThreshold = 0.3f
    @Configuration("ATFD")
    private val atfdThreshold: Int by config(defaultValue = 2)
    @Configuration("FDP")
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
        val numOfForeignDataAccesses =
            getFqNamesOfAllDataAccessedInBlock(funBlock).filter {
                !localPropertyFqNames.contains(it)
            }.groupingBy {
                it
            }.eachCount().values.sum()

        // if there is not ATFD, there can't be feature envy
        if (numOfForeignDataAccesses == 0) return

        val numOfLocalDataAccesses = getFqNameToNumOfUsagesOfLocalDataInFunction(function).values.sum()

        val localityOfAttributeAccess = numOfLocalDataAccesses / numOfForeignDataAccesses.toFloat()

        if (numOfForeignDataAccesses > atfdThreshold &&
            localityOfAttributeAccess < localityOfDataAccessThreshold &&
            foreignDataProviderCount <= fdpThreshold
        ) {
            report(
                ThresholdedCodeSmell(
                    issue,
                    Entity.from(function),
                    Metric(
                        "Locality of Data access",
                        localityOfDataAccessThreshold.toDouble(),
                        localityOfDataAccessThreshold.toDouble()
                    ),
                    issue.description
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

/*
@file:Suppress
package io.gitlab.arturbosch.detekt.rules.complexity

const val BLA = "lala"

data class B(val b: Int = 18, val bb: Int = 100) {

    val test: Int? = null

    fun lala() {}
}

object LSÖFDK {
    val test = 10
    fun lalalalala(){}
}

class Test(
    val param: Int,
    param2: Int
) {
    val a = 5

    fun test(b: B) {
        val temp = 5

        b.lala()

        b.apply {
            this.b.plus(bb)
            bb.plus(a)
        }

        println(b.b)
        println(BLA)
    }
}

data class ContactInfo(
    val city: String,
    val postalCode: String,
    val street: String,
    val number: String
)

class User(val contactInfo: ContactInfo) {

    fun prettyPrintAddress() {
        val prettyAddress = buildString {
            append(contactInfo.postalCode)
            append(" ")
            append(contactInfo.city)
            append("\n")
            append(contactInfo.street)
            append(" ")
            append(contactInfo.number)
        }
        println(prettyAddress)
    }

}

data class Rectangle(val width: Int, val height: Int)

class RectangleUsageSite(val rectangle: Rectangle) {
    fun printArea() {
        val area = rectangle.width * rectangle.height
        println("The area is: \${'$'}{area}")
    }
}

data class Cube(val width: Int, val length: Int, val height: Int)

class CubeUsageSite(val cube: Cube) {
    fun printVolume() {
        val volume = cube.width * cube.length * cube.height
        println("The volume is: \${volume}")
    }
}
 */
