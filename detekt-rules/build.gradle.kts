plugins {
    module
}

dependencies {
    runtimeOnly(project(":detekt-rules-complexity"))
    runtimeOnly(project(":detekt-rules-coroutines"))
    runtimeOnly(project(":detekt-rules-documentation"))
    runtimeOnly(project(":detekt-rules-empty"))
    runtimeOnly(project(":detekt-rules-errorprone"))
    runtimeOnly(project(":detekt-rules-exceptions"))
    runtimeOnly(project(":detekt-rules-naming"))
    runtimeOnly(project(":detekt-rules-performance"))
    runtimeOnly(project(":detekt-rules-style"))

    testImplementation(project(":detekt-core"))
    testImplementation(project(":detekt-test"))
    testImplementation(project(":detekt-rules-complexity"))
    testImplementation(project(":detekt-rules-coroutines"))
    testImplementation(project(":detekt-rules-documentation"))
    testImplementation(project(":detekt-rules-empty"))
    testImplementation(project(":detekt-rules-errorprone"))
    testImplementation(project(":detekt-rules-exceptions"))
    testImplementation(project(":detekt-rules-naming"))
    testImplementation(project(":detekt-rules-performance"))
    testImplementation(project(":detekt-rules-style"))
}

tasks.build { finalizedBy(":detekt-generator:generateDocumentation") }
