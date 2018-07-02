package com.thecodewarrior.unifontlib.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import java.nio.file.Paths

//fun main(args: Array<String>) {
//    val list = GlyphList()
//    list.read(Paths.get("unifont-base.hex"))
//    list.write(Paths.get("unifont-base-out.hex"))
//}

class UnifontCommand: CliktCommand() {
    init {
        subcommands(Test(), ExportBMP(), Download(), Guides(), HexmlRanges())
    }

    override fun run() = Unit
}

class Test: CliktCommand(name = "test") {
    override fun run() {
        val project = UnifontProject(Paths.get(".").toAbsolutePath())
        project.loadPage(0)
    }
}

fun main(args: Array<String>) = UnifontCommand().main(args)

// for debugging in IDEA. Instead of constantly editing the run configuration, read arguments from stdin
internal object IDEAMain {
    @JvmStatic
    fun main(args: Array<String>) {
        while (true) {
            print("> ")
            val line = readLine()?.trim() ?: break
            if (line.equals("quit", ignoreCase = true))
                break
            val stdinArgs = ArgumentTokenizer.tokenize(line)
            println()
            UnifontCommand().main(stdinArgs)
        }
    }
}
