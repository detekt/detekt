package dev.detekt.tooling.api

import java.nio.file.Path
import java.util.ServiceLoader

interface BaselineProvider {
    fun of(manuallySuppressedIssues: FindingsIdList, currentIssues: FindingsIdList): Baseline
    fun read(sourcePath: Path): Baseline
    fun write(targetPath: Path, baseline: Baseline)

    companion object {

        fun load(
            classLoader: ClassLoader = BaselineProvider::class.java.classLoader,
        ): BaselineProvider = ServiceLoader.load(BaselineProvider::class.java, classLoader).first()
    }
}

typealias FindingsIdList = Set<String>
typealias FindingId = String

interface Baseline {

    val manuallySuppressedIssues: FindingsIdList
    val currentIssues: FindingsIdList

    fun contains(id: FindingId): Boolean
}
