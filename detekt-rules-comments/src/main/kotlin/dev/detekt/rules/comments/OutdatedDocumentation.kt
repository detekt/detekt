package dev.detekt.rules.comments

import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
import dev.detekt.psi.isInternal
import dev.detekt.psi.isOverride
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
import org.jetbrains.kotlin.util.capitalizeDecapitalize.toLowerCaseAsciiOnly

/**
 * This rule will report any class, function or constructor with KDoc that does not match the declaration signature.
 * If KDoc is not present or does not contain any @param or @property tags, rule violation will not be reported.
 * By default, both type and value parameters need to be matched and declarations orders must be preserved. You can
 * turn off these features using configuration options.
 *
 * Every mismatch is reported separately, so that each finding points at a single problem of the documentation.
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
        "if true, all parameters must be documented; " +
            "if false, only documented parameters are validated against the declaration"
    )
    private val exhaustive: Boolean by config(true)

    private inline val DeclarationType.displayName
        get() = this.name.toLowerCaseAsciiOnly()

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)
        val classDeclarations = getClassDeclarations(klass)
        val overridePropertyNames = getOverridePropertyNames(klass)
        val isInternalOrPrivate = isInternalOrPrivate(klass.primaryConstructor)
        getOutdatedDocumentationViolations(klass, overridePropertyNames) {
            if (isInternalOrPrivate) {
                // only properties can be documented when the constructor is not part of the public API
                classDeclarations.filterNot { it.type == DeclarationType.PARAM }
            } else {
                classDeclarations
            }
        }.forEach { message ->
            reportFinding(klass, message)
        }
    }

    override fun visitSecondaryConstructor(constructor: KtSecondaryConstructor) {
        super.visitSecondaryConstructor(constructor)
        getOutdatedDocumentationViolations(constructor) {
            getSecondaryConstructorDeclarations(constructor)
        }.forEach { message ->
            reportFinding(constructor, message)
        }
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        getOutdatedDocumentationViolations(function) { getFunctionDeclarations(function) }.forEach { message ->
            reportFinding(function, message)
        }
    }

    private fun getClassDeclarations(klass: KtClass): List<Declaration> {
        val ctor = klass.primaryConstructor
        val constructorDeclarations = if (ctor != null) {
            getPrimaryConstructorDeclarations(ctor)
        } else {
            emptyList()
        }
        val typeParams = if (matchTypeParameters) {
            klass.typeParameters.mapNotNull { it.name.toTypedParamOrNull() }
        } else {
            emptyList()
        }
        return typeParams + constructorDeclarations
    }

    private fun getOverridePropertyNames(klass: KtClass): Set<String> =
        klass.primaryConstructor
            ?.valueParameters
            ?.filter { it.isOverride() }
            ?.mapNotNull { it.name }
            ?.toSet()
            .orEmpty()

    private fun getFunctionDeclarations(function: KtNamedFunction): List<Declaration> {
        val typeParams = if (matchTypeParameters) {
            function.typeParameters.mapNotNull { it.name.toTypedParamOrNull() }
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
            if (it.isOverride()) return@mapNotNull null
            it.name?.let { name ->
                val type = if (it.isPropertyParameter() && it.isPrivate().not()) {
                    if (allowParamOnConstructorProperties) {
                        DeclarationType.ANY
                    } else {
                        DeclarationType.PROPERTY
                    }
                } else {
                    if (it.isFunctionTypeParameter) {
                        DeclarationType.TYPED_PARAM
                    } else {
                        DeclarationType.PARAM
                    }
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
        val declaration = docTag.getSubjectName()?.let { subjectName ->
            when (docTag.knownTag) {
                KDocKnownTag.PARAM -> Declaration(subjectName, DeclarationType.PARAM)
                KDocKnownTag.PROPERTY -> Declaration(subjectName, DeclarationType.PROPERTY)
                else -> null
            }
        }
        return listOfNotNull(declaration) + findSwallowedDeclarations(docTag)
    }

    /**
     * The KDoc lexer treats a tag description consisting only of an inline code span (e.g. ``@param x `[0, 1]` ``)
     * as a code span that continues on the following lines, so subsequent tags are merged into the current
     * [KDocTag] as raw text instead of being parsed as separate tags. Recover them from the tag text.
     */
    private fun findSwallowedDeclarations(docTag: KDocTag): List<Declaration> {
        var inFencedCodeBlock = false
        return docTag.text
            .lineSequence()
            .drop(1)
            .map { it.trimStart().removePrefix("*").trim() }
            .mapNotNull { line ->
                if (line.startsWith("```")) {
                    inFencedCodeBlock = !inFencedCodeBlock
                    return@mapNotNull null
                }
                if (inFencedCodeBlock) return@mapNotNull null
                swallowedTagRegex.find(line)?.let { match ->
                    val (tagName, subjectName) = match.destructured
                    val type = if (tagName == "param") DeclarationType.PARAM else DeclarationType.PROPERTY
                    Declaration(subjectName, type)
                }
            }
            .toList()
    }

    private fun getOutdatedDocumentationViolations(
        element: KtNamedDeclaration,
        overridePropertyNames: Set<String> = emptySet(),
        elementDeclarationsProvider: () -> List<Declaration>,
    ): List<String> {
        val doc = element.docComment ?: return emptyList()
        val docDeclarations = getDocDeclarations(doc)
            .filterNot { it.name in overridePropertyNames }
        if (docDeclarations.isEmpty()) return emptyList()
        val elementDeclarations = elementDeclarationsProvider()
        return getDeclarationsMismatches(docDeclarations, elementDeclarations)
    }

    private fun isInternalOrPrivate(primaryConstructor: KtPrimaryConstructor?): Boolean {
        primaryConstructor ?: return true
        return primaryConstructor.isInternal() || primaryConstructor.isPrivate()
    }

    private fun getDeclarationsMismatches(doc: List<Declaration>, element: List<Declaration>): List<String> {
        val docNames = doc.map { it.name }
        val elementNames = element.map { it.name }
        val (docWithMissingElement, docWithPresentElement) = doc.partition { it.name !in elementNames }
        val (elementWithMissingDoc, elementWithPresentDoc) = element.partition { it.name !in docNames }

        val missingElementReasons =
            docWithMissingElement.map {
                "@${it.type.displayName} ${it.name} doesn't have corresponding public declaration."
            }
        val missingDocReasons = if (exhaustive) {
            elementWithMissingDoc.map { "Documentation of ${it.name} is missing." }
        } else {
            emptyList()
        }

        val finalWarnings = missingElementReasons + missingDocReasons

        val zippedElements = if (matchDeclarationsOrder) {
            docWithPresentElement.zip(elementWithPresentDoc)
        } else {
            docWithPresentElement.sortedBy { it.name }.zip(elementWithPresentDoc.sortedBy { it.name })
        }

        return finalWarnings + if (
            !zippedElements.all { (docItr, elementItr) ->
                declarationNameMatches(docItr, elementItr)
            }
        ) {
            listOf("Documentation elements order is mismatched with declaration.")
        } else {
            zippedElements.mapNotNull { (docItr, elementItr) ->
                getDeclarationTypeMismatchMsgOrNull(docItr, elementItr)
            }
        }
    }

    private fun getDeclarationTypeMismatchMsgOrNull(doc: Declaration, element: Declaration): String? =
        if (
            element.type == DeclarationType.ANY ||
            DeclarationType.isTypeMatches(doc.type, element.type)
        ) {
            null
        } else {
            "@${doc.type.displayName} ${doc.name} type doesn't match corresponding " +
                "declaration of type ${element.type.displayName}."
        }

    private fun declarationNameMatches(doc: Declaration, element: Declaration): Boolean = element.name == doc.name

    private fun reportFinding(element: KtNamedDeclaration, message: String) {
        report(
            Finding(
                Entity.atName(element),
                message,
            ),
        )
    }

    private fun String?.toParamOrNull(): Declaration? = this?.let { Declaration(it, DeclarationType.PARAM) }

    private fun String?.toTypedParamOrNull(): Declaration? = this?.let { Declaration(it, DeclarationType.TYPED_PARAM) }

    data class Declaration(val name: String, val type: DeclarationType)

    enum class DeclarationType {
        PARAM,
        TYPED_PARAM,
        PROPERTY,
        ANY,
        ;

        companion object {
            fun isTypeMatches(docType: DeclarationType, elementType: DeclarationType): Boolean =
                if (docType == PARAM) {
                    elementType == PARAM || elementType == TYPED_PARAM
                } else {
                    docType == elementType
                }
        }
    }
}

private val swallowedTagRegex = Regex("""^@(param|property)\s+\[?([^\s\[\]]+)]?""")
