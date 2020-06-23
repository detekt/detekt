package io.github.detekt.tooling.api

import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class VersionProviderSpec : Spek({

    describe("version provider") {

        it("retries current running detekt version") {
            assertThat(VersionProvider.load().current()).isEqualTo("unknown")
        }
    }
})

internal class TestVersionProvider : VersionProvider {

    override fun current(): String = "unknown"
}
