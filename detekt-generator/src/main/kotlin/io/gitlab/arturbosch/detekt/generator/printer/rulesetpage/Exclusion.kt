package io.gitlab.arturbosch.detekt.generator.printer.rulesetpage

import io.gitlab.arturbosch.detekt.generator.collection.Rule

/**
 * Holds a list of extra exclusions for rules and rule sets.
 */
val exclusions = arrayOf(TestExclusions, KotlinScriptExclusions, LibraryExclusions)

/**
 * Tracks rules and rule sets which needs an extra `excludes: $pattern` property
 * when printing the default detekt config yaml file.
 */
abstract class Exclusions {

    abstract val pattern: String
    open val ruleSets: Set<String> = setOf()
    abstract val rules: Set<String>

    fun isExcluded(rule: Rule) = rule.name in rules || rule.inMultiRule in rules
}

private object TestExclusions : Exclusions() {

    override val pattern =
            "['**/test/**', '**/androidTest/**', '**/commonTest/**', '**/jvmTest/**', '**/jsTest/**', '**/iosTest/**']"
    override val ruleSets = setOf("comments")
    override val rules = setOf(
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
        "InstanceOfCheckForException",
        "ThrowingExceptionsWithoutMessageOrCause"
    )
}

private object KotlinScriptExclusions : Exclusions() {

    override val pattern = "['*.kts']"
    override val rules = setOf("InvalidPackageDeclaration")
}

private object LibraryExclusions : Exclusions() {

    override val pattern = "['**']"
    override val rules = setOf(
        "ForbiddenPublicDataClass",
        "LibraryCodeMustSpecifyReturnType",
        "LibraryEntitiesShouldNotBePublic",
    )
}
