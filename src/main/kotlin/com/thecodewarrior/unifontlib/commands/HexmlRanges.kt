package com.thecodewarrior.unifontlib.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.types.file
import com.thecodewarrior.unifontlib.utils.DISTINCT_COLORS
import com.thecodewarrior.unifontlib.utils.codepointHex
import java.awt.Color
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.BufferedInputStream
import java.io.File
import javax.imageio.ImageIO

class HexmlRanges: CliktCommand(
        name = "genHexmlRanges",
        epilog = "Generates the hexml for the codepoint ranges present in the specified file"
) {
    val output by argument(name = "output", help = "The image file to output. File type is inferred from extension").file()
    val files by argument(name = "hex", help = "The .hex file to generate ranges for").file().multiple()

    override fun run() {

        files.forEachIndexed { index, file ->

            val image = BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB)

            val color = DISTINCT_COLORS[index % DISTINCT_COLORS.size]
            val lines = file.readLines()
            val codepoints = lines.map { it.split(':')[0].toInt(16) }.sorted()
            codepoints.forEach {
                val x = it and 0xFF
                val y = (it shr 8) and 0xFF
                image.setRGB(x, y, color.rgb)
            }

            ImageIO.write(image, output.extension, output.resolveSibling(
                    "${output.nameWithoutExtension}_${file.nameWithoutExtension}.${output.extension}"
            ))

        }

//        hexmlRangesLines(file).forEach(::println)
    }

    fun fillRange(g: Graphics, range: IntRange) {
        if(range.start > 0xFFFF) return

        var currentStart = range.first
        while(currentStart <= range.last) {
            var rowEnd = (currentStart and 0xFF00) or 0xFF
            if (rowEnd >= range.last) {
                rowEnd = range.last
            }

            g.drawRect(currentStart and 0xFF, currentStart shr 8 and 0xFF, rowEnd - currentStart + 1, 1)
            currentStart = rowEnd + 1
            if (rowEnd == range.last)
                return
        }
    }

//    fun hexmlRangesLines(ranges: List<IntRange>): List<String> {
//
//        val xmlLines = listOf("<hex file=\"$file\">") +
//                rangesXml(ranges).map { "    $it" } +
//                listOf("    <not>") +
//                rangesXml(notRanges).map { "        $it" } +
//                listOf("    </not>") +
//                listOf("</hex>")
//
//        return xmlLines.map { "    $it" }
//    }
//
//    fun rangesXml(ranges: List<IntRange>): List<String> {
//        val rangeTags = ranges.filter { it.start != it.endInclusive && it.count() > 4 }.map {
//            """<range from="${it.start.codepointHex()}" to="${it.endInclusive.codepointHex()}"/>"""
//        }
//        val singlesLines = ranges.filter { it.start == it.endInclusive || it.count() < 4 }.map { range ->
//            "    " + range.joinToString(" ") { it.codepointHex() }
//        }
//
//        return rangeTags +
//                listOf("<codepoints>") +
//                singlesLines +
//                listOf("</codepoints>")
//    }
//
//    fun readRanges(file: File): List<IntRange> {
//        return ranges
//    }
}

