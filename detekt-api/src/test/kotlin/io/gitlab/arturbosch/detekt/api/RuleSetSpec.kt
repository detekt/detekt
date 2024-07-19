package io.gitlab.arturbosch.detekt.api

import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class RuleSetSpec {
    @Nested
    inner class Id {
        @ParameterizedTest(name = "should allow RuleSet with id {0}")
        @ValueSource(
            strings = [
                "abc-def",
                "abc1-def",
                "abc-1",
                "abc-def1",
                "abc-de1f",
                "abcDef",
                "abcDef1",
            ]
        )
        fun shouldAllowValidIds(ruleSetId: String) {
            assertThatCode { RuleSet.Id(ruleSetId) }
                .doesNotThrowAnyException()
        }

        @ParameterizedTest(name = "should not allow RuleSet with id {0}")
        @ValueSource(
            strings = [
                "abc def",
                "abc-",
                "-abcDef",
                "1abcDef",
            ]
        )
        fun shouldNotAllowInvalidIds(ruleSetId: String) {
            assertThatCode { RuleSet.Id(ruleSetId) }
                .hasMessageStartingWith("Id '$ruleSetId' must match")
                .isInstanceOf(IllegalArgumentException::class.java)
        }
    }
}
