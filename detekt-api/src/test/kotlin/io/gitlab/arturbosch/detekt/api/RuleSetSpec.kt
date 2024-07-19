package io.gitlab.arturbosch.detekt.api

import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class RuleSetSpec {
    @ParameterizedTest(name = "should allow RuleSet with name {0}")
    @MethodSource("getValidNames")
    fun shouldAllowValidNames(ruleSetId: String) {
        assertThatCode { RuleSet(RuleSet.Id(ruleSetId), emptyList()) }.doesNotThrowAnyException()
    }

    @ParameterizedTest(name = "should not allow RuleSet with name {0}")
    @MethodSource("getInvalidNames")
    fun shouldNotAllowInvalidNames(ruleSetId: String) {
        assertThatExceptionOfType(IllegalArgumentException::class.java).isThrownBy {
            RuleSet(
                RuleSet.Id(ruleSetId),
                emptyList()
            )
        }.withMessageStartingWith("Id '$ruleSetId' must match")
    }

    companion object {
        @JvmStatic
        fun getValidNames() = listOf(
            "abc-def",
            "abc-def",
            "abc1-def",
            "ab1c-def",
            "abc1",
            "abc-1",
            "abc-def1",
            "abc-de1f",
            "abcDef",
            "abcDef1",
        )

        @JvmStatic
        fun getInvalidNames() = listOf(
            "abc def",
            "abc1 def",
            "ab1c def",
            "abc 1",
            "abc-",
            "abc-def-",
            "-abcDef",
            "1abcDef",
        )
    }
}
