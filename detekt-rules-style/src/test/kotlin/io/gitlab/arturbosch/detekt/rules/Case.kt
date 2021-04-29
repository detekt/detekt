package io.gitlab.arturbosch.detekt.rules

import io.github.detekt.test.utils.resourceAsPath
import java.nio.file.Path

/* Do not add new elements to this file. Instead, use inline code snippets within the tests.
   See https://github.com/detekt/detekt/issues/1089 */
enum class Case(val file: String) {
    FunctionReturningConstantPositive("/FunctionReturningConstantPositive.kt"),
    FunctionReturningConstantNegative("/FunctionReturningConstantNegative.kt"),
    LoopWithTooManyJumpStatementsNegative("LoopWithTooManyJumpStatementsNegative.kt"),
    LoopWithTooManyJumpStatementsPositive("LoopWithTooManyJumpStatementsPositive.kt"),
    MaxLineLength("/MaxLineLength.kt"),
    MaxLineLengthSuppressed("/MaxLineLengthSuppressed.kt"),
    MaxLineLengthWithLongComments("/MaxLineLengthWithLongComments.kt"),
    UtilityClassesPositive("/UtilityClassesPositive.kt"),
    UtilityClassesNegative("/UtilityClassesNegative.kt"),
    NoTabsNegative("/NoTabsNegative.kt"),
    NoTabsPositive("/NoTabsPositive.kt");

    fun path(): Path = resourceAsPath(file)
}
