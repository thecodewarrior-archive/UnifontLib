package com.thecodewarrior.unifontlib.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.output.TermUi

//fun main(args: Array<String>) {
//    val list = GlyphList()
//    list.read(Paths.get("unifont-base.hex"))
//    list.write(Paths.get("unifont-base-out.hex"))
//}

class UnifontCommand: CliktCommand() {
    init {
        subcommands(ExportBMP(), Download(), Guides())
    }

    override fun run() = Unit
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
