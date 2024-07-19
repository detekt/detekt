package io.gitlab.arturbosch.detekt.api

import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class RuleSpec {
    @Nested
    inner class Id {
        @ParameterizedTest(name = "should allow Rule with id {0}")
        @ValueSource(
            strings = [
                "abc-def",
                "abc1-def",
                "abc-1",
                "abc-def1",
                "abc-de1f",
                "abcDef",
                "abcDef1",

                "abc-def/abcDef1",
                "abc1-def/abc-def",
                "abc-1/abc1-def",
                "abc-def1/abc-1",
                "abc-de1f/abc-def1",
                "abcDef/abc-de1f",
                "abcDef1/abcDef",
            ]
        )
        fun shouldAllowValidIds(ruleName: String) {
            assertThatCode { Rule.Id(ruleName) }
                .doesNotThrowAnyException()
        }

        @ParameterizedTest(name = "should not allow Rule with id {0}")
        @ValueSource(
            strings = [
                "abc def",
                "abc1 def",
                "ab1c def",
                "abc 1",
                "abc-",
                "abc-def-",
                "-abcDef",
                "1abcDef",

                "abc-def/abc def",
                "abc1-def/abc1 def",
                "abc-1/ab1c def",
                "abc-def1/abc 1",
                "abc-de1f/abc-",
                "abcDef/abc-def-",

                "abcDef1/abc/def",
                "abcDef1/abc/",
                "abcDef1/",
            ]
        )
        fun shouldNotAllowInvalidIds(ruleName: String) {
            assertThatCode { Rule.Id(ruleName) }
                .hasMessageStartingWith("Id '$ruleName' must match")
                .isInstanceOf(IllegalArgumentException::class.java)
        }
    }

    @Nested
    inner class Name {
        @ParameterizedTest(name = "should allow Rule with name {0}")
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
        fun shouldAllowValidNames(ruleName: String) {
            assertThatCode { Rule.Name(ruleName) }
                .doesNotThrowAnyException()
        }

        @ParameterizedTest(name = "should not allow Rule with name {0}")
        @ValueSource(
            strings = [
                "abc def",
                "abc-",
                "-abcDef",
                "1abcDef",
            ]
        )
        fun shouldNotAllowInvalidNames(ruleName: String) {
            assertThatCode { Rule.Name(ruleName) }
                .hasMessageStartingWith("Name '$ruleName' must match")
                .isInstanceOf(IllegalArgumentException::class.java)
        }
    }
}
