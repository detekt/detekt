package io.gitlab.arturbosch.detekt.api

/**
 * The type to use when referring to rule ids giving it more context then a String would.
 */
typealias RuleId = String

/**
 * Defines the visiting mechanism for KtFile's.
 *
 * Custom rule implementations should actually use [Rule] as base class.
 *
 * The extraction of this class from [Rule] actually resulted from the need
 * of running many different checks on the same KtFile but within a single
 * potential costly visiting process, see [MultiRule].
 *
 * This base rule class abstracts over single and multi rules and allows the
 * detekt core engine to only care about a single type.
 */
@Deprecated("""
Do not use this class directly. Use Rule or MultiRule instead.
This class was introduced to support a common handling of the mentioned rule types.
This class will be made sealed in a different release and you won't be able to derive from it. 
""")
typealias BaseRule = io.gitlab.arturbosch.detekt.api.internal.BaseRule
