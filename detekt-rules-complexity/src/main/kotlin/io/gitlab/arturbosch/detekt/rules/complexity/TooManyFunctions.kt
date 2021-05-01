package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Metric
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.api.internal.config
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

    @Configuration("threshold in files")
    private val thresholdInFiles: Int by config(DEFAULT_THRESHOLD)

    @Configuration("threshold in classes")
    private val thresholdInClasses: Int by config(DEFAULT_THRESHOLD)

    @Configuration("threshold in interfaces")
    private val thresholdInInterfaces: Int by config(DEFAULT_THRESHOLD)

    @Configuration("threshold in objects")
    private val thresholdInObjects: Int by config(DEFAULT_THRESHOLD)

    @Configuration("threshold in enums")
    private val thresholdInEnums: Int by config(DEFAULT_THRESHOLD)

    @Configuration("ignore deprecated functions")
    private val ignoreDeprecated: Boolean by config(false)

    @Configuration("ignore private functions")
    private val ignorePrivate: Boolean by config(false)

    @Configuration("ignore overridden functions")
    private val ignoreOverridden: Boolean by config(false)

    private var amountOfTopLevelFunctions: Int = 0

    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)
        if (amountOfTopLevelFunctions >= thresholdInFiles) {
            report(
                ThresholdedCodeSmell(
                    issue,
                    Entity.atPackageOrFirstDecl(file),
                    Metric("SIZE", amountOfTopLevelFunctions, thresholdInFiles),
                    "File '${file.name}' with '$amountOfTopLevelFunctions' functions detected. " +
                        "Defined threshold inside files is set to '$thresholdInFiles'"
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
                if (amount >= thresholdInInterfaces) {
                    report(
                        ThresholdedCodeSmell(
                            issue,
                            Entity.atName(klass),
                            Metric("SIZE", amount, thresholdInInterfaces),
                            "Interface '${klass.name}' with '$amount' functions detected. " +
                                "Defined threshold inside interfaces is set to " +
                                "'$thresholdInInterfaces'"
                        )
                    )
                }
            }
            klass.isEnum() -> {
                if (amount >= thresholdInEnums) {
                    report(
                        ThresholdedCodeSmell(
                            issue,
                            Entity.atName(klass),
                            Metric("SIZE", amount, thresholdInEnums),
                            "Enum class '${klass.name}' with '$amount' functions detected. " +
                                "Defined threshold inside enum classes is set to " +
                                "'$thresholdInEnums'"
                        )
                    )
                }
            }
            else -> {
                if (amount >= thresholdInClasses) {
                    report(
                        ThresholdedCodeSmell(
                            issue,
                            Entity.atName(klass),
                            Metric("SIZE", amount, thresholdInClasses),
                            "Class '${klass.name}' with '$amount' functions detected. " +
                                "Defined threshold inside classes is set to '$thresholdInClasses'"
                        )
                    )
                }
            }
        }
        super.visitClass(klass)
    }

    override fun visitObjectDeclaration(declaration: KtObjectDeclaration) {
        val amount = calcFunctions(declaration)
        if (amount >= thresholdInObjects) {
            report(
                ThresholdedCodeSmell(
                    issue,
                    Entity.from(declaration.nameIdentifier ?: declaration),
                    Metric("SIZE", amount, thresholdInObjects),
                    "Object '${declaration.name}' with '$amount' functions detected. " +
                        "Defined threshold inside objects is set to '$thresholdInObjects'"
                )
            )
        }
        super.visitObjectDeclaration(declaration)
    }

    private fun calcFunctions(classOrObject: KtClassOrObject): Int = classOrObject.body
        ?.run {
            declarations
                .filterIsInstance<KtNamedFunction>()
                .filter { !isIgnoredFunction(it) }
                .size
        } ?: 0

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
