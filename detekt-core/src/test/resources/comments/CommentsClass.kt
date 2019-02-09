package comments

@Suppress("Unused")
class CommentsClass {

    /**
     * Doc comment
     *
     * @param args
     */
    fun x(args: String) { // comment total: 10
        /*
        comment
        */
        //Comment

        println(args)

        println("/* no comment */")
        println("// no comment //")
    }
}
