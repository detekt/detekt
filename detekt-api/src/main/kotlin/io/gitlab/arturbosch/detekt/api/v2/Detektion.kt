package io.gitlab.arturbosch.detekt.api.v2

import io.gitlab.arturbosch.detekt.api.Notification

// TODO should this be a class or just an interface
class Detektion(
    val findings: List<Finding> = emptyList(),
    val notifications: List<Notification> = emptyList(), // TODO do we really need this?
    val customInfo: Map<String, Any> = emptyMap(),
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Detektion

        if (findings != other.findings) return false
        if (notifications != other.notifications) return false
        if (customInfo != other.customInfo) return false

        return true
    }

    override fun hashCode(): Int {
        var result = findings.hashCode()
        result = 31 * result + notifications.hashCode()
        result = 31 * result + customInfo.hashCode()
        return result
    }

    override fun toString(): String {
        return "Detektion(findings=$findings, notifications=$notifications, customInfo=$customInfo)"
    }
}
