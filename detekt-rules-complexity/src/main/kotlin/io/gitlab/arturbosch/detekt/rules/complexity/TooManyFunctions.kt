package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Metric
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
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
        Severity.Maintainability,
        "Too many functions inside a/an file/class/object/interface always indicate a violation of " +
            "the single responsibility principle. Maybe the file/class/object/interface wants to manage too " +
            "many things at once. Extract functionality which clearly belongs together.",
        Debt.TWENTY_MINS
    )

    @Configuration("The maximum allowed functions in files")
    private val allowedFunctionsInFiles: Int by config(DEFAULT_THRESHOLD)

    @Configuration("The maximum allowed functions in classes")
    private val allowedFunctionsInClasses: Int by config(DEFAULT_THRESHOLD)

    @Configuration("The maximum allowed functions in interfaces")
    private val allowedFunctionsInInterfaces: Int by config(DEFAULT_THRESHOLD)

    @Configuration("The maximum allowed functions in objects")
    private val allowedFunctionsInObjects: Int by config(DEFAULT_THRESHOLD)

    @Configuration("The maximum allowed functions in enums")
    private val allowedFunctionsInEnums: Int by config(DEFAULT_THRESHOLD)

    @Configuration("ignore deprecated functions")
    private val ignoreDeprecated: Boolean by config(false)

    @Configuration("ignore private functions")
    private val ignorePrivate: Boolean by config(false)

    @Configuration("ignore overridden functions")
    private val ignoreOverridden: Boolean by config(false)

    private var amountOfTopLevelFunctions: Int = 0

    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)
        if (amountOfTopLevelFunctions > allowedFunctionsInFiles) {
            report(
                ThresholdedCodeSmell(
                    issue,
                    Entity.atPackageOrFirstDecl(file),
                    Metric("SIZE", amountOfTopLevelFunctions, allowedFunctionsInFiles),
                    "File '${file.name}' with '$amountOfTopLevelFunctions' functions detected. " +
                        "The maximum allowed functions inside files is set to '$allowedFunctionsInFiles'"
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
                if (amount > allowedFunctionsInInterfaces) {
                    report(
                        ThresholdedCodeSmell(
                            issue,
                            Entity.atName(klass),
                            Metric("SIZE", amount, allowedFunctionsInInterfaces),
                            "Interface '${klass.name}' with '$amount' functions detected. " +
                                "The maximum allowed functions inside interfaces is set to " +
                                "'$allowedFunctionsInInterfaces'"
                        )
                    )
                }
            }
            klass.isEnum() -> {
                if (amount > allowedFunctionsInEnums) {
                    report(
                        ThresholdedCodeSmell(
                            issue,
                            Entity.atName(klass),
                            Metric("SIZE", amount, allowedFunctionsInEnums),
                            "Enum class '${klass.name}' with '$amount' functions detected. " +
                                "The maximum allowed functions inside enum classes is set to " +
                                "'$allowedFunctionsInEnums'"
                        )
                    )
                }
            }
            else -> {
                if (amount > allowedFunctionsInClasses) {
                    report(
                        ThresholdedCodeSmell(
                            issue,
                            Entity.atName(klass),
                            Metric("SIZE", amount, allowedFunctionsInClasses),
                            "Class '${klass.name}' with '$amount' functions detected. " +
                                "The maximum allowed functions inside classes is set to '$allowedFunctionsInClasses'"
                        )
                    )
                }
            }
        }
        super.visitClass(klass)
    }

    override fun visitObjectDeclaration(declaration: KtObjectDeclaration) {
        val amount = calcFunctions(declaration)
        if (amount > allowedFunctionsInObjects) {
            report(
                ThresholdedCodeSmell(
                    issue,
                    Entity.atName(declaration),
                    Metric("SIZE", amount, allowedFunctionsInObjects),
                    "Object '${declaration.name}' with '$amount' functions detected. " +
                        "The maximum allowed functions inside objects is set to '$allowedFunctionsInObjects'"
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
