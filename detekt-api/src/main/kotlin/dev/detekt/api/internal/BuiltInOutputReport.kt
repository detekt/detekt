package dev.detekt.api.internal

interface BuiltInOutputReport {
    /**
     * The reportId allows you to specify a custom report name for your [dev.detekt.api.OutputReport]
     * if it differs from the default ending determined by the [dev.detekt.api.OutputReport.ending].
     * For instance, a "Checkstyle" report might need a .xml extension file, but report id will be
     * `checkstyle`
     */
    val reportId: String?
        get() = null
}
