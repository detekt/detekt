package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.rules.isInternal
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
import org.jetbrains.kotlin.psi.psiUtil.PsiChildRange
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import org.jetbrains.kotlin.psi.psiUtil.isPrivate
import org.jetbrains.kotlin.psi.psiUtil.isPropertyParameter
import org.jetbrains.kotlin.utils.addToStdlib.ifTrue

/**
 * This rule will report any class, function or constructor with KDoc that does not match the declaration signature.
 * If KDoc is not present or does not contain any @param or @property tags, rule violation will not be reported.
 * By default, both type and value parameters need to be matched and declarations orders must be preserved. You can
 * turn off these features using configuration options.
 *
 * <noncompliant>
 * /**
 *  * @param someParam
 *  * @property someProp
 *  */
 * class MyClass(otherParam: String, val otherProp: String)
 *
 * /**
 *  * @param T
 *  * @param someParam
 *  */
 * fun <T, S> myFun(someParam: String)
 *
 * </noncompliant>
 *
 * <compliant>
 * /**
 *  * @param someParam
 *  * @property someProp
 *  */
 * class MyClass(someParam: String, val someProp: String)
 *
 * /**
 *  * @param T
 *  * @param S
 *  * @param someParam
 *  */
 * fun <T, S> myFun(someParam: String)
 *
 * </compliant>
 */
@Suppress("TooManyFunctions")
class OutdatedDocumentation(config: Config) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        "KDoc comments should match the actual function or class signature",
    )

    @Configuration("if type parameters should be matched")
    private val matchTypeParameters: Boolean by config(true)

    @Configuration("if the order of declarations should be preserved")
    private val matchDeclarationsOrder: Boolean by config(true)

    @Configuration("if we allow constructor parameters to be marked as @param instead of @property")
    private val allowParamOnConstructorProperties: Boolean by config(false)

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)
        val classDeclarations = getClassDeclarations(klass)
        (
            isDocumentationOutdated(klass) { classDeclarations } &&
                (
                    // checking below only if constructor in internal or private
                    isInternalOrPrivate(klass.primaryConstructor).not() ||
                        isDocumentationOutdated(klass) {
                            // case when only property can be documented
                            classDeclarations.filterNot { it.type == DeclarationType.PARAM }
                        }
                    )
            )
            .ifTrue {
                reportCodeSmell(klass)
            }
    }

    override fun visitSecondaryConstructor(constructor: KtSecondaryConstructor) {
        super.visitSecondaryConstructor(constructor)
        isDocumentationOutdated(constructor) { getSecondaryConstructorDeclarations(constructor) }.ifTrue {
            reportCodeSmell(constructor)
        }
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        isDocumentationOutdated(function) { getFunctionDeclarations(function) }.ifTrue {
            reportCodeSmell(function)
        }
    }

    private fun getClassDeclarations(klass: KtClass): List<Declaration> {
        val ctor = klass.primaryConstructor
        val constructorDeclarations = if (ctor != null) getPrimaryConstructorDeclarations(ctor) else emptyList()
        val typeParams = if (matchTypeParameters) {
            klass.typeParameters.mapNotNull { it.name.toParamOrNull() }
        } else {
            emptyList()
        }
        return typeParams + constructorDeclarations
    }

    private fun getFunctionDeclarations(function: KtNamedFunction): List<Declaration> {
        val typeParams = if (matchTypeParameters) {
            function.typeParameters.mapNotNull { it.name.toParamOrNull() }
        } else {
            emptyList()
        }
        val valueParams = function.valueParameters.mapNotNull { it.name.toParamOrNull() }
        return typeParams + valueParams
    }

    private fun getPrimaryConstructorDeclarations(constructor: KtPrimaryConstructor): List<Declaration> {
        return getDeclarationsForValueParameters(constructor.valueParameters)
    }

    private fun getSecondaryConstructorDeclarations(constructor: KtSecondaryConstructor): List<Declaration> {
        return getDeclarationsForValueParameters(constructor.valueParameters)
    }

    private fun getDeclarationsForValueParameters(valueParameters: List<KtParameter>): List<Declaration> {
        return valueParameters.mapNotNull {
            it.name?.let { name ->
                val type = if (it.isPropertyParameter() && it.isPrivate().not()) {
                    if (allowParamOnConstructorProperties) {
                        DeclarationType.ANY
                    } else {
                        DeclarationType.PROPERTY
                    }
                } else {
                    DeclarationType.PARAM
                }
                Declaration(name, type)
            }
        }
    }

    private fun getDocDeclarations(doc: KDoc): List<Declaration> {
        return processDocChildren(doc.allChildren)
    }

    private fun processDocChildren(children: PsiChildRange): List<Declaration> {
        return children
            .map {
                when (it) {
                    is KDocSection -> processDocChildren(it.allChildren)
                    is KDocTag -> processDocTag(it)
                    else -> emptyList()
                }
            }
            .fold(emptyList()) { acc, declarations -> acc + declarations }
    }

    @Suppress("ElseCaseInsteadOfExhaustiveWhen")
    private fun processDocTag(docTag: KDocTag): List<Declaration> {
        val knownTag = docTag.knownTag
        val subjectName = docTag.getSubjectName() ?: return emptyList()
        return when (knownTag) {
            KDocKnownTag.PARAM -> listOf(Declaration(subjectName, DeclarationType.PARAM))
            KDocKnownTag.PROPERTY -> listOf(Declaration(subjectName, DeclarationType.PROPERTY))
            else -> emptyList()
        }
    }

    private fun isDocumentationOutdated(
        element: KtNamedDeclaration,
        elementDeclarationsProvider: () -> List<Declaration>,
    ): Boolean {
        val doc = element.docComment ?: return false
        val docDeclarations = getDocDeclarations(doc)
        return if (docDeclarations.isNotEmpty()) {
            val elementDeclarations = elementDeclarationsProvider()
            !declarationsMatch(docDeclarations, elementDeclarations)
        } else {
            false
        }
    }

    private fun isInternalOrPrivate(primaryConstructor: KtPrimaryConstructor?): Boolean {
        primaryConstructor ?: return false
        return primaryConstructor.isInternal() || primaryConstructor.isPrivate()
    }

    private fun declarationsMatch(
        doc: List<Declaration>,
        element: List<Declaration>,
    ): Boolean {
        if (doc.size != element.size) {
            return false
        }

        val zippedElements = if (matchDeclarationsOrder) {
            doc.zip(element)
        } else {
            doc.sortedBy { it.name }.zip(element.sortedBy { it.name })
        }

        return zippedElements.all { (doc, element) -> declarationMatches(doc, element) }
    }

    private fun declarationMatches(doc: Declaration, element: Declaration): Boolean {
        return element.name == doc.name && (element.type == DeclarationType.ANY || element.type == doc.type)
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

    private fun String?.toParamOrNull(): Declaration? {
        return this?.let { Declaration(it, DeclarationType.PARAM) }
    }

    data class Declaration(
        val name: String,
        val type: DeclarationType,
    )

    enum class DeclarationType {
        PARAM,
        PROPERTY,
        ANY
    }
}
