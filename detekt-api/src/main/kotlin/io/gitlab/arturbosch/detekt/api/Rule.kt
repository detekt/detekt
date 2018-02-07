package io.gitlab.arturbosch.detekt.api

import org.jetbrains.kotlin.psi.KtFile

/**
 * A rule defines how one specific code structure should look like. If code is found
 * which does not meet this structure, it is considered as harmful regarding maintainability
 * or readability.
 *
 * A rule is implemented using the visitor pattern and should be started using the visit(KtFile)
 * function. If calculations must be done before or after the visiting process, here are
 * two predefined (preVisit/postVisit) functions which can be overridden to setup/teardown additional data.
 *
 * @author Artur Bosch
 * @author Marvin Ramin
 */
abstract class Rule(override val config: Config = Config.empty,
					ruleContext: Context = DefaultContext()) :
		BaseRule(ruleContext), ConfigAware {

	abstract val issue: Issue
	final override val id: String by lazy(LazyThreadSafetyMode.NONE) { issue.id }

	override fun visitCondition(root: KtFile) = active && !root.isSuppressedBy(id, issue.aliases)
}
