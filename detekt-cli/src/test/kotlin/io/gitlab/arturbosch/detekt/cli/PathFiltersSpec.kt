package io.gitlab.arturbosch.detekt.cli

import io.github.detekt.test.utils.NullPrintStream
import io.gitlab.arturbosch.detekt.api.internal.PathFilters
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Paths

class PathFiltersSpec : Spek({

    describe("parse different filter settings") {

        it("should load single filter") {
            val filters = CliArgs { excludes = "**/one/**" }.toSpecFilters()
            assertThat(filters?.isIgnored(Paths.get("/one/path"))).isTrue()
            assertThat(filters?.isIgnored(Paths.get("/two/path"))).isFalse()
        }

        describe("parsing with different separators") {

            it("should load multiple comma-separated filters with no spaces around commas") {
                val filters = CliArgs { excludes = "**/one/**,**/two/**,**/three" }.toSpecFilters()
                assertSameFiltersIndependentOfSpacingAndSeparater(filters)
            }

            it("should load multiple semicolon-separated filters with no spaces around semicolons") {
                val filters = CliArgs { excludes = "**/one/**;**/two/**;**/three" }.toSpecFilters()
                assertSameFiltersIndependentOfSpacingAndSeparater(filters)
            }

            it("should load multiple comma-separated filters with spaces around commas") {
                val filters = CliArgs { excludes = "**/one/** ,**/two/**, **/three" }.toSpecFilters()
                assertSameFiltersIndependentOfSpacingAndSeparater(filters)
            }

            it("should load multiple semicolon-separated filters with spaces around semicolons") {
                val filters = CliArgs { excludes = "**/one/** ;**/two/**; **/three" }.toSpecFilters()
                assertSameFiltersIndependentOfSpacingAndSeparater(filters)
            }

            it("should load multiple mixed-separated filters with no spaces around separators") {
                val filters = CliArgs { excludes = "**/one/**,**/two/**;**/three" }.toSpecFilters()
                assertSameFiltersIndependentOfSpacingAndSeparater(filters)
            }

            it("should load multiple mixed-separated filters with spaces around separators") {
                val filters = CliArgs { excludes = "**/one/** ,**/two/**; **/three" }.toSpecFilters()
                assertSameFiltersIndependentOfSpacingAndSeparater(filters)
            }
        }

        it("should ignore empty and blank filters") {
            val filters = CliArgs { excludes = " ,,**/three" }.toSpecFilters()
            assertThat(filters?.isIgnored(Paths.get("/three"))).isTrue()
            assertThat(filters?.isIgnored(Paths.get("/root/three"))).isTrue()
            assertThat(filters?.isIgnored(Paths.get("/one/path"))).isFalse()
            assertThat(filters?.isIgnored(Paths.get("/two/path"))).isFalse()
            assertThat(filters?.isIgnored(Paths.get("/three/path"))).isFalse()
        }
    }
})

private fun CliArgs.toSpecFilters(): PathFilters? {
    val spec = this.createSpec(NullPrintStream(), NullPrintStream()).projectSpec
    return PathFilters.of(spec.includes.toList(), spec.excludes.toList())
}

// can parse pattern **/one/**,**/two/**,**/three
private fun assertSameFiltersIndependentOfSpacingAndSeparater(filters: PathFilters?) {
    assertThat(filters?.isIgnored(Paths.get("/one/path"))).isTrue()
    assertThat(filters?.isIgnored(Paths.get("/two/path"))).isTrue()
    assertThat(filters?.isIgnored(Paths.get("/three"))).isTrue()
    assertThat(filters?.isIgnored(Paths.get("/root/three"))).isTrue()
    assertThat(filters?.isIgnored(Paths.get("/three/path"))).isFalse()
}
