package io.gitlab.arturbosch.detekt.api

import io.gitlab.arturbosch.detekt.test.createCorrectableFinding
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CorrectableCodeSmellSpec {

    @Test
    fun `toString contains all information`() {
        val codeSmell: CorrectableCodeSmell = createCorrectableFinding()

        assertThat(codeSmell.toString()).isEqualTo(
            "CorrectableCodeSmell(autoCorrectEnabled=true, issue=Issue(id='TestSmell'), " +
                "entity=Entity(name=TestEntity, signature=TestEntitySignature, " +
                "location=Location(source=1:1, endSource=1:1, text=0:0, " +
                "filePath=FilePath(absolutePath=TestFile.kt, basePath=null, relativePath=null)), " +
                "ktElement=null), message=TestMessage, references=[], severity=Error, id='TestSmell')"
        )
    }
}
