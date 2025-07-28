package dev.detekt.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class YamlSpec {

    @Nested
    inner class KeyValue {
        @Test
        fun `renders key and value as provided`() {
            val result = yaml { keyValue { "key" to "value" } }
            assertThat(result).isEqualTo("key: value")
        }
    }

    @Nested
    inner class ListOfStrings {

        @Test
        fun `renders an empty list`() {
            val given = emptyList<String>()
            val result = yaml { list("key", given) }
            val expected = "key: []"
            assertThat(result).isEqualTo(expected)
        }

        @Test
        fun `renders single element`() {
            val given = listOf("value")
            val result = yaml { list("key", given) }
            val expected = """
                key:
                  - 'value'
            """.trimIndent()
            assertThat(result).isEqualTo(expected)
        }

        @Test
        fun `renders multiple elements`() {
            val given = listOf("value 1", "value 2")
            val result = yaml { list("key", given) }
            val expected = """
                key:
                  - 'value 1'
                  - 'value 2'
            """.trimIndent()
            assertThat(result).isEqualTo(expected)
        }

        @Test
        fun `quotes a value containing special characters`() {
            val given = listOf("val*ue1", "val|ue2", "val\$ue3")
            val result = yaml { list("key", given) }
            val expected = """
                key:
                  - 'val*ue1'
                  - 'val|ue2'
                  - 'val${"$"}ue3'
            """.trimIndent()
            assertThat(result).isEqualTo(expected)
        }

        @Test
        fun `quotes a blank value`() {
            val given = listOf("   ")
            val result = yaml { list("key", given) }
            val expected = """
                key:
                  - '   '
            """.trimIndent()
            assertThat(result).isEqualTo(expected)
        }

        @Test
        fun `does not add quotes when value is already enclosed in quotes`() {
            val given = listOf("'val*ue1'", "\"val|ue2\"", "\"\"", "''")
            val result = yaml { list("key", given) }
            val expected = """
                key:
                  - 'val*ue1'
                  - "val|ue2"
                  - ""
                  - ''
            """.trimIndent()
            assertThat(result).isEqualTo(expected)
        }
    }

    @Nested
    inner class ListOfMaps {

        @Test
        fun `renders an empty list of maps`() {
            val given = emptyList<Map<String, String>>()
            val result = yaml { listOfMaps("key", given) }
            val expected = "key: []"
            assertThat(result).isEqualTo(expected)
        }

        @Test
        fun `renders an list of empty maps`() {
            val given = listOf<Map<String, String>>(emptyMap(), emptyMap())
            val result = yaml { listOfMaps("key", given) }
            val expected = "key: []"
            assertThat(result).isEqualTo(expected)
        }

        @Test
        fun `renders single map with single element`() {
            val given = listOf(mapOf("name" to "value"))
            val result = yaml { listOfMaps("key", given) }
            val expected = """
                key:
                  - name: 'value'
            """.trimIndent()
            assertThat(result).isEqualTo(expected)
        }

        @Test
        fun `renders single map with multiple elements`() {
            val given = listOf(
                mapOf(
                    "name1" to "value 1",
                    "name2" to "value 2",
                    "name3" to "value 3"
                )
            )
            val result = yaml { listOfMaps("key", given) }
            val expected = """
                key:
                  - name1: 'value 1'
                    name2: 'value 2'
                    name3: 'value 3'
            """.trimIndent()
            assertThat(result).isEqualTo(expected)
        }

        @Test
        fun `renders multiple maps with multiple elements omitting empty maps`() {
            val given = listOf(
                mapOf(
                    "name1" to "value 1",
                    "name2" to "value 2"
                ),
                emptyMap(),
                mapOf(
                    "name3" to "value 3"
                ),
                mapOf(
                    "name4" to "value 4",
                    "name5" to "value 5"
                )
            )
            val result = yaml { listOfMaps("key", given) }
            val expected = """
                key:
                  - name1: 'value 1'
                    name2: 'value 2'
                  - name3: 'value 3'
                  - name4: 'value 4'
                    name5: 'value 5'
            """.trimIndent()
            assertThat(result).isEqualTo(expected)
        }

        @Test
        fun `sorts entries by key name`() {
            val given = listOf(
                mapOf(
                    "z" to "value",
                    "a" to "value",
                    "x" to "value",
                    "b" to "value",
                ),
            )
            val result = yaml { listOfMaps("key", given) }
            val expected = """
                key:
                  - a: 'value'
                    b: 'value'
                    x: 'value'
                    z: 'value'
            """.trimIndent()
            assertThat(result).isEqualTo(expected)
        }

        @Test
        fun `omits entries with null value`() {
            val given = listOf(
                mapOf(
                    "a" to "value",
                    "b" to null,
                    "c" to "value",
                ),
            )
            val result = yaml { listOfMaps("key", given) }
            val expected = """
                key:
                  - a: 'value'
                    c: 'value'
            """.trimIndent()
            assertThat(result).isEqualTo(expected)
        }

        @Test
        fun `quotes values if necessary`() {
            val given = listOf(
                mapOf(
                    "name1" to "'already quoted'",
                    "name2" to "\"also quoted\"",
                    "name3" to "should be quoted"
                )
            )
            val result = yaml { listOfMaps("key", given) }
            val expected = """
                key:
                  - name1: 'already quoted'
                    name2: "also quoted"
                    name3: 'should be quoted'
            """.trimIndent()
            assertThat(result).isEqualTo(expected)
        }
    }
}
