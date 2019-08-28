package io.gitlab.arturbosch.detekt.api

/**
 * [FileBasedConsoleReport] is an alternate for [FindingsReport]'.
 * `consoleGroupByRule` should be configured as false
 * in the 'console-reports' property of a detekt yaml config.
 * else [FindingsReport] is the default console report
 */
abstract class FileBasedConsoleReport : ConsoleReport()
