package com.thecodewarrior.unifontlib.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import com.thecodewarrior.unifontlib.Images
import com.thecodewarrior.unifontlib.utils.byteArrayOf
import com.thecodewarrior.unifontlib.utils.vec
import java.awt.Color
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.awt.image.IndexColorModel
import java.nio.ByteBuffer
import javax.imageio.ImageIO

// unifont guides export unifont0F.png -p 0F
// unifont guides export unifont0F.png -p 0F -h unifont.hex
// unifont guides import unifont0F.png -p 0F
// unifont guides import unifont0F.png -p 0F -h unifont.hex
class Guides: CliktCommand(
        name="guides",
        help="Export and import image files laid out for editing glyphs.",
        epilog = ""
) {
    init {
        this.subcommands(ExportGuides(), ImportGuides())
    }

    override fun run() = Unit

    companion object {
        val gridStart = vec(75, 32)
        val gridSize = vec(34, 34)
    }
}

class ExportGuides: CliktCommand(
        name="export"
) {
    val image by argument(name = "image", help = "The destination image file. Image type is inferred from extension.").file()
    val prefix by option("-p", "--prefix", help = "The codepoint prefix in hex. The output image will contain all the " +
            "codepoints from U+xxxx00 to U+xxxxFF.").hex().default(0)
    val hex by option("-h", "--hex", help = "The the source .hex file").file()
    val flip by option("-f", "--flip", help = "Flips the glyphs to go top to bottom, then left to right.")
            .flag("-F", "--unflip")


    override fun run() {
        val image = BufferedImage(604, 561, BufferedImage.TYPE_BYTE_BINARY,
                IndexColorModel(2, 4, // white, black, red, cyan
                        byteArrayOf(0xFF, 0x00, 0xFF, 0x00), //R
                        byteArrayOf(0xFF, 0x00, 0x00, 0xFF), //G
                        byteArrayOf(0xFF, 0x00, 0x00, 0xFF)  //B
                )
        )

        val g = image.graphics
        g.color = Color.WHITE
        g.drawRect(0, 0, image.width, image.height)

        drawMetadata(g)
        drawAxes(g)
//        drawGuides(g)
//        draw

        g.dispose()

        ImageIO.write(image, this.image.extension, this.image)
    }

    private fun drawMetadata(g: Graphics) {
        drawMetadataLine(g, row = 0, value = prefix, bits = 16)
        drawMetadataLine(g, row = 1, value = if(flip) 1 else 0, bits = 1)
    }

    private fun drawMetadataLine(g: Graphics, row: Int, value: Int, bits: Int) {
        val y = 3 + row * 2
        var x = 3

        g.color = Color.BLACK
        g.fillRect(x-1, y-1, bits + 2, 3)

        for(i in 0 until bits) {
            val mask = 1 shl (15-i)
            // off bit = white, on bit = black. Not the other way round because white is background, black is foreground
            val color = if(value and mask == 0) Color.WHITE else Color.BLACK
            g.drawPixel(x, y, color)
            x++
        }
    }

    private fun drawAxes(g: Graphics) {
        val prefixText = "U+%04X\u00FE\u00FE".format(prefix)
        Images.drawText(g, 7, 15, prefixText)
        // y = upper unless flipped
        // x = lower unless flipped
    }
}

class ImportGuides: CliktCommand(
        name="import"
) {
    val image by argument(name = "image", help = "The source image file. Image type is inferred from extension.").file()
    val page by option("-p", "--prefix", help = "The codepoint prefix in hex. The input image will be interpreted to " +
            "contain the codepoints from U+xxxx00 to U+xxxxFF. This value will be read from the image's metadata bars " +
            "if not specified.")
    val hex by option("-h", "--hex", help = "The output hex file. By default it outputs to a .hex file with the same " +
            "directory and name as the image, except with the .hex extension.").file()

    override fun run() {
    }
}
