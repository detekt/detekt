val assertjVersion: String by project

dependencies {
	implementation(kotlin("compiler-embeddable"))
	implementation(project(":detekt-core"))
	implementation("org.assertj:assertj-core:$assertjVersion")
}
