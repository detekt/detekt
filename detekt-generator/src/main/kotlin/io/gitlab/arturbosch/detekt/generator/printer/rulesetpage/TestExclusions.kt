package io.gitlab.arturbosch.detekt.generator.printer.rulesetpage

import io.gitlab.arturbosch.detekt.generator.collection.Rule

/**
 * Tracks rules and rule sets which needs an extra `exclusions: $pattern` property
 * when printing the default detekt config yaml file.
 */
object TestExclusions {

    const val pattern = "['**/test/**', '**/androidTest/**', '**/*.Test.kt', '**/*.Spec.kt', '**/*.Spek.kt']"
    val ruleSets = setOf("comments")
    val rules = setOf(
        "NamingRules",
        "WildcardImport",
        "MagicNumber",
        "LateinitUsage",
        "StringLiteralDuplication",
        "SpreadOperator",
        "TooManyFunctions",
        "ForEachOnRange",
        "FunctionMaxLength",
        "TooGenericExceptionCaught",
        "InstanceOfCheckForException"
    )

    fun Rule.isExcludedInTests() = name in rules || inMultiRule in rules
}
