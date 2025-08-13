package dev.detekt.psi

import dev.detekt.psi.internal.FullQualifiedNameGuesser
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.types.KaType
import org.jetbrains.kotlin.analysis.api.types.symbol
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
    context: BindingContext? = null, // this will be removed
    private val fullAnalysis: Boolean = false
) {

    private val fullQualifiedNameGuesser = FullQualifiedNameGuesser(root)

    /**
     * Is true if any given annotation name is declared in the SplitPattern
     * which basically describes entries to exclude.
     */
    fun shouldExclude(annotations: List<KtAnnotationEntry>): Boolean {
        if (annotations.isEmpty()) return false

        if (fullAnalysis) {

            return annotations.any { annotation ->
                analyze(annotation) {
                    annotation.typeReference?.let { isExcludedFullAnalysis(it.type) } ?: false
                }
            }
        }
        return annotations.any { annotation ->
            annotation.typeReference?.let { isExcludedLightAnalysis(it) } ?: false
        }
    }


    private fun isExcludedFullAnalysis(type: KaType): Boolean {
        val fqName = type.symbol?.classId?.asSingleFqName() ?: return false
        val possibleNames = calculateCandidates(
            listOf(fqName.getPackage() to fqName.toString())
        )

        return possibleNames.any { name -> name in excludes }
    }

    private fun isExcludedLightAnalysis(annotation: KtTypeReference): Boolean {
        val possibleNames = calculateCandidates(
            fullQualifiedNameGuesser.getFullQualifiedName(annotation.text.toString())
                .map { it.getPackage() to it }
        )

        return possibleNames.any { name -> name in excludes }
    }

    private fun calculateCandidates(packageToFqName: List<Pair<String, String>>): List<String> = packageToFqName
        .flatMap { (packaage, fqName) ->
            fqName.substringAfter("$packaage.", "")
                .split(".")
                .reversed()
                .scan("") { acc, name -> if (acc.isEmpty()) name else "$name.$acc" }
                .drop(1) + fqName
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

private operator fun Iterable<Regex>.contains(a: String?): Boolean {
    if (a == null) return false
    return any { it.matches(a) }
}
