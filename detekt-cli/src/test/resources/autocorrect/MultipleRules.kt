class Test {


    val foo =
        listOf(1, 2, 3).
        filter { it > 2 }!!.
        takeIf { it.count() > 100 }?.
        sum()
    val foobar =
        foo() ?:
        bar




}
