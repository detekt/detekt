package io.gitlab.arturbosch.detekt.api.v2

import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.SeverityLevel
import org.jetbrains.kotlin.psi.KtFile

interface Rule : (KtFile) -> List<Issue>{
    val id: String
    val description: String

    override fun invoke(file: KtFile): List<Issue>
}

interface Issue {
    val entity: Entity // TODO We should rethink this class. I'm using the old because I don't want to address this yet
    val message: String
    val severity: SeverityLevel
    val debt: Debt
    val autoCorrectable: Boolean
}
