package io.gitlab.arturbosch.detekt.invoke

import io.gitlab.arturbosch.detekt.gradle.TestFileCollection
import io.gitlab.arturbosch.detekt.internal.ClassLoaderCache
import org.assertj.core.api.Assertions.assertThatCode
import org.gradle.api.GradleException
import org.junit.jupiter.api.Test
import java.net.URLClassLoader

class DefaultCliInvokerSpec {

    @Test
    fun `catches ClassCastException and fails build`() {
        val stubbedCache = ClassLoaderCache { URLClassLoader(emptyArray()) }

        assertThatCode {
            DefaultCliInvoker(stubbedCache)
                .invokeCli(emptyList(), TestFileCollection(), "detekt", ignoreFailures = false)
        }.isInstanceOf(GradleException::class.java)
            .hasMessageContaining("testing reflection wrapper...")
    }
}
