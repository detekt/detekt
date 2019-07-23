package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class SerialVersionUIDInSerializableClassSpec : Spek({
    val subject by memoized { SerialVersionUIDInSerializableClass(Config.empty) }

    describe("SerialVersionUIDInSerializableClass rule") {

        it("reports serializable classes which do not implement the serialVersionUID correctly") {
            assertThat(subject.lint(Case.SerializablePositive.path())).hasSize(5)
        }

        it("does not report serializable classes which implement the serialVersionUID correctly") {
            assertThat(subject.lint(Case.SerializableNegative.path())).hasSize(0)
        }
    }
})
