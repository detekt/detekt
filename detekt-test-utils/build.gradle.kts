dependencies {
    api(kotlin("stdlib-jdk8"))
    api("org.assertj:assertj-core")
    implementation(project(":detekt-parser"))
    implementation(project(":detekt-psi-utils"))
    implementation(kotlin("script-runtime"))
    implementation(kotlin("script-util"))
    implementation(kotlin("scripting-compiler-embeddable"))
}
