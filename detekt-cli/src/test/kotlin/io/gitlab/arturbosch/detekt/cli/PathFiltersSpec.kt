package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.internal.PathFilters
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.io.path.Path

class PathFiltersSpec {

    @Nested
    inner class `parsing with different nullability combinations of path filters` {
        @Test
        fun `returns an empty path filter when includes are empty and excludes are empty`() {
            val pathFilter = PathFilters.of(emptyList(), emptyList())
            assertThat(pathFilter).isNull()
        }

        @Test
        fun `parses includes correctly`() {
            val pathFilter = PathFilters.of(listOf("**/one/**", "**/two/**"), emptyList())
            assertThat(pathFilter).isNotNull
            assertThat(pathFilter?.isIgnored(Path("/one/path"))).isFalse
            assertThat(pathFilter?.isIgnored(Path("/two/path"))).isFalse
            assertThat(pathFilter?.isIgnored(Path("/three/path"))).isTrue
        }

        @Test
        fun `parses excludes correctly`() {
            val pathFilter = PathFilters.of(emptyList(), listOf("**/one/**", "**/two/**"))
            assertThat(pathFilter).isNotNull
            assertThat(pathFilter?.isIgnored(Path("/one/path"))).isTrue
            assertThat(pathFilter?.isIgnored(Path("/two/path"))).isTrue
            assertThat(pathFilter?.isIgnored(Path("/three/path"))).isFalse
        }

        @Test
        fun `parses both includes and excludes correctly`() {
            val pathFilter = PathFilters.of(listOf("**/one/**"), listOf("**/two/**"))
            assertThat(pathFilter).isNotNull
            assertThat(pathFilter?.isIgnored(Path("/one/path"))).isFalse
            assertThat(pathFilter?.isIgnored(Path("/two/path"))).isTrue
            assertThat(pathFilter?.isIgnored(Path("/three/path"))).isTrue
            assertThat(pathFilter?.isIgnored(Path("/one/two/three/path"))).isTrue
        }
    }
}
