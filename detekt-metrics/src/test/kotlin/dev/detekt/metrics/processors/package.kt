package dev.detekt.metrics.processors

val default = """
    package cases
    
    /**
     * A comment
     */
    @Suppress("Unused")
    class Default
""".trimIndent()

val emptyEnum = """
    package empty
    
    @Suppress("Unused")
    enum class EmptyEnum
""".trimIndent()

val emptyInterface = """
    package empty
    
    @Suppress("Unused")
    interface EmptyInterface
""".trimIndent()

val classWithFields = """
    package fields
    
    @Suppress("unused")
    class ClassWithFields {
    
        private var x = 0
        val y = 0
    }
""".trimIndent()

val commentsClass = """
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
""".trimIndent()

val complexClass = """
    package cases
    
    import org.jetbrains.kotlin.utils.sure
    
    @Suppress("unused")
    class ComplexClass {// McCabe: 44, LLOC: 20 + 20 + 4x4
    
        class NestedClass { //14
            fun complex() { //1 +
                try {//4
                    while (true) {
                        if (true) {
                            when ("string") {
                                "" -> println()
                                else -> println()
                            }
                        }
                    }
                } catch (ex: Exception) { //1 + 3
                    try {
                        println()
                    } catch (ex: Exception) {
                        while (true) {
                            if (false) {
                                println()
                            } else { // +1
                                println()
                            }
                        }
                    }
                } finally { // 3
                    try {
                        println()
                    } catch (ex: Exception) {
                        while (true) {
                            if (false) {
                                println()
                            } else { // +1
                                println()
                            }
                        }
                    }
                }
                (1..10).forEach {
                    //1
                    println()
                }
                for (i in 1..10) { //1
                    println()
                }
            }
        }
    
        fun complex() { //1 +
            try {//4
                while (true) {
                    if (true) {
                        when ("string") {
                            "" -> println()
                            else -> println()
                        }
                    }
                }
            } catch (ex: Exception) { //1 + 3
                try {
                    println()
                } catch (ex: Exception) {
                    while (true) {
                        if (false) {
                            println()
                        } else { // +1
                            println()
                        }
                    }
                }
            } finally { // 3
                try {
                    println()
                } catch (ex: Exception) {
                    while (true) {
                        if (false) {
                            println()
                        } else { // +1
                            println()
                        }
                    }
                }
            }
            (1..10).forEach {
                //1
                println()
            }
            for (i in 1..10) { //1
                println()
            }
        }
    
        fun manyClosures() {//4
            true.let {
                true.apply {
                    true.run {
                        true.sure {
                            ""
                        }
                    }
                }
            }
        }
    
        fun manyClosures2() {//4
            true.let {
                true.apply {
                    true.run {
                        true.sure {
                            ""
                        }
                    }
                }
            }
        }
    
        fun manyClosures3() {//4
            true.let {
                true.apply {
                    true.run {
                        true.sure {
                            ""
                        }
                    }
                }
            }
        }
    
        fun manyClosures4() {//4
            true.let {
                true.apply {
                    true.run {
                        true.sure {
                            ""
                        }
                    }
                }
            }
        }
    }
""".trimIndent()
