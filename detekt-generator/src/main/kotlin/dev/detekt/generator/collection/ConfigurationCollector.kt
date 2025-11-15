package dev.detekt.generator.collection

import dev.detekt.api.ValueWithReason
import dev.detekt.api.valuesWithReason
import dev.detekt.generator.collection.ConfigurationCollector.ConfigWithAndroidVariantsSupport.ANDROID_VARIANTS_DELEGATE_NAME
import dev.detekt.generator.collection.ConfigurationCollector.ConfigWithAndroidVariantsSupport.DEFAULT_ANDROID_VALUE_ARGUMENT_NAME
import dev.detekt.generator.collection.ConfigurationCollector.ConfigWithAndroidVariantsSupport.isAndroidVariantConfigDelegate
import dev.detekt.generator.collection.ConfigurationCollector.ConfigWithFallbackSupport.FALLBACK_DELEGATE_NAME
import dev.detekt.generator.collection.ConfigurationCollector.ConfigWithFallbackSupport.checkUsingInvalidFallbackReference
import dev.detekt.generator.collection.ConfigurationCollector.ConfigWithFallbackSupport.isFallbackConfigDelegate
import dev.detekt.generator.collection.ConfigurationCollector.DefaultValueSupport.getAndroidDefaultValue
import dev.detekt.generator.collection.ConfigurationCollector.DefaultValueSupport.getDefaultValue
import dev.detekt.generator.collection.ConfigurationCollector.DefaultValueSupport.toDefaultValueIfLiteral
import dev.detekt.generator.collection.ConfigurationCollector.StringListSupport.getListDefaultOrNull
import dev.detekt.generator.collection.ConfigurationCollector.StringListSupport.hasListDeclaration
import dev.detekt.generator.collection.ConfigurationCollector.ValuesWithReasonSupport.getValuesWithReasonDefaultOrNull
import dev.detekt.generator.collection.ConfigurationCollector.ValuesWithReasonSupport.hasValuesWithReasonDeclaration
import dev.detekt.generator.collection.exception.InvalidDocumentationException
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtCallableReferenceExpression
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.referenceExpression
import dev.detekt.api.Configuration as ConfigAnnotation

class ConfigurationCollector {

    private val constantsByName = mutableMapOf<String, DefaultValue>()
    private val properties = mutableListOf<KtProperty>()

    fun getConfigurations(): List<Configuration> = properties.mapNotNull { it.parseConfigurationAnnotation() }

    fun addProperty(prop: KtProperty) {
        properties.add(prop)
    }

    fun addCompanion(aRuleCompanion: KtObjectDeclaration) {
        constantsByName.putAll(
            aRuleCompanion
                .collectDescendantsOfType<KtProperty>()
                .mapNotNull(::resolveConstantOrNull)
        )
    }

    private fun resolveConstantOrNull(prop: KtProperty): Pair<String, DefaultValue>? {
        if (prop.isVar) return null

        val propertyName = checkNotNull(prop.name)
        val constantOrNull = prop.getConstantValue()

        return constantOrNull?.let { propertyName to it }
    }

    private fun KtProperty.getConstantValue(): DefaultValue? {
        if (hasValuesWithReasonDeclaration()) {
            return getValuesWithReasonDefaultOrNull()
                ?: invalidDocumentation { "Invalid declaration of values with reasons default for property '$text'" }
        }
        if (hasListDeclaration()) {
            return getListDefaultOrNull(emptyMap())
                ?: invalidDocumentation { "Invalid declaration of string list default for property '$text'" }
        }

        return findDescendantOfType<KtConstantExpression>()?.toDefaultValueIfLiteral()
            ?: findDescendantOfType<KtStringTemplateExpression>()?.toDefaultValueIfLiteral()
    }

    private fun KtProperty.parseConfigurationAnnotation(): Configuration? =
        when {
            isAnnotatedWith(ConfigAnnotation::class) -> toConfiguration()
            isInitializedWithConfigDelegate() -> invalidDocumentation {
                "'$name' is using the config delegate but is not annotated with @Configuration"
            }
            else -> null
        }

    private fun KtProperty.toConfiguration(): Configuration {
        if (!isInitializedWithConfigDelegate()) {
            invalidDocumentation { "'$name' is not using one of the config property delegates ($DELEGATE_NAMES)" }
        }

        if (isFallbackConfigDelegate()) {
            checkUsingInvalidFallbackReference(properties)
        }

        val propertyName: String = checkNotNull(name)
        val deprecationMessage = firstAnnotationParameterOrNull(Deprecated::class)
        val description: String = firstAnnotationParameter(ConfigAnnotation::class)
        val defaultValue = getDefaultValue(constantsByName)
        val defaultAndroidValue = getAndroidDefaultValue(constantsByName)

        return Configuration(
            name = propertyName,
            description = description,
            defaultValue = defaultValue,
            defaultAndroidValue = defaultAndroidValue,
            deprecated = deprecationMessage,
        )
    }

    private object DefaultValueSupport {
        fun KtProperty.getDefaultValue(constantsByName: Map<String, DefaultValue>): DefaultValue {
            val defaultValueArgument = getValueArgument(
                name = DEFAULT_VALUE_ARGUMENT_NAME,
                actionForPositionalMatch = { arguments ->
                    when {
                        isFallbackConfigDelegate() -> arguments[1]
                        isAndroidVariantConfigDelegate() -> arguments[0]
                        else -> arguments[0]
                    }
                }
            ) ?: invalidDocumentation { "'$name' is not a delegated property" }
            return checkNotNull(defaultValueArgument.getArgumentExpression()).toDefaultValue(constantsByName)
        }

        fun KtProperty.getAndroidDefaultValue(constantsByName: Map<String, DefaultValue>): DefaultValue? {
            val defaultValueArgument = getValueArgument(
                name = DEFAULT_ANDROID_VALUE_ARGUMENT_NAME,
                actionForPositionalMatch = { arguments ->
                    when {
                        isAndroidVariantConfigDelegate() -> arguments[1]
                        else -> null
                    }
                }
            )
            return defaultValueArgument?.getArgumentExpression()?.toDefaultValue(constantsByName)
        }

        fun KtExpression.toDefaultValue(constantsByName: Map<String, DefaultValue>): DefaultValue =
            getValuesWithReasonDefaultOrNull()
                ?: getListDefaultOrNull(constantsByName)
                ?: toDefaultValueIfLiteral()
                ?: constantsByName[text.withoutQuotes()]
                ?: error("$text is neither a literal nor a constant")

        fun KtExpression.toDefaultValueIfLiteral(): DefaultValue? = createDefaultValueIfLiteral(text)
    }

    private object ConfigWithFallbackSupport {
        const val FALLBACK_DELEGATE_NAME = "configWithFallback"
        private const val FALLBACK_ARGUMENT_NAME = "fallbackProperty"

        fun KtProperty.isFallbackConfigDelegate(): Boolean =
            delegate?.expression?.referenceExpression()?.text == FALLBACK_DELEGATE_NAME

        fun KtProperty.checkUsingInvalidFallbackReference(properties: List<KtProperty>) {
            val fallbackPropertyReference = getValueArgument(
                name = FALLBACK_ARGUMENT_NAME,
                actionForPositionalMatch = { it.first() }
            )?.getReferenceIdentifierOrNull()

            val fallbackProperty = properties.find { it.name == fallbackPropertyReference }
            if (fallbackProperty == null || !fallbackProperty.isInitializedWithConfigDelegate()) {
                invalidDocumentation {
                    "The fallback property '$fallbackPropertyReference' of property '$name' " +
                        "must also be defined using a config property delegate "
                }
            }
        }

        private fun KtValueArgument.getReferenceIdentifierOrNull(): String? =
            (getArgumentExpression() as? KtCallableReferenceExpression)
                ?.callableReference
                ?.getIdentifier()
                ?.text
    }

    private object ConfigWithAndroidVariantsSupport {
        const val ANDROID_VARIANTS_DELEGATE_NAME = "configWithAndroidVariants"
        const val DEFAULT_ANDROID_VALUE_ARGUMENT_NAME = "defaultAndroidValue"

        fun KtProperty.isAndroidVariantConfigDelegate(): Boolean =
            delegate?.expression?.referenceExpression()?.text == ANDROID_VARIANTS_DELEGATE_NAME
    }

    private object ValuesWithReasonSupport {
        private const val VALUES_WITH_REASON_FACTORY_METHOD = "valuesWithReason"

        fun KtElement.getValuesWithReasonDefaultOrNull(): DefaultValue? =
            getValuesWithReasonDeclarationOrNull()
                ?.valueArguments
                ?.map(::toValueWithReason)
                ?.let { DefaultValue.of(valuesWithReason(it)) }

        fun KtElement.getValuesWithReasonDeclarationOrNull(): KtCallExpression? =
            findDescendantOfType { it.isValuesWithReasonDeclaration() }

        fun KtCallExpression.isValuesWithReasonDeclaration(): Boolean =
            referenceExpression()?.text == VALUES_WITH_REASON_FACTORY_METHOD

        fun KtProperty.hasValuesWithReasonDeclaration(): Boolean =
            anyDescendantOfType<KtCallExpression> { it.isValuesWithReasonDeclaration() }

        private fun toValueWithReason(arg: KtValueArgument): ValueWithReason {
            val keyToValue = arg.children.first() as? KtBinaryExpression
            return keyToValue?.let {
                ValueWithReason(
                    value = it.left?.text?.withoutQuotes()
                        ?: error("left side of value with reason argument is null"),
                    reason = it.right?.text?.withoutQuotes()
                        ?: error("right side of value with reason argument is null")
                )
            } ?: error("invalid value argument '${arg.text}'")
        }
    }

    private object StringListSupport {
        private const val LIST_OF = "listOf"
        private const val EMPTY_LIST = "emptyList"
        private val LIST_CREATORS = setOf(LIST_OF, EMPTY_LIST)

        fun KtElement.getListDefaultOrNull(constantsByName: Map<String, DefaultValue>): DefaultValue? =
            getListDeclarationOrNull()?.valueArguments?.map {
                (constantsByName[it.text]?.getPlainValue() ?: it.text.withoutQuotes())
            }?.let { DefaultValue.of(it) }

        fun KtElement.getListDeclarationOrNull(): KtCallExpression? = findDescendantOfType { it.isListDeclaration() }

        fun KtProperty.hasListDeclaration(): Boolean = anyDescendantOfType<KtCallExpression> { it.isListDeclaration() }

        fun KtCallExpression.isListDeclaration() = referenceExpression()?.text in LIST_CREATORS
    }

    companion object {
        private const val SIMPLE_DELEGATE_NAME = "config"
        private val DELEGATE_NAMES = listOf(
            SIMPLE_DELEGATE_NAME,
            FALLBACK_DELEGATE_NAME,
            ANDROID_VARIANTS_DELEGATE_NAME
        )
        private const val DEFAULT_VALUE_ARGUMENT_NAME = "defaultValue"

        private fun KtProperty.isInitializedWithConfigDelegate(): Boolean =
            delegate?.expression?.referenceExpression()?.text in DELEGATE_NAMES

        private fun KtElement.invalidDocumentation(message: () -> String): Nothing =
            throw InvalidDocumentationException("[${containingFile.name}] ${message.invoke()}")

        private fun KtProperty.getValueArgument(
            name: String,
            actionForPositionalMatch: (List<KtValueArgument>) -> KtValueArgument?,
        ): KtValueArgument? {
            val callExpression = delegate?.expression as? KtCallExpression ?: return null
            val arguments = callExpression.valueArguments
            return arguments.find { it.getArgumentName()?.text == name } ?: actionForPositionalMatch(arguments)
        }
    }
}
