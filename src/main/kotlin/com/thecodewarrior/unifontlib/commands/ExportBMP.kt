package com.thecodewarrior.unifontlib.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.thecodewarrior.unifontlib.CommonImages
import com.thecodewarrior.unifontlib.GlyphList
import com.thecodewarrior.unifontlib.Unifont
import java.awt.Color
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Paths
import javax.imageio.ImageIO

class ExportBMP: CliktCommand(name="pic") {

    //val includeMissing by option("-m", "--missing", help="Generate missing glyphs").file()
    val inputFile by option("-i", "--input", help="The input .hex file. Default is `unifont.hex`").file()
    val outputFile by option("-o", "--output", help="The output image. The format is inferred from the extension. " +
            "Default is `unifont.png`").file()

    val border = 32
    val imageSize = 16*256 + border
    override fun run() {
        val inputFile = inputFile ?: File("unifont.hex")
        val outputFile = outputFile ?: File("unifont.png")

        val glyphList = GlyphList()
        glyphList.read(inputFile.toPath())

        val image = BufferedImage(imageSize, imageSize, BufferedImage.TYPE_BYTE_BINARY)
        val g = image.graphics
        g.color = Color.WHITE
        g.fillRect(0, 0, image.width, image.height)
        addEdges(g)
        addGlyphs(g, glyphList)
        g.dispose()

        ImageIO.write(image, outputFile.extension, outputFile)
    }

    private fun addEdges(g: Graphics) {
        g.color = Color.BLACK
        g.drawLine(0, border-2, imageSize-1, border-2)
        g.drawLine(border-2, 0, border-2, imageSize-1)

        for(i in 0 until 256) {
            val x = border + i*16
            val y = border - 16

            val hex = "%02x".format(i).map { it.toString().toInt(16) }

            g.drawImage(CommonImages.digits[hex[0]], x+1, y+2, null)
            g.drawImage(CommonImages.digits[hex[1]], x+8, y+2, null)
            if(i and 0xf == 0xf)
                g.drawLine(x+15, y-16, x+15, y+14)
            else
                g.drawLine(x+15, y, x+15, y+14)
        }

        for(i in 0 until 256) {
            val x = border - 16
            val y = border + i*16

            val hex = "%02x".format(i).map { it.toString().toInt(16) }

            g.drawImage(CommonImages.digits[hex[0]], x, y+4, null)
            g.drawImage(CommonImages.digits[hex[1]], x+7, y+4, null)
            if(i and 0xf == 0xf)
                g.drawLine(x-16, y+15, x+14, y+15)
            else
                g.drawLine(x, y+15, x+14, y+15)
        }

    }

    private fun addGlyphs(g: Graphics, list: GlyphList) {
        val countPerProgressPoint = list.glyphs.size/64
        var count = 0
        list.glyphs.forEach { codepoint, glyph ->
            if(count % countPerProgressPoint == 0)
                print("#")
            val xIndex = codepoint and 0xFF
            val yIndex = codepoint and 0xFF00 shr 8

            val x = border + 16 * xIndex
            val y = border + 16 * yIndex

            g.drawImage(glyph.image, x, y, null)
            count++
        }
    }
}