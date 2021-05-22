package io.gitlab.arturbosch.detekt.generator.collection

import io.gitlab.arturbosch.detekt.generator.collection.ConfigurationCollector.ConfigWithFallbackSupport.FALLBACK_DELEGATE_NAME
import io.gitlab.arturbosch.detekt.generator.collection.ConfigurationCollector.ConfigWithFallbackSupport.getFallbackPropertyName
import io.gitlab.arturbosch.detekt.generator.collection.ConfigurationCollector.ConfigWithFallbackSupport.isFallbackConfigDelegate
import io.gitlab.arturbosch.detekt.generator.collection.ConfigurationCollector.ConfigWithFallbackSupport.isUsingInvalidFallbackReference
import io.gitlab.arturbosch.detekt.generator.collection.exception.InvalidDocumentationException
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtLambdaArgument
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtPropertyDelegate
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.referenceExpression
import io.gitlab.arturbosch.detekt.api.internal.Configuration as ConfigAnnotation

class ConfigurationCollector {

    private val constantsByName = mutableMapOf<String, String>()
    private val properties = mutableListOf<KtProperty>()

    fun getConfiguration(): List<Configuration> {
        return properties.mapNotNull { it.parseConfigurationAnnotation() }
    }

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

    private fun resolveConstantOrNull(prop: KtProperty): Pair<String, String>? {
        if (prop.isVar) return null

        val propertyName = checkNotNull(prop.name)
        val constantOrNull = prop.getConstantValueAsStringOrNull()

        return constantOrNull?.let { propertyName to it }
    }

    private fun KtProperty.getConstantValueAsStringOrNull(): String? {
        if (hasListDeclaration()) {
            return getListDeclarationAsConfigString()
        }

        return findDescendantOfType<KtConstantExpression>()?.text
            ?: findDescendantOfType<KtStringTemplateExpression>()?.text?.withoutQuotes()
    }

    private fun KtProperty.getListDeclarationAsConfigString(): String {
        return getListDeclaration()
            .valueArguments
            .map { "'${it.text.withoutQuotes()}'" }.toString()
    }

    private fun KtProperty.parseConfigurationAnnotation(): Configuration? {
        if (isAnnotatedWith(ConfigAnnotation::class)) return toConfiguration()
        if (isInitializedWithConfigDelegate()) {
            invalidDocumentation {
                "'$name' is using the config delegate but is not annotated with @Configuration"
            }
        }
        return null
    }

    private fun KtProperty.toConfiguration(): Configuration {
        if (!isInitializedWithConfigDelegate()) {
            invalidDocumentation { "'$name' is not using one of the config property delegates ($DELEGATE_NAMES)" }
        }
        if (isFallbackConfigDelegate()) {
            val fallbackPropertyName = getFallbackPropertyName()
            if (isUsingInvalidFallbackReference(properties, fallbackPropertyName)) {
                invalidDocumentation { "The fallback property '$fallbackPropertyName' is missing for property '$name'" }
            }
        }

        val propertyName: String = checkNotNull(name)
        val deprecationMessage = firstAnnotationParameterOrNull(Deprecated::class)
        val description: String = firstAnnotationParameter(ConfigAnnotation::class)
        val defaultValueAsString = delegate?.getDefaultValueAsString()
            ?: invalidDocumentation { "'$propertyName' is not a delegated property" }

        return Configuration(
            name = propertyName,
            description = description,
            defaultValue = defaultValueAsString,
            deprecated = deprecationMessage
        )
    }

    private fun KtPropertyDelegate.getDefaultValueAsString(): String {
        val defaultValueExpression = getDefaultValueExpression()
        val listDeclarationForDefault = defaultValueExpression.getListDeclarationOrNull()
        if (listDeclarationForDefault != null) {
            return listDeclarationForDefault.valueArguments.map {
                val value = constantsByName[it.text] ?: it.text
                "'${value.withoutQuotes()}'"
            }.toString()
        }

        val defaultValueOrConstantName = checkNotNull(
            defaultValueExpression.text?.withoutQuotes()
        )
        val defaultValue = constantsByName[defaultValueOrConstantName] ?: defaultValueOrConstantName
        return property.formatDefaultValueAccordingToType(defaultValue)
    }

    private fun KtPropertyDelegate.getDefaultValueExpression(): KtExpression {
        val arguments = (expression as KtCallExpression).valueArguments.filterNot { it is KtLambdaArgument }
        if (arguments.size == 1) {
            return checkNotNull(arguments[0].getArgumentExpression())
        }
        val defaultArgument = arguments
            .find { it.getArgumentName()?.text == DEFAULT_VALUE_ARGUMENT_NAME }
            ?: if (property.isFallbackConfigDelegate()) arguments[1] else arguments.first()

        return checkNotNull(defaultArgument.getArgumentExpression())
    }

    private object ConfigWithFallbackSupport {
        const val FALLBACK_DELEGATE_NAME = "configWithFallback"
        private const val FALLBACK_ARGUMENT_NAME = "fallbackPropertyName"

        fun KtProperty.isFallbackConfigDelegate(): Boolean =
            delegate?.expression?.referenceExpression()?.text == FALLBACK_DELEGATE_NAME

        fun KtProperty.getFallbackPropertyName(): String {
            val callExpression = delegate?.expression as KtCallExpression
            val arguments = callExpression.valueArguments
            val fallbackArgument = arguments
                .find { it.getArgumentName()?.text == FALLBACK_ARGUMENT_NAME }
                ?: arguments.first()
            return checkNotNull(fallbackArgument.getArgumentExpression()?.text?.withoutQuotes())
        }

        fun isUsingInvalidFallbackReference(properties: List<KtProperty>, fallbackPropertyName: String) =
            properties
                .filter { it.isInitializedWithConfigDelegate() }
                .none { it.name == fallbackPropertyName }
    }

    companion object {
        private const val SIMPLE_DELEGATE_NAME = "config"
        private val DELEGATE_NAMES = listOf(SIMPLE_DELEGATE_NAME, FALLBACK_DELEGATE_NAME)
        private const val DEFAULT_VALUE_ARGUMENT_NAME = "defaultValue"
        private const val LIST_OF = "listOf"
        private const val EMPTY_LIST = "emptyList"
        private val LIST_CREATORS = setOf(LIST_OF, EMPTY_LIST)

        private const val TYPE_STRING = "String"
        private const val TYPE_REGEX = "Regex"
        private const val TYPE_SPLIT_PATTERN = "SplitPattern"
        private val TYPES_THAT_NEED_QUOTATION_FOR_DEFAULT = listOf(TYPE_STRING, TYPE_REGEX, TYPE_SPLIT_PATTERN)

        private val KtPropertyDelegate.property: KtProperty
            get() = parent as KtProperty

        private val KtProperty.declaredTypeOrNull: String?
            get() = typeReference?.text

        private fun KtElement.getListDeclaration(): KtCallExpression =
            checkNotNull(getListDeclarationOrNull())

        private fun KtElement.getListDeclarationOrNull(): KtCallExpression? =
            findDescendantOfType { it.isListDeclaration() }

        private fun KtProperty.isInitializedWithConfigDelegate(): Boolean =
            delegate?.expression?.referenceExpression()?.text in DELEGATE_NAMES

        private fun KtProperty.formatDefaultValueAccordingToType(value: String): String {
            val defaultValue = value.withoutQuotes()
            val needsQuotes = declaredTypeOrNull in TYPES_THAT_NEED_QUOTATION_FOR_DEFAULT
            return if (needsQuotes) "'$defaultValue'" else defaultValue
        }

        private fun KtProperty.hasListDeclaration(): Boolean =
            anyDescendantOfType<KtCallExpression> { it.isListDeclaration() }

        private fun KtCallExpression.isListDeclaration() =
            referenceExpression()?.text in LIST_CREATORS

        private fun KtElement.invalidDocumentation(message: () -> String): Nothing {
            throw InvalidDocumentationException("[${containingFile.name}] ${message.invoke()}")
        }
    }
}
