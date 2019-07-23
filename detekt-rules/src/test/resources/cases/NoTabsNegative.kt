package cases

class NoTabsNegative {

    fun methodOk() {
        println("A message")
    }

    val str = "A \t tab	"
    val multiStr = """A \t tab	"""
}
