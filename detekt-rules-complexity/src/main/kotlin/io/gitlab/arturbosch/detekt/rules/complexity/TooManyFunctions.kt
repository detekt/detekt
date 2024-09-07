package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.rules.hasAnnotation
import io.gitlab.arturbosch.detekt.rules.isInternal
import io.gitlab.arturbosch.detekt.rules.isOverride
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.psiUtil.isPrivate

/**
 * This rule reports files, classes, interfaces, objects and enums which contain too many functions.
 * Each element can be configured with different thresholds.
 *
 * Too many functions indicate a violation of the single responsibility principle. Prefer extracting functionality
 * which clearly belongs together in separate parts of the code.
 */
@ActiveByDefault(since = "1.0.0")
class TooManyFunctions(config: Config) : Rule(
    config,
    "Too many functions inside a/an file/class/object/interface always indicate a violation of " +
        "the single responsibility principle. Maybe the file/class/object/interface wants to manage too " +
        "many things at once. Extract functionality which clearly belongs together."
) {

    @Configuration("The maximum allowed functions per file")
    private val allowedFunctionsPerFile: Int by config(DEFAULT_THRESHOLD)

    @Configuration("The maximum allowed functions per class")
    private val allowedFunctionsPerClass: Int by config(DEFAULT_THRESHOLD)

    @Configuration("The maximum allowed functions per interface")
    private val allowedFunctionsPerInterface: Int by config(DEFAULT_THRESHOLD)

    @Configuration("The maximum allowed function per object")
    private val allowedFunctionsPerObject: Int by config(DEFAULT_THRESHOLD)

    @Configuration("The maximum allowed functions in enums")
    private val allowedFunctionsPerEnum: Int by config(DEFAULT_THRESHOLD)

    @Configuration("ignore deprecated functions")
    private val ignoreDeprecated: Boolean by config(false)

    @Configuration("ignore private functions")
    private val ignorePrivate: Boolean by config(false)

    @Configuration("ignore internal functions")
    private val ignoreInternal: Boolean by config(false)

    @Configuration("ignore overridden functions")
    private val ignoreOverridden: Boolean by config(false)

    @Configuration("ignore functions annotated with these annotations")
    private val ignoreAnnotatedFunctions: List<String> by config(emptyList())

    private var amountOfTopLevelFunctions: Int = 0

    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)
        if (amountOfTopLevelFunctions > allowedFunctionsPerFile) {
            report(
                CodeSmell(
                    Entity.atPackageOrFirstDecl(file),
                    "File '${file.name}' with '$amountOfTopLevelFunctions' functions detected. " +
                        "The maximum allowed functions per file is set to '$allowedFunctionsPerFile'"
                )
            )
        }
        amountOfTopLevelFunctions = 0
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        if (function.isTopLevel && !isIgnoredFunction(function)) {
            amountOfTopLevelFunctions++
        }
    }

    override fun visitClass(klass: KtClass) {
        val amount = calcFunctions(klass)
        when {
            klass.isInterface() -> {
                if (amount > allowedFunctionsPerInterface) {
                    report(
                        CodeSmell(
                            Entity.atName(klass),
                            "Interface '${klass.name}' with '$amount' functions detected. " +
                                "The maximum allowed functions per interface is set to " +
                                "'$allowedFunctionsPerInterface'"
                        )
                    )
                }
            }

            klass.isEnum() -> {
                if (amount > allowedFunctionsPerEnum) {
                    report(
                        CodeSmell(
                            Entity.atName(klass),
                            "Enum class '${klass.name}' with '$amount' functions detected. " +
                                "The maximum allowed functions per enum class is set to " +
                                "'$allowedFunctionsPerEnum'"
                        )
                    )
                }
            }

            else -> {
                if (amount > allowedFunctionsPerClass) {
                    report(
                        CodeSmell(
                            Entity.atName(klass),
                            "Class '${klass.name}' with '$amount' functions detected. " +
                                "The maximum allowed functions per class is set to '$allowedFunctionsPerClass'"
                        )
                    )
                }
            }
        }
        super.visitClass(klass)
    }

    override fun visitObjectDeclaration(declaration: KtObjectDeclaration) {
        val amount = calcFunctions(declaration)
        if (amount > allowedFunctionsPerObject) {
            report(
                CodeSmell(
                    Entity.atName(declaration),
                    "Object '${declaration.name}' with '$amount' functions detected. " +
                        "The maximum allowed functions per object is set to '$allowedFunctionsPerObject'"
                )
            )
        }
        super.visitObjectDeclaration(declaration)
    }

    private fun calcFunctions(classOrObject: KtClassOrObject): Int = classOrObject.body
        ?.run {
            declarations
                .filterIsInstance<KtNamedFunction>()
                .count { !isIgnoredFunction(it) }
        }
        ?: 0

    private fun isIgnoredFunction(function: KtNamedFunction): Boolean = when {
        ignoreDeprecated && function.hasAnnotation(DEPRECATED) -> true
        ignorePrivate && function.isPrivate() -> true
        ignoreInternal && function.isInternal() -> true
        ignoreOverridden && function.isOverride() -> true
        ignoreAnnotatedFunctions.any { function.hasAnnotation(it) } -> true
        else -> false
    }

    companion object {
        const val DEFAULT_THRESHOLD = 11
        private const val DEPRECATED = "Deprecated"
    }
}
