package dev.detekt.gradle.invoke

import dev.detekt.gradle.internal.ClassLoaderCache
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
                .invokeCli(emptyList(), emptySet(), "detekt", ignoreFailures = false)
        }.isInstanceOf(GradleException::class.java)
            .hasMessageContaining("testing reflection wrapper...")
    }
}
