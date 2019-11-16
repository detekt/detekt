package cases

@Suppress("unused")
class TrailingWhitespaceNegative {

    fun myFunction() {
        println("A message")
        println("Another message") ;
    }

    // Indents inside multi-line strings should not be reported
    val multiLineStringWithIndents = """
        Should ignore indent on the next line
        
        Should ignore indent on the previous line
    """.trimIndent()

}
