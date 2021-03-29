package io.gitlab.arturbosch.detekt.generator.collection

import io.gitlab.arturbosch.detekt.generator.collection.exception.InvalidDocumentationException
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtElement
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

    private fun KtProperty.hasListDeclaration(): Boolean =
        anyDescendantOfType(::isListDeclaration)

    private fun KtProperty.getListDeclarationAsConfigString(): String {
        val listDeclaration = checkNotNull(findDescendantOfType(::isListDeclaration))
        return listDeclaration
            .valueArguments
            .map { "'${it.text.withoutQuotes()}'" }.toString()
    }

    private fun isListDeclaration(expression: KtCallExpression) =
        expression.referenceExpression()?.text == LIST_OF

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
        if (!hasSupportedType()) {
            invalidDocumentation {
                "Type of '$name' is not supported. " +
                    "For properties annotated with @Configuration use one of$SUPPORTED_TYPES."
            }
        }
        if (!isInitializedWithConfigDelegate()) {
            invalidDocumentation { "'$name' is not using the '$DELEGATE_NAME' delegate" }
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
        val delegateArgument = checkNotNull(
            (expression as KtCallExpression).valueArguments[0].getArgumentExpression()
        )
        val listDeclaration = delegateArgument.findDescendantOfType(::isListDeclaration)
        if (listDeclaration != null) {
            return listDeclaration.valueArguments.map {
                val value = constantsByName[it.text] ?: it.text
                "'${value.withoutQuotes()}'"
            }.toString()
        }

        val defaultValueOrConstantName = checkNotNull(
            delegateArgument.text?.withoutQuotes()
        )
        val defaultValue = constantsByName[defaultValueOrConstantName] ?: defaultValueOrConstantName
        return property.formatDefaultValueAccordingToType(defaultValue)
    }

    private val KtProperty.declaredTypeOrNull: String?
        get() = typeReference?.text

    private fun KtProperty.hasSupportedType(): Boolean {
        return declaredTypeOrNull in SUPPORTED_TYPES
    }

    private fun KtProperty.isInitializedWithConfigDelegate(): Boolean =
        delegate?.expression?.referenceExpression()?.text == DELEGATE_NAME

    private fun KtProperty.formatDefaultValueAccordingToType(value: String): String {
        val defaultValue = value.withoutQuotes()
        return when (declaredTypeOrNull) {
            TYPE_STRING -> "'$defaultValue'"
            TYPE_BOOLEAN, TYPE_INT, TYPE_LONG, TYPE_STRING_LIST -> defaultValue
            else -> error("Unable to format unexpected type '$declaredTypeOrNull'")
        }
    }

    private val KtPropertyDelegate.property: KtProperty
        get() = parent as KtProperty

    private val KtPropertyDelegate.propertyName: String
        get() = checkNotNull(property.name)

    private fun KtElement.invalidDocumentation(message: () -> String): Nothing {
        throw InvalidDocumentationException("[${containingFile.name}] ${message.invoke()}")
    }

    companion object {
        private const val DELEGATE_NAME = "config"
        private const val LIST_OF = "listOf"
        private const val TYPE_STRING = "String"
        private const val TYPE_BOOLEAN = "Boolean"
        private const val TYPE_INT = "Int"
        private const val TYPE_LONG = "Long"
        private const val TYPE_STRING_LIST = "List<String>"
        private val SUPPORTED_TYPES = listOf(TYPE_STRING, TYPE_BOOLEAN, TYPE_INT, TYPE_LONG, TYPE_STRING_LIST)
    }
}
