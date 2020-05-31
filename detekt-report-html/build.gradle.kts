dependencies {
    compileOnly(project(":detekt-api"))
    compileOnly(project(":detekt-metrics"))
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:${Versions.KOTLINX_HTML}") {
        exclude(group = "org.jetbrains.kotlin")
    }
    testImplementation(project(":detekt-metrics"))
    testImplementation(project(":detekt-test"))
}
