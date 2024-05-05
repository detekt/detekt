package io.gitlab.arturbosch.detekt.api

import io.gitlab.arturbosch.detekt.test.location
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.io.path.Path
import kotlin.io.path.absolute

class CodeSmellSpec {

    @Test
    fun `toString contains all information`() {
        val codeSmell = CodeSmell(
            entity = Entity(
                name = "TestEntity",
                signature = "TestEntitySignature",
                location = Location(
                    source = SourceLocation(1, 1),
                    endSource = SourceLocation(1, 1),
                    text = TextLocation(0, 0),
                    path = Path("/").absolute().resolve("Users/tester/detekt/TestFile.kt"),
                ),
                ktElement = null
            ),
            message = "TestMessage"
        )

        assertThat(codeSmell.toString()).isEqualTo(
            "CodeSmell(entity=Entity(name=TestEntity, signature=TestEntitySignature, " +
                "location=Location(source=1:1, endSource=1:1, text=0:0, " +
                "path=${codeSmell.location.path}), ktElement=null), message=TestMessage, " +
                "references=[])"
        )
    }
}
