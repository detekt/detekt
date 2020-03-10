package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.test.resource
import java.nio.file.Path
import java.nio.file.Paths

/* Do not add new elements to this file. Instead, use inline code snippets within the tests.
   See https://github.com/arturbosch/detekt/issues/1089 */
enum class Case(val file: String) {
    CollapsibleIfsPositive("/cases/CollapsibleIfsPositive.kt"),
    CollapsibleIfsNegative("/cases/CollapsibleIfsNegative.kt"),
    ComplexMethods("/cases/ComplexMethods.kt"),
    ConstInObjects("/cases/ConstInObjects.kt"),
    Default("/cases/Default.kt"),
    Empty("/cases/Empty.kt"),
    EmptyKtFile("/cases/EmptyKtFile.kt"),
    EmptyDefaultConstructorNegative("/cases/EmptyDefaultConstructorNegative.kt"),
    EmptyDefaultConstructorPositive("/cases/EmptyDefaultConstructorPositive.kt"),
    EqualsAlwaysReturnsTrueOrFalsePositive("/cases/EqualsAlwaysReturnsTrueOrFalsePositive.kt"),
    EqualsAlwaysReturnsTrueOrFalseNegative("/cases/EqualsAlwaysReturnsTrueOrFalseNegative.kt"),
    TooGenericExceptions("/cases/TooGenericExceptions.kt"),
    TooGenericExceptionsOptions("/cases/TooGenericExceptionsOptions.kt"),
    ExceptionRaisedInMethodsNegative("/cases/ExceptionRaisedInMethodsNegative.kt"),
    ExceptionRaisedInMethodsPositive("/cases/ExceptionRaisedInMethodsPositive.kt"),
    FinalClassNegative("/cases/ProtectedMemberInFinalClassNegative.kt"),
    FinalClassPositive("/cases/ProtectedMemberInFinalClassPositive.kt"),
    FunctionReturningConstantPositive("/cases/FunctionReturningConstantPositive.kt"),
    FunctionReturningConstantNegative("/cases/FunctionReturningConstantNegative.kt"),
    IteratorImplNegative("/cases/IteratorImplNegative.kt"),
    IteratorImplPositive("/cases/IteratorImplPositive.kt"),
    Library("/cases/library/Library.kt"),
    LoopWithTooManyJumpStatementsNegative("cases/LoopWithTooManyJumpStatementsNegative.kt"),
    LoopWithTooManyJumpStatementsPositive("cases/LoopWithTooManyJumpStatementsPositive.kt"),
    MayBeConstNegative("cases/MayBeConstNegative.kt"),
    MandatoryBracesIfStatementsNegative("cases/MandatoryBracesIfStatementsNegative.kt"),
    NewLineAtEndOfFile("/cases/NewLineAtEndOfFile.kt"),
    MaxLineLength("/cases/MaxLineLength.kt"),
    MaxLineLengthSuppressed("/cases/MaxLineLengthSuppressed.kt"),
    MaxLineLengthWithLongComments("/cases/MaxLineLengthWithLongComments.kt"),
    MemberNameEqualsClassNameNegative("/cases/MemberNameEqualsClassNameNegative.kt"),
    NestedClasses("/cases/NestedClasses.kt"),
    NoClasses("/cases/NoClasses.kt"),
    UnreachableCode("/cases/UnreachableCode.kt"),
    UnnecessaryAbstractClassPositive("/cases/UnnecessaryAbstractClassPositive.kt"),
    UnnecessaryAbstractClassNegative("/cases/UnnecessaryAbstractClassNegative.kt"),
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

    fun path(): Path = Paths.get(resource(file))
}
