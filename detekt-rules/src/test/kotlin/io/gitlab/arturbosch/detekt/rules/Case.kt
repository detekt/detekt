package io.gitlab.arturbosch.detekt.rules

import io.github.detekt.test.utils.resourceAsPath
import java.nio.file.Path

/* Do not add new elements to this file. Instead, use inline code snippets within the tests.
   See https://github.com/detekt/detekt/issues/1089 */
enum class Case(val file: String) {
    ComplexMethods("/cases/ComplexMethods.kt"),
    ConstInObjects("/cases/ConstInObjects.kt"),
    Default("/cases/Default.kt"),
    Empty("/cases/Empty.kt"),
    EmptyDefaultConstructorNegative("/cases/EmptyDefaultConstructorNegative.kt"),
    EmptyDefaultConstructorPositive("/cases/EmptyDefaultConstructorPositive.kt"),
    EqualsAlwaysReturnsTrueOrFalsePositive("/cases/EqualsAlwaysReturnsTrueOrFalsePositive.kt"),
    EqualsAlwaysReturnsTrueOrFalseNegative("/cases/EqualsAlwaysReturnsTrueOrFalseNegative.kt"),
    ExceptionRaisedInMethodsNegative("/cases/ExceptionRaisedInMethodsNegative.kt"),
    ExceptionRaisedInMethodsPositive("/cases/ExceptionRaisedInMethodsPositive.kt"),
    FunctionReturningConstantPositive("/cases/FunctionReturningConstantPositive.kt"),
    FunctionReturningConstantNegative("/cases/FunctionReturningConstantNegative.kt"),
    IteratorImplNegative("/cases/IteratorImplNegative.kt"),
    IteratorImplPositive("/cases/IteratorImplPositive.kt"),
    Library("/cases/library/Library.kt"),
    LoopWithTooManyJumpStatementsNegative("cases/LoopWithTooManyJumpStatementsNegative.kt"),
    LoopWithTooManyJumpStatementsPositive("cases/LoopWithTooManyJumpStatementsPositive.kt"),
    MayBeConstNegative("cases/MayBeConstNegative.kt"),
    MaxLineLength("/cases/MaxLineLength.kt"),
    MaxLineLengthSuppressed("/cases/MaxLineLengthSuppressed.kt"),
    MaxLineLengthWithLongComments("/cases/MaxLineLengthWithLongComments.kt"),
    NestedClasses("/cases/NestedClasses.kt"),
    UtilityClassesPositive("/cases/UtilityClassesPositive.kt"),
    UtilityClassesNegative("/cases/UtilityClassesNegative.kt"),
    SuppressedElements("/SuppressedByElementAnnotation.kt"),
    SuppressedElementsByFile("/SuppressedElementsByFileAnnotation.kt"),
    SuppressedElementsByClass("/SuppressedElementsByClassAnnotation.kt"),
    SuppressStringLiteralDuplication("/SuppressStringLiteralDuplication.kt"),
    UnconditionalJumpStatementInLoopNegative("/cases/UnconditionalJumpStatementInLoopNegative.kt"),
    UnconditionalJumpStatementInLoopPositive("/cases/UnconditionalJumpStatementInLoopPositive.kt"),
    NoTabsNegative("/cases/NoTabsNegative.kt"),
    NoTabsPositive("/cases/NoTabsPositive.kt"),
    UnusedPrivateMemberPositive("/cases/UnusedPrivateMemberPositive.kt"),
    UnusedPrivateMemberNegative("/cases/UnusedPrivateMemberNegative.kt");

    fun path(): Path = resourceAsPath(file)
}
