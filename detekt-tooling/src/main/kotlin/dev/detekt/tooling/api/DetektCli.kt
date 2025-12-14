package dev.detekt.tooling.api

import java.util.ServiceLoader

interface DetektCli {

    fun run(args: Array<String>): AnalysisResult

    fun run(args: Array<String>, outputChannel: Appendable, errorChannel: Appendable): AnalysisResult

    companion object {

        fun load(classLoader: ClassLoader = DetektCli::class.java.classLoader): DetektCli =
            ServiceLoader.load(DetektCli::class.java, classLoader).first()
    }
}
