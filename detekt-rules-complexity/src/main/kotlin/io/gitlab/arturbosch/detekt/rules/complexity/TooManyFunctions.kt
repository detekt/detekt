package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Metric
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.rules.hasAnnotation
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
class TooManyFunctions(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        "TooManyFunctions",
        "Too many functions inside a/an file/class/object/interface always indicate a violation of " +
            "the single responsibility principle. Maybe the file/class/object/interface wants to manage too " +
            "many things at once. Extract functionality which clearly belongs together.",
        Debt.TWENTY_MINS
    )

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

    @Configuration("ignore overridden functions")
    private val ignoreOverridden: Boolean by config(false)

    private var amountOfTopLevelFunctions: Int = 0

    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)
        if (amountOfTopLevelFunctions > allowedFunctionsPerFile) {
            report(
                ThresholdedCodeSmell(
                    issue,
                    Entity.atPackageOrFirstDecl(file),
                    Metric("SIZE", amountOfTopLevelFunctions, allowedFunctionsPerFile),
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
                        ThresholdedCodeSmell(
                            issue,
                            Entity.atName(klass),
                            Metric("SIZE", amount, allowedFunctionsPerInterface),
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
                        ThresholdedCodeSmell(
                            issue,
                            Entity.atName(klass),
                            Metric("SIZE", amount, allowedFunctionsPerEnum),
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
                        ThresholdedCodeSmell(
                            issue,
                            Entity.atName(klass),
                            Metric("SIZE", amount, allowedFunctionsPerClass),
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
                ThresholdedCodeSmell(
                    issue,
                    Entity.atName(declaration),
                    Metric("SIZE", amount, allowedFunctionsPerObject),
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
        ignoreOverridden && function.isOverride() -> true
        else -> false
    }

    companion object {
        const val DEFAULT_THRESHOLD = 11
        private const val DEPRECATED = "Deprecated"
    }
}
