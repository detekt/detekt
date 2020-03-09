package io.gitlab.arturbosch.detekt.api

import io.gitlab.arturbosch.detekt.test.resource
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Paths

class YamlConfigSpec : Spek({

    describe("yaml config loader") {

        it("loads the config from a given yaml file") {
            val path = Paths.get(resource("detekt.yml"))
            val config = YamlConfig.load(path)
            assertThat(config).isNotNull
        }

        it("loads the config from a given text file") {
            val path = Paths.get(resource("detekt.txt"))
            val config = YamlConfig.load(path)
            assertThat(config).isNotNull
        }

        it("throws an exception on an non-existing file") {
            val path = Paths.get("doesNotExist.yml")
            assertThatIllegalArgumentException()
                .isThrownBy { YamlConfig.load(path) }
                .withMessageStartingWith("Configuration does not exist")
        }

        it("throws an exception on a directory") {
            val path = Paths.get(resource("/config_validation"))
            assertThatIllegalArgumentException()
                .isThrownBy { YamlConfig.load(path) }
                .withMessageStartingWith("Configuration must be a file")
        }
    }
})
