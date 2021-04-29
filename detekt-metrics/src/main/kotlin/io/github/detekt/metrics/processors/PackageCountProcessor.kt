package io.github.detekt.metrics.processors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.gitlab.arturbosch.detekt.api.ProjectMetric
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

class PackageCountProcessor : FileProcessListener {

    private val visitor = PackageCountVisitor()
    private val key = numberOfPackagesKey

    override fun onProcess(file: KtFile, bindingContext: BindingContext) {
        file.accept(visitor)
    }

    override fun onFinish(files: List<KtFile>, result: Detektion, bindingContext: BindingContext) {
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
        val packageName = file.packageFqNameByTree.toString()
        file.putUserData(numberOfPackagesKey, packageName)
    }
}
