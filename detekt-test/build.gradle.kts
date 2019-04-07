val assertjVersion: String by project

dependencies {
    implementation(kotlin("script-runtime"))
    implementation(kotlin("compiler-embeddable"))
    implementation(kotlin("script-util"))

    api(project(":detekt-core"))
    implementation("org.assertj:assertj-core:$assertjVersion")
}
