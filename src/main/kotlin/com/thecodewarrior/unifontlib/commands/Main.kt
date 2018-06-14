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
        subcommands(ExportBMP(), Download())
    }

    override fun run() = Unit
}

fun main(args: Array<String>) = UnifontCommand().main(args)

