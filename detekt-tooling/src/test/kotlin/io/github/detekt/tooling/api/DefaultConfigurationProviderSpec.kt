package io.github.detekt.tooling.api

import io.github.detekt.test.utils.createTempFileForTest
import org.assertj.core.api.Assertions.assertThatCode
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Path

class DefaultConfigurationProviderSpec : Spek({

    describe("default configuration") {

        it("loads first found instance") {
            assertThatCode {
                DefaultConfigurationProvider.load()
                    .copy(createTempFileForTest("test", "test"))
            }.doesNotThrowAnyException()
        }
    }
})

internal class TestConfigurationProvider : DefaultConfigurationProvider {

    override fun copy(targetLocation: Path) {
        // nothing
    }
}
