dependencies {
    api(kotlin("stdlib-jdk8"))
    api(project(":detekt-core"))
    implementation(kotlin("script-runtime"))
    implementation(kotlin("script-util"))
    implementation(kotlin("scripting-compiler-embeddable"))
    implementation("org.assertj:assertj-core:${Versions.ASSERTJ}")
}
