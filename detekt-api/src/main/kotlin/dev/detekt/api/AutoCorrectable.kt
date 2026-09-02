package dev.detekt.api

/**
 * Marks a [Rule] which is able to correct the code it reports on.
 *
 * Only rules implementing this interface can be auto-corrected: for every other rule
 * [Rule.autoCorrect] is always `false`, regardless of configuration. This allows detekt to
 * tell apart "this rule was asked to correct" from "this rule is able to correct", and to
 * warn about configuration which would otherwise silently do nothing.
 */
interface AutoCorrectable
