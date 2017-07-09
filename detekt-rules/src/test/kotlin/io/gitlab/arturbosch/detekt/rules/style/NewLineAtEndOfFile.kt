package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Java6Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

class NewLineAtEndOfFileSpec : Spek({

  given("a kt file containing new space at end") {
    it("should not flag it") {
      assertThat(NewLineAtEndOfFile().lint(Case.NewLineAtEndOfFile.path())).hasSize(0)
    }
  }

  given("a kt file not containing new space at end") {
    it("should flag it") {
      assertThat(NewLineAtEndOfFile().lint("class Test")).hasSize(1)
    }
  }

})
