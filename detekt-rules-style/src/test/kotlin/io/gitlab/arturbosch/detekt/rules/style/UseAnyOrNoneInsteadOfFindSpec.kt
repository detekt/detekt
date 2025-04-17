package io.gitlab.arturbosch.detekt.rules.style

import io.github.detekt.test.utils.KotlinEnvironmentContainer
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.lintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UseAnyOrNoneInsteadOfFindSpec(val env: KotlinEnvironmentContainer) {
    val subject = UseAnyOrNoneInsteadOfFind(Config.empty)

    @Test
    @DisplayName("Reports collections.find != null")
    fun reportCollectionsFindNotEqualToNull() {
        val code = "val x = listOf(1, 2, 3).find { it == 4 } != null"
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(1)
        assertThat(actual[0].message).isEqualTo("Use 'any' instead of 'find'")
    }

    @Test
    @DisplayName("Reports sequences.find != null")
    fun reportSequencesFindNotEqualToNull() {
        val code = "val x = sequenceOf(1, 2, 3).find { it == 4 } != null"
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    @DisplayName("Reports text.find != null")
    fun reportTextFindNotEqualToNull() {
        val code = "val x = \"123\".find { it == '4' } != null"
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    @DisplayName("Reports collections.firstOrNull != null")
    fun reportCollectionsFirstOrNullNotEqualToNull() {
        val code = "val x = arrayOf(1, 2, 3).firstOrNull { it == 4 } != null"
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(1)
        assertThat(actual[0].message).isEqualTo("Use 'any' instead of 'firstOrNull'")
    }

    @Test
    @DisplayName("Reports sequences.firstOrNull != null")
    fun reportSequencesFirstOrNullNotEqualToNull() {
        val code = "val x = sequenceOf(1, 2, 3).firstOrNull { it == 4 } != null"
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    @DisplayName("Reports text.firstOrNull != null")
    fun reportTextFirstOrNullNotEqualToNull() {
        val code = "val x = \"123\".firstOrNull { it == '4' } != null"
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    @DisplayName("Reports collections.find == null")
    fun reportCollectionsFindEqualToNull() {
        val code = "val x = setOf(1, 2, 3).find { it == 4 } == null"
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(1)
        assertThat(actual[0].message).isEqualTo("Use 'none' instead of 'find'")
    }

    @Test
    @DisplayName("Reports null != collections.find")
    fun reportNullNotEqualToCollectionsFind() {
        val code = "val x = null != listOf(1, 2, 3).find { it == 4 }"
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(1)
        assertThat(actual[0].message).isEqualTo("Use 'any' instead of 'find'")
    }

    @Test
    @DisplayName("Reports collections.find != null in extension")
    fun reportCollectionsFindNotEqualToNullInExtension() {
        val code = "fun List<Int>.test(): Boolean = find { it == 4 } != null"
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    @DisplayName("Reports collections.lastOrNull != null")
    fun reportCollectionsLastOrNullNotEqualToNull() {
        val code = "val x = listOf(1, 2, 3).lastOrNull { it == 4 } != null"
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(1)
        assertThat(actual[0].message).isEqualTo("Use 'any' instead of 'lastOrNull'")
    }

    @Test
    @DisplayName("Does not report collections.find")
    fun noReportCollectionsFind() {
        val code = "val x = listOf(1, 2, 3).find { it == 4 }"
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).isEmpty()
    }
}
