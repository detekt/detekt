object Versions {

    const val DETEKT: String = "1.23.7"
    const val SNAPSHOT_NAME: String = "main"

    fun currentOrSnapshot(): String {
        if (System.getProperty("snapshot")?.toBoolean() == true) {
            return "$SNAPSHOT_NAME-SNAPSHOT"
        }
        return DETEKT
    }
}
