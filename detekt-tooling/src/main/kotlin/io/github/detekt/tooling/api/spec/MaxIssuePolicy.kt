package io.github.detekt.tooling.api.spec

/**
 * Policy on how many issues are allowed before detekt throws an error.
 */
sealed class MaxIssuePolicy {

    class AllowAny : MaxIssuePolicy()
    class NoneAllowed : MaxIssuePolicy()
    class AllowAmount(val amount: Int) : MaxIssuePolicy()
}
