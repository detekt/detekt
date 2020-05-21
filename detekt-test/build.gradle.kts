dependencies {
    api(kotlin("stdlib-jdk8"))
    api(project(":detekt-core"))
    api(project(":detekt-test-utils"))
    implementation(kotlin("script-runtime"))
    implementation(kotlin("script-util"))
    implementation(kotlin("scripting-compiler-embeddable"))
    implementation("org.assertj:assertj-core:${Versions.ASSERTJ}")
}
