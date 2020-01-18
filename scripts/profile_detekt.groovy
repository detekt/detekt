@groovy.lang.Grab("io.gitlab.arturbosch.detekt:detekt-cli:1.5.0-SNAPSHOT")
import io.gitlab.arturbosch.detekt.cli.Main

def invokeDetekt() {
    def clazz = getClass().classLoader.loadClass("io.gitlab.arturbosch.detekt.cli.Main")
    def runner = clazz.getMethod(
            "buildRunner",
            String[].class,
            PrintStream.class,
            PrintStream.class
    ).invoke(null, ['--help'] as String[], System.out, System.err)
    runner.class.getMethod("execute")
            .invoke(runner)
}

def console = System.console()
if (!console) {
    println("No System.console() available.")
    System.exit(1)
}

println("Welcome to detekt interactive run. Connect to visualvm to profile for memory leaks.")
while (true) {
    def command = console.readLine("detekt> ")
    switch (command) {
        case "run":
//            invokeDetekt()
            Main.main(['--help'] as String[])
            break
        case "exit":
            return
    }
}
