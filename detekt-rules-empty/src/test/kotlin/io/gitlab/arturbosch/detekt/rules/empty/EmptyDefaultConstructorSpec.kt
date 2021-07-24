package io.gitlab.arturbosch.detekt.rules.empty

import io.github.detekt.test.utils.compileForTest
import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

internal class EmptyDefaultConstructorSpec : Spek({

    describe("EmptyDefaultConstructor rule") {

        it("findsEmptyDefaultConstructor") {
            val rule = EmptyDefaultConstructor(Config.empty)
            val file = compileForTest(resourceAsPath("EmptyDefaultConstructorPositive.kt"))
            Assertions.assertThat(rule.lint(file)).hasSize(2)
        }

        it("doesNotFindEmptyDefaultConstructor") {
            val rule = EmptyDefaultConstructor(Config.empty)
            val file = compileForTest(resourceAsPath("EmptyDefaultConstructorNegative.kt"))
            Assertions.assertThat(rule.lint(file)).isEmpty()
        }
    }
})
