package dev.detekt.psi

import dev.detekt.psi.internal.FullQualifiedNameGuesser
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.types.KaClassType
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.utils.KotlinExceptionWithAttachments

/**
 * Primary use case for an AnnotationExcluder is to decide if a KtElement should be
 * excluded from further analysis. This is done by checking if a special annotation
 * is present over the element.
 */
class AnnotationExcluder(
    root: KtFile,
    private val excludes: List<Regex>,
) {

    private val fullQualifiedNameGuesser = FullQualifiedNameGuesser(root)

    /**
     * Is true if any given annotation name is declared in the SplitPattern
     * which basically describes entries to exclude.
     */
    fun shouldExclude(annotations: List<KtAnnotationEntry>): Boolean =
        annotations.any { annotation -> annotation.typeReference?.let { isExcluded(it) } ?: false }

    private fun isExcluded(annotation: KtTypeReference): Boolean {
        val fqName = annotation.fqNameOrNull()
        val possibleNames = if (fqName == null) {
            fullQualifiedNameGuesser.getFullQualifiedName(annotation.text.toString())
                .map { it.getPackage() to it }
                .flatMap { (packaage, fqName) ->
                    fqName.substringAfter("$packaage.", "")
                        .split(".")
                        .reversed()
                        .scan("") { acc, name -> if (acc.isEmpty()) name else "$name.$acc" }
                        .drop(1) + fqName
                }
        } else {
            buildList {
                add(fqName.first + "." + fqName.second)
                addAll(
                    fqName.second
                        .split(".")
                        .reversed()
                        .scan("") { acc, name -> if (acc.isEmpty()) name else "$name.$acc" }
                        .drop(1)
                )
            }
        }

        return possibleNames.any { name -> name in excludes }
    }
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

private fun KtTypeReference.fqNameOrNull(): Pair<String, String>? = try {
    analyze(this) {
        val type = type as? KaClassType ?: return null
        val classId = type.symbol.classId ?: return null
        classId.packageFqName.toString() to classId.relativeClassName.toString()
    }
} catch (ex: KotlinExceptionWithAttachments) {
    if (ex.message == "Cannot find a KaModule for the VirtualFile") {
        null
    } else {
        throw ex
    }
}

private operator fun Iterable<Regex>.contains(a: String?): Boolean {
    if (a == null) return false
    return any { it.matches(a) }
}
