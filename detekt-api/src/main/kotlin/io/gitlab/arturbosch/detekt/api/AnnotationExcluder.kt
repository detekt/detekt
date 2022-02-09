package io.gitlab.arturbosch.detekt.api

import io.github.detekt.psi.internal.FullQualifiedNameGuesser
import io.gitlab.arturbosch.detekt.rules.fqNameOrNull
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.resolve.BindingContext

/**
 * Primary use case for an AnnotationExcluder is to decide if a KtElement should be
 * excluded from further analysis. This is done by checking if a special annotation
 * is present over the element.
 */
class AnnotationExcluder(
    root: KtFile,
    private val excludes: List<Regex>,
    private val context: BindingContext,
) {

    private val fullQualifiedNameGuesser = FullQualifiedNameGuesser(root)

    @Deprecated("Use AnnotationExcluder(List<Regex>, KtFile) instead")
    constructor(root: KtFile, excludes: SplitPattern) : this(
        root,
        excludes.mapAll { it }
            .map { it.replace(".", "\\.").replace("*", ".*").toRegex() },
        BindingContext.EMPTY,
    )

    @Deprecated("Use AnnotationExcluder(List<Regex>, KtFile) instead")
    constructor(
        root: KtFile,
        excludes: List<String>,
    ) : this(
        root,
        excludes.map {
            it.replace(".", "\\.").replace("*", ".*").toRegex()
        },
        BindingContext.EMPTY,
    )

    /**
     * Is true if any given annotation name is declared in the SplitPattern
     * which basically describes entries to exclude.
     */
    fun shouldExclude(annotations: List<KtAnnotationEntry>): Boolean {
        return annotations.any { annotation -> annotation.typeReference?.let { isExcluded(it, context) } ?: false }
    }

    private fun isExcluded(annotation: KtTypeReference, context: BindingContext): Boolean {
        val fqName = if (context == BindingContext.EMPTY) null else annotation.fqNameOrNull(context)
        return if (fqName == null) {
            fullQualifiedNameGuesser.getFullQualifiedName(annotation.text.toString())
                .map { it.getPackage() to it }
        } else {
            listOf(fqName.getPackage() to fqName.toString())
        }
            .flatMap { (pack, fqName) ->
                fqName.substringAfter("$pack.", "")
                    .split(".")
                    .reversed()
                    .scan("") { acc, name -> if (acc.isEmpty()) name else "$name.$acc" }
                    .drop(1) + fqName
            }
            .any { name -> name in excludes }
    }
}

private fun FqName.getPackage(): String {
    /* This is a shortcut. Right now we are using the same heuristic that we use when we don't have type solving
     * information. With the type solving information we should know exactly which part is package and which part is
     * class name. But right now I don't know how to extract that information. There is a disabled test that should be
     * enabled once this is solved.
     */
    return this.toString().getPackage()
}

private fun String.getPackage(): String {
    /* We can't know if the annotationText is a full-qualified name or not. We can have these cases:
     * @Component
     * @Component.Factory
     * @dagger.Component.Factory
     * For that reason we use a heuristic here: If the first character is lower case we assume it's a package name
     */
    return this
        .splitToSequence(".")
        .takeWhile { it.first().isLowerCase() }
        .joinToString(".")
}

private fun KtTypeReference.fqNameOrNull(bindingContext: BindingContext): FqName? {
    return bindingContext[BindingContext.TYPE, this]?.fqNameOrNull()
}

private operator fun Iterable<Regex>.contains(a: String?): Boolean {
    if (a == null) return false
    return any { it.matches(a) }
}
