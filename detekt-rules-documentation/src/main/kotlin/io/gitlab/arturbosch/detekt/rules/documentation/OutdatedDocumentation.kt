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
import org.jetbrains.kotlin.psi.psiUtil.PsiChildRange
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import org.jetbrains.kotlin.psi.psiUtil.isPropertyParameter

/**
 * This rule will report any class, function or constructor with KDoc that does not match declaration signature.
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
class OutdatedDocumentation(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Maintainability,
        "KDoc should match actual function or class signature",
        Debt.TEN_MINS
    )

    @Configuration("if type parameters should be matched")
    private val matchTypeParameters: Boolean by config(true)

    @Configuration("if the order of declarations should be preserved")
    private val matchDeclarationsOrder: Boolean by config(true)

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)
        reportIfDocumentationIsOutdated(klass) { getClassDeclarations(klass) }
    }

    override fun visitSecondaryConstructor(constructor: KtSecondaryConstructor) {
        super.visitSecondaryConstructor(constructor)
        reportIfDocumentationIsOutdated(constructor) { getSecondaryConstructorDeclarations(constructor) }
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        reportIfDocumentationIsOutdated(function) { getFunctionDeclarations(function) }
    }

    private fun getClassDeclarations(klass: KtClass): Declarations {
        val ctor = klass.primaryConstructor
        return if (ctor != null) {
            val constructorDeclarations = getPrimaryConstructorDeclarations(ctor)
            val typeParams = if (matchTypeParameters) {
                klass.typeParameters.mapNotNull { it.name }
            } else emptyList()
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
        } else emptyList()
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
        val params = valueParameters.filter { !it.isPropertyParameter() }.mapNotNull { it.name }
        val props = valueParameters.filter { it.isPropertyParameter() }.mapNotNull { it.name }
        return Declarations(
            params = params,
            props = props
        )
    }

    private fun getDocDeclarations(doc: KDoc): Declarations {
        return processDocChildren(doc.allChildren)
    }

    private fun processDocChildren(children: PsiChildRange): Declarations {
        return children
            .map {
                when (it) {
                    is KDocSection -> processDocChildren(it.allChildren)
                    is KDocTag -> processDocTag(it)
                    else -> Declarations()
                }
            }
            .fold(Declarations()) { acc, declarations -> acc + declarations }
    }

    private fun processDocTag(docTag: KDocTag): Declarations {
        val knownTag = docTag.knownTag
        val subjectName = docTag.getSubjectName()
        return if (subjectName != null) {
            when (knownTag) {
                KDocKnownTag.PARAM -> Declarations(params = listOf(subjectName))
                KDocKnownTag.PROPERTY -> Declarations(props = listOf(subjectName))
                else -> Declarations()
            }
        } else {
            Declarations()
        }
    }

    private fun reportIfDocumentationIsOutdated(
        element: KtNamedDeclaration,
        elementDeclarationsProvider: () -> Declarations
    ) {
        val doc = element.docComment ?: return
        val docDeclarations = getDocDeclarations(doc)
        if (docDeclarations.params.isNotEmpty() || docDeclarations.props.isNotEmpty()) {
            val elementDeclarations = elementDeclarationsProvider()
            if (!declarationsMatch(docDeclarations, elementDeclarations)) {
                reportCodeSmell(element)
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

    private data class Declarations(
        val params: List<String> = emptyList(),
        val props: List<String> = emptyList()
    ) {
        operator fun plus(declarations: Declarations): Declarations {
            return Declarations(
                params = params + declarations.params,
                props = props + declarations.props
            )
        }
    }
}
