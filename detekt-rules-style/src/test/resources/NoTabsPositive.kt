package cases

class NoTabsPositive {
	fun methodOk() { // reports 3
		println("A message")

	}

  val str = "${		methodOk()}" // reports 1
  val multiStr = """${	methodOk()}""" // reports 1
}
