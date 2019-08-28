package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Location
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.api.TextLocation
import io.gitlab.arturbosch.detekt.core.ModificationNotification
import io.gitlab.arturbosch.detekt.test.resource
import java.nio.file.Paths

fun createFinding(ruleSet: String = "TestSmell", fileName: String = "TestFile.kt") = CodeSmell(createIssue(ruleSet), createEntity(fileName), "TestMessage")

fun createIssue(id: String = "TestSmell") = Issue(id, Severity.CodeSmell, "For Test", Debt.FIVE_MINS)

fun createEntity(fileName: String) = Entity("TestEntity", "TestEntity", "S1", createLocation(fileName))

fun createLocation(fileName: String) = Location(SourceLocation(1, 1), TextLocation(1, 1), "", fileName)

fun createNotification() = ModificationNotification(Paths.get(resource("empty.txt")))
