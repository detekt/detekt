package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.api.config
import dev.detekt.api.valuesWithReason
import dev.detekt.psi.pathGlobToRegex
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.analysis.api.types.KaType
import org.jetbrains.kotlin.analysis.api.types.symbol
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * Reports classes that extend a forbidden parent class.
 *
 * This rule allows to forbid the inheritance from specific classes, for example when a project defines a custom
 * replacement which is different from what the community recommends. Unlike `ForbiddenImport`, this rule uses type
 * resolution, so it also reports subclasses that do not mention the forbidden parent at all - neither in an import
 * nor in the class declaration - because the parent is inherited transitively through an intermediate class.
 *
 * Superclasses are configured as glob patterns and may be exact names such as `androidx.lifecycle.ViewModel`
 * or wildcard patterns such as `com.example.internal.*`. Each entry can also include a reason that is shown in
 * the finding. Patterns are matched against the fully qualified name of the parent and follow detekt's
 * [simple glob syntax](https://detekt.dev/docs/introduction/glob-patterns#simple-patterns).
 *
 * Implementing a forbidden interface is reported as well, because from the type system's perspective an interface
 * is a supertype too.
 *
 * ```yaml
 * ForbiddenSuperclass:
 *   active: false
 *   parentClasses:
 *     - value: 'androidx.lifecycle.ViewModel'
 *       reason: 'Use the custom BaseViewModel of this project instead.'
 *     - value: 'java.lang.Thread'
 *       reason: 'Use coroutines or java.util.concurrent instead.'
 * ```
 *
 * <noncompliant>
 * class CustomViewModel : androidx.lifecycle.ViewModel()
 *
 * open class BaseViewModel : ViewModel()
 * class AnotherViewModel : BaseViewModel()
 * </noncompliant>
 *
 * <compliant>
 * class CustomViewModel : BaseViewModel()
 * </compliant>
 */
class ForbiddenSuperclass(config: Config) :
    Rule(
        config,
        "Mark forbidden parent classes. Extending a forbidden class could indicate a violation of an " +
            "architecture decision of your project."
    ),
    RequiresAnalysisApi {

    @Configuration(
        "List of parent classes, specified as glob patterns, that are forbidden. " +
            "A pattern has to match the whole fully qualified name, where `*` matches zero or more " +
            "characters including `.` and `?` matches exactly one character. " +
            "Entries may either be plain strings or value/reason pairs, allowing you to specify the reason why each " +
            "individual parent class is forbidden."
    )
    private val parentClasses: List<ForbiddenParent> by config(valuesWithReason()) { list ->
        list.map { ForbiddenParent(it.value, it.value.pathGlobToRegex(), it.reason) }
    }

    override fun visitClassOrObject(classOrObject: KtClassOrObject) {
        super.visitClassOrObject(classOrObject)
        if (classOrObject is KtClass && classOrObject.isInterface()) return

        analyze(classOrObject) {
            val classSymbol = classOrObject.symbol as? KaClassSymbol ?: return@analyze

            val directSuperTypes = classSymbol.superTypes
            val transitiveSuperTypes = directSuperTypes + directSuperTypes.flatMap { it.allSupertypes }

            val forbiddenParent = transitiveSuperTypes.firstNotNullOfOrNull { it.forbiddenParent() }
            if (forbiddenParent != null) {
                val reason = forbiddenParent.reason?.let {
                    "The parent class `${forbiddenParent.original}` has been forbidden: $it"
                } ?: defaultReason(forbiddenParent.original)

                report(Finding(Entity.atName(classOrObject), reason))
            }
        }
    }

    private fun KaType.forbiddenParent(): ForbiddenParent? {
        val fqName = symbol?.classId?.asFqNameString() ?: return null
        return parentClasses.firstOrNull { it.matches(fqName) }
    }

    private fun defaultReason(forbiddenParent: String): String =
        "The parent class `$forbiddenParent` has been forbidden in the detekt config."

    private data class ForbiddenParent(val original: String, val value: Regex, val reason: String?) {

        fun matches(fqName: String): Boolean = value.matches(fqName)
    }
}
