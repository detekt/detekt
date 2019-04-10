package io.gitlab.arturbosch.detekt.cli

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import org.assertj.core.api.Assertions.assertThat
import java.io.PrintStream

internal class LOGSpec : Spek({

    describe("Logging messages") {

        val defaultLogs: () -> Unit = {
            LOG.verbose("verbose")
            LOG.debug("debug")
            LOG.info("info")
            LOG.warn("warn")
            LOG.error("error")
        }

        fun captureLog(action: () -> Unit): List<String> {
            val tempFile = createTempFile()
            PrintStream(tempFile).use {
                LOG.printer = it
                action()
            }
            return tempFile.readLines()
        }

        it ("all logs are suppressed when level = NONE") {
            LOG.level = LogLevel.NONE

            val logLines = captureLog(defaultLogs)

            assertThat(logLines.size).isEqualTo(0)
        }

        it ("everything is logged when level = VERBOSE") {
            LOG.level = LogLevel.VERBOSE

            val logLines = captureLog(defaultLogs)

            assertThat(logLines.size).isEqualTo(5)
        }

        it ("only the necessary log levels are output") {
            LOG.level = LogLevel.WARN

            val logLines = captureLog(defaultLogs)

            assertThat(logLines.size).isEqualTo(2)
            assertThat(logLines[0]).isEqualTo("warn")
            assertThat(logLines[1]).isEqualTo("error")
        }

        it ("lambdas are not executed if not necessary") {
            LOG.level = LogLevel.INFO

            var verbose = false
            var debug = false
            var info = false
            var warn = false
            var error = false
            val logLines = captureLog {
                LOG.verbose {
                    verbose = true
                    "verbose"
                }
                LOG.debug {
                    debug = true
                    "debug"
                }
                LOG.info {
                    info = true
                    "info"
                }
                LOG.warn {
                    warn = true
                    "warn"
                }
                LOG.error {
                    error = true
                    "error"
                }
            }

            assertThat(logLines.size).isEqualTo(3)
            assertThat(verbose).isFalse()
            assertThat(debug).isFalse()
            assertThat(info).isTrue()
            assertThat(warn).isTrue()
            assertThat(error).isTrue()
        }

        it ("no new lines are printed") {
            LOG.level = LogLevel.VERBOSE

            val logLines = captureLog {
                LOG.verbose(newLine = false) { "a" }
                LOG.debug(newLine = false) { "b" }
                LOG.info("c", newLine = false)
            }

            assertThat(logLines.size).isEqualTo(1)
            assertThat(logLines[0]).isEqualTo("abc")
        }
    }
})
