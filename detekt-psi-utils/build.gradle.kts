plugins {
    module
    id("binary-compatibility-validator")
}

dependencies {
    implementation(kotlin("compiler-embeddable"))
}
