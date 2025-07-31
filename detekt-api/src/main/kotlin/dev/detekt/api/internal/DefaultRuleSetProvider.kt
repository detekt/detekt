package dev.detekt.api.internal

import dev.detekt.api.RuleSetProvider

/**
 * Interface which marks sub-classes as provided by detekt via the rules sub-module.
 *
 * Allows to implement "--disable-default-rulesets" effectively without the need
 * to manage a list of rule set names.
 */
interface DefaultRuleSetProvider : RuleSetProvider
