package dev.detekt.rules.comments

import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
import dev.detekt.psi.isInternal
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

/**
 * This rule will report any class, function or constructor with KDoc that does not match the declaration signature.
 * If KDoc is not present or does not contain any @param or @property tags, rule violation will not be reported.
 * By default, both type and value parameters need to be matched and declarations orders must be preserved. You can
 * turn off these features using configuration options.
 *
 * Set `exhaustive` to `false` to only check that documented parameters are valid without requiring all
 * parameters to be documented. This is useful when documentation is used sparingly and only where it is really needed.
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
class OutdatedDocumentation(config: Config) :
    Rule(config, "KDoc comments should match the actual function or class signature") {

    @Configuration("if type parameters should be matched")
    private val matchTypeParameters: Boolean by config(true)

    @Configuration("if the order of declarations should be preserved")
    private val matchDeclarationsOrder: Boolean by config(true)

    @Configuration("if we allow constructor parameters to be marked as @param instead of @property")
    private val allowParamOnConstructorProperties: Boolean by config(false)

    @Configuration(
        "if true, all parameters must be documented; if false, only documented parameters are validated against the declaration"
    )
    private val exhaustive: Boolean by config(true)

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)
        val classDeclarations = getClassDeclarations(klass)
        val reason = findDocumentationMismatch(klass) { classDeclarations }
        if (reason != null && (
                isInternalOrPrivate(klass.primaryConstructor).not() ||
                    findDocumentationMismatch(klass) {
                        classDeclarations.filterNot { it.type == DeclarationType.PARAM }
                    } != null
                )
        ) {
            reportFinding(klass, reason)
        }
    }

    override fun visitSecondaryConstructor(constructor: KtSecondaryConstructor) {
        super.visitSecondaryConstructor(constructor)
        findDocumentationMismatch(constructor) { getSecondaryConstructorDeclarations(constructor) }?.let {
            reportFinding(constructor, it)
        }
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        findDocumentationMismatch(function) { getFunctionDeclarations(function) }?.let {
            reportFinding(function, it)
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

    private fun getPrimaryConstructorDeclarations(constructor: KtPrimaryConstructor): List<Declaration> =
        getDeclarationsForValueParameters(constructor.valueParameters)

    private fun getSecondaryConstructorDeclarations(constructor: KtSecondaryConstructor): List<Declaration> =
        getDeclarationsForValueParameters(constructor.valueParameters)

    private fun getDeclarationsForValueParameters(valueParameters: List<KtParameter>): List<Declaration> =
        valueParameters.mapNotNull {
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

    private fun getDocDeclarations(doc: KDoc): List<Declaration> = processDocChildren(doc.allChildren)

    private fun processDocChildren(children: PsiChildRange): List<Declaration> =
        children
            .map {
                when (it) {
                    is KDocSection -> processDocChildren(it.allChildren)
                    is KDocTag -> processDocTag(it)
                    else -> emptyList()
                }
            }
            .fold(emptyList()) { acc, declarations -> acc + declarations }

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

    private fun findDocumentationMismatch(
        element: KtNamedDeclaration,
        elementDeclarationsProvider: () -> List<Declaration>,
    ): String? {
        val doc = element.docComment ?: return null
        val docDeclarations = getDocDeclarations(doc)
        if (docDeclarations.isEmpty()) return null
        val elementDeclarations = elementDeclarationsProvider()
        return findMismatchReason(docDeclarations, elementDeclarations)
    }

    private fun isInternalOrPrivate(primaryConstructor: KtPrimaryConstructor?): Boolean {
        primaryConstructor ?: return false
        return primaryConstructor.isInternal() || primaryConstructor.isPrivate()
    }

    private fun findMismatchReason(doc: List<Declaration>, element: List<Declaration>): String? {
        val invalidDocs = doc.filter { docDecl ->
            element.none { elementDecl -> declarationMatches(docDecl, elementDecl) }
        }

        val orderMismatch = matchDeclarationsOrder && doc.map { docDecl ->
            element.indexOfFirst { elementDecl -> declarationMatches(docDecl, elementDecl) }
        }.zipWithNext().any { (a, b) -> a >= b }

        val undocumented = if (exhaustive && doc.size != element.size) {
            element.filter { elementDecl ->
                doc.none { docDecl -> declarationMatches(docDecl, elementDecl) }
            }
        } else {
            emptyList()
        }

        return when {
            invalidDocs.isNotEmpty() -> {
                val names = invalidDocs.joinToString {
                    "'${it.name}'"
                }
                "documented parameters $names are not present in the declaration"
            }

            orderMismatch ->
                "order of documented parameters does not match the declaration order"

            undocumented.isNotEmpty() -> {
                val names = undocumented.joinToString {
                    "'${it.name}'"
                }
                "parameters $names are not documented"
            }

            else -> null
        }
    }

    private fun declarationMatches(doc: Declaration, element: Declaration): Boolean =
        element.name == doc.name && (element.type == DeclarationType.ANY || element.type == doc.type)

    private fun reportFinding(element: KtNamedDeclaration, reason: String) {
        report(
            Finding(
                Entity.atName(element),
                "Documentation of ${element.nameAsSafeName} is outdated: $reason"
            )
        )
    }

    private fun String?.toParamOrNull(): Declaration? = this?.let { Declaration(it, DeclarationType.PARAM) }

    data class Declaration(val name: String, val type: DeclarationType)

    enum class DeclarationType {
        PARAM,
        PROPERTY,
        ANY,
    }
}
