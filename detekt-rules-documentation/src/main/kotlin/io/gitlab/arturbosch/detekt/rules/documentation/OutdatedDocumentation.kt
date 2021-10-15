package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.kdoc.parser.KDocKnownTag
import org.jetbrains.kotlin.kdoc.psi.api.KDoc
import org.jetbrains.kotlin.kdoc.psi.impl.KDocSection
import org.jetbrains.kotlin.kdoc.psi.impl.KDocTag
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import org.jetbrains.kotlin.psi.psiUtil.isPropertyParameter

class OutdatedDocumentation(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Maintainability,
        "KDoc should match actual function or class signature",
        Debt.TWENTY_MINS
    )

    override fun visitClass(klass: KtClass) {
        reportIfDocumentationIsOutdated(klass) { getClassDeclarations(klass) }
        super.visitClass(klass)
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        reportIfDocumentationIsOutdated(function) { getFunctionDeclarations(function) }
        super.visitNamedFunction(function)
    }

    private fun getClassDeclarations(element: KtClass): Declarations {
        val constructor = element.primaryConstructor
        return if (constructor != null) {
            getPrimaryConstructorDeclarations(constructor)
        } else {
            Declarations()
        }
    }

    private fun getFunctionDeclarations(element: KtNamedFunction): Declarations {
        val params = element.valueParameters.mapNotNull { it.name }
        return Declarations(params = params.toMutableList())
    }

    private fun getPrimaryConstructorDeclarations(constructor: KtPrimaryConstructor): Declarations {
        val params = constructor.valueParameters.filter { !it.isPropertyParameter() }.mapNotNull { it.name }
        val props = constructor.valueParameters.filter { it.isPropertyParameter() }.mapNotNull { it.name }
        return Declarations(
            params = params.toMutableList(),
            props = props.toMutableList()
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
            val elementDeclarations = elementDeclarationsProvider()
            val docDeclarations = getDocDeclarations(doc)
            if (docDeclarations != elementDeclarations) {
                reportCodeSmell(element)
            }
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
