package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Location
import io.gitlab.arturbosch.detekt.api.ProjectMetric
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.api.TextLocation
import io.gitlab.arturbosch.detekt.core.ModificationNotification
import io.gitlab.arturbosch.detekt.test.resource
import java.nio.file.Paths

fun createFinding(ruleSet: String = "TestSmell") = CodeSmell(createIssue(ruleSet), createEntity(), "TestMessage")

fun createIssue(id: String = "TestSmell")= Issue(id, Severity.CodeSmell, "For Test", Debt.FIVE_MINS)

fun createEntity() = Entity("TestEntity", "TestEntity", "S1", createLocation())

fun createLocation() = Location(SourceLocation(1, 1), TextLocation(1, 1), "", "TestFile.kt")

fun createNotification() = ModificationNotification(Paths.get(resource("empty.txt")))
