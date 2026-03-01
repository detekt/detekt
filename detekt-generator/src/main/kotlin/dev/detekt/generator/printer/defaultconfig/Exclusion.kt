package dev.detekt.generator.printer.defaultconfig

import dev.detekt.generator.collection.Rule

/**
 * Holds a list of extra exclusions for rules and rule sets.
 */
val exclusions = arrayOf(TestExclusions, KotlinScriptExclusions, KotlinScriptAndTestExclusions)

/**
 * Tracks rules and rule sets which needs an extra `excludes: $pattern` property
 * when printing the default detekt config yaml file.
 */
abstract class Exclusions {

    abstract val pattern: String
    open val ruleSets: Set<String> = emptySet()
    abstract val rules: Set<String>

    fun isExcluded(rule: Rule) = rule.name in rules

    companion object {
        internal val testFolders = listOf(
            "test",
            "androidTest",
            "commonTest",
            "jvmTest",
            "androidUnitTest",
            "androidHostTest",
            "androidInstrumentedTest",
            "jsTest",
            "iosTest",
        )
    }
}

private object TestExclusions : Exclusions() {
    override val pattern = testFolders.map { "**/$it/**" }
        .joinToString(prefix = "[", separator = ", ", postfix = "]") { "'$it'" }
    override val ruleSets = emptySet<String>()
    override val rules = setOf(
        "FunctionNaming",
        "LateinitUsage",
        "StringLiteralDuplication",
        "SpreadOperator",
        "TooManyFunctions",
        "ForEachOnRange",
        "TooGenericExceptionCaught",
        "InstanceOfCheckForException",
        "ThrowingExceptionsWithoutMessageOrCause",
        "UndocumentedPublicClass",
        "UndocumentedPublicFunction",
        "UndocumentedPublicProperty",
        "UnsafeCallOnNullableType",
        "KDocReferencesNonPublicProperty",
    )
}

private object KotlinScriptExclusions : Exclusions() {
    override val pattern = "['**/*.kts']"
    override val rules = setOf("MissingPackageDeclaration")
}

private object KotlinScriptAndTestExclusions : Exclusions() {
    override val pattern = (testFolders.map { "**/$it/**" } + "**/*.kts")
        .joinToString(prefix = "[", separator = ", ", postfix = "]") { "'$it'" }
    override val rules = setOf("MagicNumber")
}
