package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.rules.style.KtFileContent
import io.gitlab.arturbosch.detekt.test.compileForTest
import io.gitlab.arturbosch.detekt.test.resource
import java.nio.file.Path
import java.nio.file.Paths

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
    LabeledExpressionNegative("/cases/LabeledExpressionNegative.kt"),
    LabeledExpressionPositive("/cases/LabeledExpressionPositive.kt"),
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
    MemberNameEqualsClassNamePositive("/cases/MemberNameEqualsClassNamePositive.kt"),
    OverloadedMethods("/cases/OverloadedMethods.kt"),
    ComplexMethod("/cases/ComplexMethod.kt"),
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
    TooManyFunctions("/cases/TooManyFunctions.kt"),
    TooManyFunctionsTopLevel("/cases/TooManyFunctionsTopLevel.kt"),
    UnconditionalJumpStatementInLoopNegative("/cases/UnconditionalJumpStatementInLoopNegative.kt"),
    UnconditionalJumpStatementInLoopPositive("/cases/UnconditionalJumpStatementInLoopPositive.kt"),
    NestedClassVisibilityPositive("/cases/NestedClassVisibilityPositive.kt"),
    NestedClassVisibilityNegative("/cases/NestedClassVisibilityNegative.kt"),
    TrailingWhitespaceNegative("/cases/TrailingWhitespaceNegative.kt"),
    TrailingWhitespacePositive("/cases/TrailingWhitespacePositive.kt"),
    NoTabsNegative("/cases/NoTabsNegative.kt"),
    NoTabsPositive("/cases/NoTabsPositive.kt"),
    UnusedPrivateMemberPositive("/cases/UnusedPrivateMemberPositive.kt"),
    UnusedPrivateMemberNegative("/cases/UnusedPrivateMemberNegative.kt");

    fun path(): Path = Paths.get(resource(file))

    fun getKtFileContent(): KtFileContent {
        val file = compileForTest(path())
        val lines = file.text.splitToSequence("\n")
        return KtFileContent(file, lines)
    }
}
