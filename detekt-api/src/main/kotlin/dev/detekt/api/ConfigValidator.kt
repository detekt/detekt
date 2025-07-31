package dev.detekt.api

/**
 * An extension which allows users to validate parts of the configuration.
 *
 * Rule authors can validate if specific properties do appear in their config
 * or if their value lies in a specified range.
 */
interface ConfigValidator : Extension {

    /**
     * Executes queries on given config and reports any warnings or errors via [Notification]s.
     */
    fun validate(config: Config): Collection<Notification>
}
