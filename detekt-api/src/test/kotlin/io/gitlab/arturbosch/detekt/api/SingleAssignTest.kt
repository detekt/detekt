package io.gitlab.arturbosch.detekt.api

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class SingleAssignTest : Spek({
  describe("value is unset") {
    var unassigned: Int by SingleAssign()
    it("should fail when value is retrieved") {
      assertFailsWith(IllegalStateException::class) {
        @Suppress("UNUSED_EXPRESSION")
        unassigned
      }
    }

    it("should succeed when value is assigned") {
      unassigned = 15
    }
  }

  describe("value is set") {
    var assigned: Int by SingleAssign()
    assigned = 15

    it("should succeed when value is retrieved") {
      assertEquals(15, assigned)
    }

    it("should fail when value is assigned") {
      assertFailsWith<IllegalStateException> { assigned = -1 }
    }
  }
})
