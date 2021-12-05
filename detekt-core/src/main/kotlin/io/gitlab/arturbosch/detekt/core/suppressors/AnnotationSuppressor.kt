package io.gitlab.arturbosch.detekt.core.suppressors

import io.gitlab.arturbosch.detekt.api.ConfigAware
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.rules.fqNameOrNull
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.resolve.BindingContext

internal class AnnotationSuppressor(
    root: KtFile,
    private val annotations: List<Regex>,
    private val bindingContext: BindingContext
) : Suppressor {

    private val resolvedAnnotations = root.importList?.run {
        imports
            .asSequence()
            .filterNot { it.isAllUnder }
            .mapNotNull { it.importedFqName?.asString() }
            .map { it.substringAfterLast('.') to it }
            .toMap()
    }.orEmpty()

    override fun shouldSuppress(finding: Finding): Boolean {
        val element = finding.entity.ktElement
        return element != null && shouldSuppress(element, annotations, bindingContext)
    }

    private fun shouldSuppress(
        element: KtElement,
        annotations: List<Regex>,
        bindingContext: BindingContext
    ): Boolean {
        return element.isAnnotatedWith(annotations, bindingContext)
    }

    @Suppress("ReturnCount")
    private fun KtElement.isAnnotatedWith(annotationNames: Iterable<Regex>, bindingContext: BindingContext): Boolean {
        if (this is KtAnnotated) {
            val references = annotationEntries.mapNotNull { it.typeReference }
            if (
                references.any { typeReference ->
                    (typeReference.text in annotationNames) ||
                        (resolvedAnnotations[typeReference.text]?.let { it in annotationNames } == true)
                }
            ) {
                return true
            } else if (bindingContext != BindingContext.EMPTY) {
                if (references.any { it.fqNameOrNull(bindingContext)?.toString() in annotationNames }) {
                    return true
                }
            }
        }
        return getStrictParentOfType<KtAnnotated>()?.isAnnotatedWith(annotationNames, bindingContext) ?: false
    }

    private fun KtTypeReference.fqNameOrNull(bindingContext: BindingContext): FqName? {
        return bindingContext[BindingContext.TYPE, this]?.fqNameOrNull()
    }

    @Suppress("UnusedPrivateMember")
    private operator fun Iterable<Regex>.contains(a: String?): Boolean {
        if (a == null) return false
        return any { it.matches(a) }
    }
}

internal fun annotationSuppressorFactory(
    file: KtFile,
    rule: ConfigAware,
    bindingContext: BindingContext
): Suppressor? {
    val annotations = rule.valueOrDefault("ignoreAnnotated", emptyList<String>()).map {
        it.qualifiedNameGlobToRegex()
    }
    return if (annotations.isNotEmpty()) {
        AnnotationSuppressor(file, annotations, bindingContext)
    } else {
        null
    }
}

private fun String.qualifiedNameGlobToRegex(): Regex {
    return this
        .replace(".", """\.""")
        .replace("**", "//")
        .replace("*", "[^.]*")
        .replace("//", ".*")
        .replace("?", ".")
        .toRegex()
}
