dependencies {
    compileOnly(project(":detekt-api"))
    implementation(project(":detekt-rules-complexity"))
    implementation(project(":detekt-rules-coroutines"))
    implementation(project(":detekt-rules-documentation"))
    implementation(project(":detekt-rules-empty"))
    implementation(project(":detekt-rules-errorprone"))
    implementation(project(":detekt-rules-exceptions"))
    implementation(project(":detekt-rules-naming"))
    implementation(project(":detekt-rules-performance"))
    implementation(project(":detekt-rules-style"))

    testImplementation(project(":detekt-core"))
    testImplementation(project(":detekt-parser"))
    testImplementation(project(":detekt-test"))
}
