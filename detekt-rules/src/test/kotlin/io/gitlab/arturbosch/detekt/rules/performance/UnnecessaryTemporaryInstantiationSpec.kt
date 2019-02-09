package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.subject.SubjectSpek

/**
 * @author schalkms
 */
class UnnecessaryTemporaryInstantiationSpec : SubjectSpek<UnnecessaryTemporaryInstantiation>({
    subject { UnnecessaryTemporaryInstantiation() }

    describe("temporary instantiation for conversion") {
        val code = "val i = Integer(1).toString()"
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    describe("right conversion without instantiation") {
        val code = "val i = Integer.toString(1)"
        assertThat(subject.compileAndLint(code)).isEmpty()
    }
})
