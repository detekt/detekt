dependencies {
    api(kotlin("stdlib-jdk8"))
    api("org.assertj:assertj-core")
    api("org.spekframework.spek2:spek-dsl-jvm")
    implementation(project(":detekt-parser"))
    implementation(project(":detekt-psi-utils"))
    implementation(kotlin("script-runtime"))
    implementation(kotlin("script-util"))
    implementation(kotlin("scripting-compiler-embeddable"))
}
