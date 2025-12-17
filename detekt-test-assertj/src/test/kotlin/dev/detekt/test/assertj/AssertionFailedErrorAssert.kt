package dev.detekt.test.assertj

import org.assertj.core.api.AbstractThrowableAssert
import org.opentest4j.AssertionFailedError

fun <T : Throwable> AbstractThrowableAssert<*, T>.isInstanceOfAssertionFailedError(): AssertionFailedErrorAssert {
    isInstanceOf(AssertionFailedError::class.java)
    return AssertionFailedErrorAssert(actual() as AssertionFailedError)
}

class AssertionFailedErrorAssert(
    actual: AssertionFailedError,
) : AbstractThrowableAssert<AssertionFailedErrorAssert, AssertionFailedError>(
    actual,
    AssertionFailedErrorAssert::class.java
) {
    fun hasActual(actual: Any) =
        apply {
            if (this.actual.actual.ephemeralValue != actual) {
                throw failureWithActualExpected(
                    this.actual.actual.ephemeralValue,
                    actual,
                    """Expected AssertionFailedError.actual to be "$actual" but was "${this.actual.actual.ephemeralValue}""""
                )
            }
        }

    fun hasExpected(expected: Any) =
        apply {
            if (this.actual.expected.ephemeralValue != expected) {
                throw failureWithActualExpected(
                    this.actual.expected.ephemeralValue,
                    expected,
                    """Expected AssertionFailedError.expected to be "$expected" but was "${this.actual.expected.ephemeralValue}""""
                )
            }
        }
}
