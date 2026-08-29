package dev.detekt.rules.standardlibrary

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.internal.DefaultRuleSetProvider

/**
 * The Standard Library (stdlib) ruleset provides rules that assert the correct usage of the classes in the stdlib.
 */
@ActiveByDefault(since = "2.0.0")
class StandardLibraryProvider : DefaultRuleSetProvider {

    override val ruleSetId = RuleSetId("standard-library")

    override fun instance(): RuleSet =
        RuleSet(
            ruleSetId,
            listOf(
                ::AlsoCouldBeApply,
                ::ArrayPrimitive,
                ::CharArrayToStringCall,
                ::CouldBeSequence,
                ::DontDowncastCollectionTypes,
                ::DoubleMutabilityForCollection,
                ::ForEachOnRange,
                ::IteratorHasNextCallsNextMethod,
                ::IteratorNotThrowingNoSuchElementException,
                ::MapGetWithNotNullAssertionOperator,
                ::MissingUseCall,
                ::MultilineRawStringIndentation,
                ::NestedScopeFunctions,
                ::RedundantHigherOrderMapUsage,
                ::ReplaceSafeCallChainWithRun,
                ::TrimMultilineRawString,
                ::UnnecessaryAny,
                ::UnnecessaryApply,
                ::UnnecessaryFilter,
                ::UnnecessaryLet,
                ::UnnecessaryReversed,
                ::UseAnyOrNoneInsteadOfFind,
                ::UseCheckNotNull,
                ::UseCheckOrError,
                ::UseEmptyCounterpart,
                ::UseIfEmptyOrIfBlank,
                ::UseIsNullOrEmpty,
                ::UselessCallOnNotNull,
                ::UseLet,
                ::UseOrEmpty,
                ::UseRequire,
                ::UseRequireNotNull,
                ::UseSumOfInsteadOfFlatMapSize,
            )
        )
}
