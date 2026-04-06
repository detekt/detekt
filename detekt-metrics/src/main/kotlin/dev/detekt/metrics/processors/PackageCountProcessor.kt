package dev.detekt.metrics.processors

import com.intellij.openapi.util.Key
import dev.detekt.api.Detektion
import dev.detekt.api.FileProcessListener
import dev.detekt.api.ProjectMetric
import org.jetbrains.kotlin.psi.KtFile

class PackageCountProcessor : FileProcessListener {
    override val id: String = "PackageCountProcessor"

    override fun onFinish(files: List<KtFile>, result: Detektion): Detektion {
        val count = files
            .map { it.packageFqName }
            .distinct()
            .size
        result.add(ProjectMetric(numberOfPackagesKey.toString(), count))
        return result
    }
}

val numberOfPackagesKey = Key<String>("number of packages")
