package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.rules.isOverride
import io.gitlab.arturbosch.detekt.rules.naming.util.isContainingExcludedClassOrObject
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtUserType

/**
 * Reports function names that do not follow the specified naming convention.
 * One exception are factory functions used to create instances of classes.
 * These factory functions can have the same name as the class being created.
 */
@ActiveByDefault(since = "1.0.0")
class FunctionNaming(config: Config = Config.empty) : Rule(config) {

    override val defaultRuleIdAliases: Set<String> = setOf("FunctionName")

    override val issue = Issue(
        javaClass.simpleName,
        "Function names should follow the naming convention set in the configuration.",
    )

    @Configuration("naming pattern")
    private val functionPattern: Regex by config("[a-z][a-zA-Z0-9]*", String::toRegex)

    @Configuration("ignores functions in classes which match this regex")
    private val excludeClassPattern: Regex by config("$^", String::toRegex)

    @Configuration("ignores functions that have the override modifier")
    @Deprecated("This configuration is ignored and will be removed in the future")
    @Suppress("UnusedPrivateMember")
    private val ignoreOverridden: Boolean by config(true)

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)

        if (function.isOverride()) {
            return
        }

        val functionName = function.nameIdentifier?.text ?: return
        if (!function.isContainingExcludedClassOrObject(excludeClassPattern) &&
            !functionName.matches(functionPattern) &&
            functionName != function.returnTypeName()
        ) {
            report(
                CodeSmell(
                    issue,
                    Entity.atName(function),
                    message = "Function names should match the pattern: $functionPattern"
                )
            )
        }
    }

    private fun KtNamedFunction.returnTypeName() = (typeReference?.typeElement as? KtUserType)?.referencedName

    companion object {
        const val FUNCTION_PATTERN = "functionPattern"
        const val EXCLUDE_CLASS_PATTERN = "excludeClassPattern"
    }
}
