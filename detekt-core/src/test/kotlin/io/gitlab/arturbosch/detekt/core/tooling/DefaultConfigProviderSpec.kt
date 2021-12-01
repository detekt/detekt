package io.gitlab.arturbosch.detekt.core.tooling

import io.github.detekt.test.utils.createTempFileForTest
import io.github.detekt.test.utils.resourceAsPath
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

internal class DefaultConfigProviderSpec : Spek({

    describe("defaultConfigProvider") {

        it("get") {
            val config = DefaultConfigProvider().get()

            assertThat(config.parentPath).isNull()
            assertThat(config.subConfig("build").valueOrNull<Int>("maxIssues")).isEqualTo(0)
        }

        it("copy") {
            val path = createTempFileForTest("test", "test")
            DefaultConfigProvider().copy(path)

            assertThat(path)
                .hasSameTextualContentAs(resourceAsPath("default-detekt-config.yml"))
        }
    }
})
