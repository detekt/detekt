val assertjVersion: String by project
val atriumVersion: String by project

dependencies {
    implementation(kotlin("script-runtime"))
    implementation(kotlin("script-util"))
    implementation(kotlin("scripting-compiler-embeddable"))

    api(project(":detekt-core"))
    api("org.assertj:assertj-core:$assertjVersion")
    api("ch.tutteli.atrium:atrium-fluent-en_GB:$atriumVersion")
    api("ch.tutteli.atrium:atrium-api-fluent-en_GB-jdk8:$atriumVersion")
}
