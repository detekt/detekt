package io.github.detekt.tooling.out

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class YamlSpec {

    @Nested
    inner class `keyValue` {
        @Test
        fun `renders key and value as provided`() {
            val result = yaml { keyValue() { "key" to "value" } }
            assertThat(result).isEqualTo("key: value")
        }
    }

    @Nested
    inner class `list` {

        @Test
        fun `renders single element`() {
            val given = listOf("value")
            val result = yaml { list("key", given) }
            val expected = """key:
                |  - 'value'
            """.trimMargin()
            assertThat(result).isEqualTo(expected)
        }

        @Test
        fun `renders multiple elements`() {
            val given = listOf("value 1", "value 2")
            val result = yaml { list("key", given) }
            val expected = """key:
                |  - 'value 1'
                |  - 'value 2'
            """.trimMargin()
            assertThat(result).isEqualTo(expected)
        }

        @Test
        fun `quotes a value containing special characters`() {
            val given = listOf("val*ue1", "val|ue2", "val\$ue3")
            val result = yaml { list("key", given) }
            val expected = """key:
                |  - 'val*ue1'
                |  - 'val|ue2'
                |  - 'val${"$"}ue3'
            """.trimMargin()
            assertThat(result).isEqualTo(expected)
        }

        @Test
        fun `quotes a blank value`() {
            val given = listOf("   ")
            val result = yaml { list("key", given) }
            val expected = """key:
                |  - '   '
            """.trimMargin()
            assertThat(result).isEqualTo(expected)
        }

        @Test
        fun `does not add quotes when value is already enclosed in quotes`() {
            val given = listOf("'val*ue1'", "\"val|ue2\"", "\"\"", "''")
            val result = yaml { list("key", given) }
            val expected = """key:
                |  - 'val*ue1'
                |  - "val|ue2"
                |  - ""
                |  - ''
            """.trimMargin()
            assertThat(result).isEqualTo(expected)
        }
    }
}
