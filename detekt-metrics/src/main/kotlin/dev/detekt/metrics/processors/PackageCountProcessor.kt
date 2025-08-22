package dev.detekt.metrics.processors

import com.intellij.openapi.util.Key
import dev.detekt.api.DetektVisitor
import dev.detekt.api.Detektion
import dev.detekt.api.FileProcessListener
import dev.detekt.api.ProjectMetric
import org.jetbrains.kotlin.psi.KtFile

class PackageCountProcessor : FileProcessListener {

    private val visitor = PackageCountVisitor()
    private val key = numberOfPackagesKey

    override val id: String = "PackageCountProcessor"

    override fun onProcess(file: KtFile) {
        file.accept(visitor)
    }

    override fun onFinish(files: List<KtFile>, result: Detektion) {
        val count = files
            .mapNotNull { it.getUserData(key) }
            .distinct()
            .size
        result.add(ProjectMetric(key.toString(), count))
    }
}

val numberOfPackagesKey = Key<String>("number of packages")

class PackageCountVisitor : DetektVisitor() {

    override fun visitKtFile(file: KtFile) {
        val packageName = file.packageFqName.toString()
        file.putUserData(numberOfPackagesKey, packageName)
    }
}
