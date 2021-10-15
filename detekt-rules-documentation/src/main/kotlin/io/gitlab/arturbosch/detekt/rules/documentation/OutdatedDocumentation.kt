package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import org.jetbrains.kotlin.kdoc.parser.KDocKnownTag
import org.jetbrains.kotlin.kdoc.psi.api.KDoc
import org.jetbrains.kotlin.kdoc.psi.impl.KDocSection
import org.jetbrains.kotlin.kdoc.psi.impl.KDocTag
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtSecondaryConstructor
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import org.jetbrains.kotlin.psi.psiUtil.isPropertyParameter

/**
 * This rule will report any class, function or constructor with KDoc that does not match declaration signature.
 * If KDoc is not present or does not contain any @param or @property tags, rule violation will not be reported.
 * By default, both type and value parameters need to be matched and declarations orders must be preserved. You can
 * turn off these features using configuration options.
 */
@Suppress("TooManyFunctions")
class OutdatedDocumentation(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Maintainability,
        "KDoc should match actual function or class signature",
        Debt.TWENTY_MINS
    )

    @Configuration("if type parameters should be matched")
    private val matchTypeParameters: Boolean by config(true)

    @Configuration("if the order of declarations should be preserved")
    private val matchDeclarationsOrder: Boolean by config(true)

    override fun visitClass(klass: KtClass) {
        reportIfDocumentationIsOutdated(klass) { getClassDeclarations(klass) }
        super.visitClass(klass)
    }

    override fun visitSecondaryConstructor(constructor: KtSecondaryConstructor) {
        reportIfDocumentationIsOutdated(constructor) { getSecondaryConstructorDeclarations(constructor) }
        super.visitSecondaryConstructor(constructor)
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        reportIfDocumentationIsOutdated(function) { getFunctionDeclarations(function) }
        super.visitNamedFunction(function)
    }

    private fun getClassDeclarations(klass: KtClass): Declarations {
        val constructor = klass.primaryConstructor
        return if (constructor != null) {
            val constructorDeclarations = getPrimaryConstructorDeclarations(constructor)
            val typeParams = if (matchTypeParameters) {
                klass.typeParameters.mapNotNull { it.name }
            } else listOf()
            return Declarations(
                params = typeParams + constructorDeclarations.params,
                props = constructorDeclarations.props
            )
        } else {
            Declarations()
        }
    }

    private fun getFunctionDeclarations(function: KtNamedFunction): Declarations {
        val typeParams = if (matchTypeParameters) {
            function.typeParameters.mapNotNull { it.name }
        } else listOf()
        val valueParams = function.valueParameters.mapNotNull { it.name }
        val params = typeParams + valueParams
        return Declarations(params = params.toMutableList())
    }

    private fun getPrimaryConstructorDeclarations(constructor: KtPrimaryConstructor): Declarations {
        return getDeclarationsForValueParameters(constructor.valueParameters)
    }

    private fun getSecondaryConstructorDeclarations(constructor: KtSecondaryConstructor): Declarations {
        return getDeclarationsForValueParameters(constructor.valueParameters)
    }

    private fun getDeclarationsForValueParameters(valueParameters: List<KtParameter>): Declarations {
        val valueParams = valueParameters.filter { !it.isPropertyParameter() }.mapNotNull { it.name }
        val props = valueParameters.filter { it.isPropertyParameter() }.mapNotNull { it.name }
        return Declarations(
            params = valueParams,
            props = props
        )
    }

    private fun getDocDeclarations(doc: KDoc): Declarations {
        val result = MutableDeclarations()
        for (child in doc.allChildren) {
            if (child is KDocSection) {
                processDocSection(child, result)
            }
        }
        return result.toDeclarations()
    }

    private fun processDocSection(section: KDocSection, result: MutableDeclarations) {
        for (child in section.allChildren) {
            if (child is KDocSection) {
                processDocSection(child, result)
            } else if (child is KDocTag) {
                processDocTag(child, result)
            }
        }
    }

    private fun processDocTag(docTag: KDocTag, result: MutableDeclarations) {
        val knownTag = docTag.knownTag
        val subjectName = docTag.getSubjectName()
        if (subjectName != null) {
            when (knownTag) {
                KDocKnownTag.PARAM -> result.params.add(subjectName)
                KDocKnownTag.PROPERTY -> result.props.add(subjectName)
                else -> Unit
            }
        }
    }

    private fun reportIfDocumentationIsOutdated(
        element: KtNamedDeclaration,
        elementDeclarationsProvider: () -> Declarations
    ) {
        val doc = element.docComment
        if (doc != null) {
            val docDeclarations = getDocDeclarations(doc)
            if (docDeclarations.params.isNotEmpty() || docDeclarations.props.isNotEmpty()) {
                val elementDeclarations = elementDeclarationsProvider()
                if (!declarationsMatch(docDeclarations, elementDeclarations)) {
                    reportCodeSmell(element)
                }
            }
        }
    }

    private fun declarationsMatch(doc: Declarations, element: Declarations): Boolean {
        return if (matchDeclarationsOrder) {
            doc == element
        } else {
            doc.props.sorted() == element.props.sorted() &&
                doc.params.sorted() == element.params.sorted()
        }
    }

    private fun reportCodeSmell(element: KtNamedDeclaration) {
        report(
            CodeSmell(
                issue,
                Entity.atName(element),
                "Documentation of ${element.nameAsSafeName} is outdated"
            )
        )
    }

    private data class MutableDeclarations(
        val params: MutableList<String> = mutableListOf(),
        val props: MutableList<String> = mutableListOf()
    ) {
        fun toDeclarations(): Declarations {
            return Declarations(
                params = params,
                props = props
            )
        }
    }

    private data class Declarations(
        val params: List<String> = listOf(),
        val props: List<String> = listOf()
    )
}
