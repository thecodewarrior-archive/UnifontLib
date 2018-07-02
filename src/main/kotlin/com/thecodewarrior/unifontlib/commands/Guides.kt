package com.thecodewarrior.unifontlib.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.PrintMessage
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import com.thecodewarrior.unifontlib.Glyph
import com.thecodewarrior.unifontlib.GlyphList
import com.thecodewarrior.unifontlib.Images
import com.thecodewarrior.unifontlib.Text
import com.thecodewarrior.unifontlib.utils.*
import java.awt.Color
import java.awt.Graphics
import java.awt.color.ColorSpace
import java.awt.image.BufferedImage
import java.awt.image.IndexColorModel
import java.io.File
import java.nio.ByteBuffer
import javax.imageio.ImageIO
import kotlin.math.max
import kotlin.math.min

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
    val hex by option("-h", "--hex", help = "The the source .hex file. Defaults to unifont.hex").file().default(File("unifont.hex"))
    val flip by option("-f", "--flip", help = "Flips the glyphs to go left to right, then top to bottom.")
            .flag("-F", "--unflip")


    override fun run() {
        val image = BufferedImage(604, 561, BufferedImage.TYPE_BYTE_BINARY,
                IndexColorModel(Color(0xffffff), Color(0x000000), Color(0xffbfbf), Color(0xc0ffff))
        )

        val c = Color(ColorSpace.getInstance(ColorSpace.CS_sRGB), floatArrayOf(1f, 1f, 1f), 0f)

        val glyphs = GlyphList()
        glyphs.read(hex.toPath())

        val g = image.graphics
        g.color = Color.WHITE
        g.drawRect(0, 0, image.width, image.height)

        drawMetadata(g)
        drawAxes(g)
        drawGuides(g)
        drawGlyphs(g, glyphs)

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
        val prefixText = "U+%04X${Text.X_CROSS}${Text.X_CROSS}".format(prefix)
        Text.drawText(g, 8, 15, prefixText)

        for(i in 0..15) { // vertical axis
            val text =
                    if(this.flip)
                        "%X${Text.X_CROSS}".format(i) // 0x - Fx
                    else
                        "${Text.X_CROSS}%X".format(i) // x0 - xF
            var y = Guides.gridStart.yi + Guides.gridSize.yi/2 - 8
            val x = Guides.gridStart.xi - 3 - 16
            y += (Guides.gridSize.xi-1) * i
            Text.drawText(g, x, y, text)
        }

        for(i in 0..15) { // horizontal axis
            val text =
                    if(this.flip)
                        "${Text.X_CROSS}%X".format(i) // x0 - xF
                    else
                        "%X".format(i) // 0x - Fx
            val y = Guides.gridStart.yi - 2 - 16
            var x = Guides.gridStart.xi + Guides.gridSize.xi/2 - 8
            x += (Guides.gridSize.xi-1) * i
            Text.drawText(g, x, y, text)
        }
    }

    private fun drawGuides(g: Graphics) {
        val guide = Images["glyph_guide_box"]

        for(xIndex in 0 until 16) {
            for(yIndex in 0 until 16) {
                val pos = Guides.gridStart + (Guides.gridSize - vec(1, 1)) * vec(xIndex, yIndex)
                g.drawImage(guide, pos.xi, pos.yi, null)
            }
        }
    }

    private fun drawGlyphs(g: Graphics, glyphs: GlyphList) {
        for(xIndex in 0 until 16) {
            for(yIndex in 0 until 16) {
                val codepoint = (prefix shl 8) or if(flip)
                    yIndex shl 4 or xIndex
                else
                    xIndex shl 4 or yIndex
                val glyph = glyphs.glyphs[codepoint] ?: continue
                val gridPos = Guides.gridStart + (Guides.gridSize - vec(1, 1)) * vec(xIndex, yIndex)
                val glyphX = when(glyph.image.width) {
                    8, 16, 24 -> 8
                    32 -> 0
                    else -> 0
                }
                val glyphY = when(glyph.image.height) {
                    8 -> 16
                    16 -> 8
                    24, 32 -> 0
                    else -> 0
                }

                g.drawImage(glyph.image, gridPos.xi + glyphX + 1, gridPos.yi + glyphY + 1, null)
            }
        }
    }
}

class ImportGuides: CliktCommand(
        name="import"
) {
    val image by argument(name = "image", help = "The source image file. Image type is inferred from extension.").file()
    val prefix by option("-p", "--prefix", help = "The codepoint prefix in hex. The input image will be interpreted to " +
            "contain the codepoints from U+xxxx00 to U+xxxxFF. This value will be read from the image's metadata bars " +
            "if not specified.").hex()
    val flip by option("-f", "--flip", help = "Flips the glyph order to go left to right, then top to bottom. This value " +
            "will be read from the image's metadata bars if not specified").nullableFlag("-F", "--unflip")
    val hex by option("-h", "--hex", help = "The output hex file. By default it outputs to a .hex file with the same " +
            "directory and name as the image, except with the .hex extension.").file()
    val append by option("-a", "--append", help = "Append to the .hex, overwriting individual characters as opposed " +
            "to the entire file").file()

    override fun run() {
        val image = ImageIO.read(this.image)

        // use command line arguments or, if they aren't supplied, read the metadata bars from the image.
        val prefix = prefix ?: readMetadataLine(image, row = 0, bits = 16)
                ?: throw PrintMessage("Prefix metadata bar is corrupt. Please specify explicitly with --prefix")
        val flip = flip ?: readMetadataLine(image, row = 1, bits = 1)?.let { it == 1 }
                ?: throw PrintMessage("Flip metadata bar is corrupt. Please specify explicitly with --flip or --unflip")

        val glyphs = GlyphList()
        readGlyphs(image, prefix, flip, glyphs)

        val output = hex ?: this.image.absoluteFile.parentFile.resolve(this.image.nameWithoutExtension + ".hex")

        glyphs.write(output.toPath())
    }

    private fun readMetadataLine(image: BufferedImage, row: Int, bits: Int): Int? {
        val y = 3 + row * 2
        var x = 3
        var value = 0

        val leftEdgeIntact = image.isColor(x-1, y-1, 1, 3, Color.BLACK)
        val rightEdgeIntact = image.isColor(x+bits, y-1, 1, 3, Color.BLACK)
        val topEdgeIntact = image.isColor(x, y-1, bits, 1, Color.BLACK)
        val bottomEdgeIntact = image.isColor(x, y+1, bits, 1, Color.BLACK)
        if(!(leftEdgeIntact && rightEdgeIntact && topEdgeIntact && bottomEdgeIntact)) {
            return null
        }

        for(i in 0 until bits) {
            val mask = 1 shl (15-i)
            // off bit = white, on bit = black. Not the other way round because white is background, black is foreground
            if(image.isColor(x, y, Color.BLACK)) {
                value = value or mask
            } else if(!image.isColor(x, y, Color.WHITE)) {
                return null
            }
            x++
        }

        return value
    }

    private fun readGlyphs(image: BufferedImage, prefix: Int, flip: Boolean, glyphs: GlyphList) {
        for(xIndex in 0 until 16) {
            for(yIndex in 0 until 16) {
                val gridPos = Guides.gridStart + (Guides.gridSize - vec(1, 1)) * vec(xIndex, yIndex)
                val glyphImage = readGlyph(image, gridPos)
                val codepoint = (prefix shl 8) or if(flip)
                    yIndex shl 4 or xIndex
                else
                    xIndex shl 4 or yIndex
                glyphs.glyphs[codepoint] = Glyph(codepoint, glyphImage)
            }
        }
    }

    private fun readGlyph(image: BufferedImage, gridPos: Vec2d): BufferedImage {
        val gridSubimage = image.getSubimage(gridPos.xi + 1, gridPos.yi + 1,
                Guides.gridSize.xi - 2, Guides.gridSize.yi - 2)
        var left = Int.MAX_VALUE
        var right = Int.MIN_VALUE
        var top = Int.MAX_VALUE
        var bottom = Int.MIN_VALUE

        gridSubimage.pixels().forEach { (x, y, color) ->
            if(color == Color.BLACK.rgb) {
                left = min(left, x)
                right = max(right, x)
                top = min(top, x)
                bottom = max(bottom, x)
            }
        }

        val height = 16

        if(left == Int.MAX_VALUE && right == Int.MIN_VALUE && top == Int.MAX_VALUE && bottom == Int.MIN_VALUE) {
            return BufferedImage(8, height, BufferedImage.TYPE_BYTE_BINARY, Glyph.COLOR_MODEL)
        }

        val width = when {
            left < 8 -> 32
            right < 16 -> 8
            right < 24 -> 16
            right < 32 -> 24
            else -> throw RuntimeException("Somehow the right >= 32 (it is $right). This shouldn't happen.")
        }

        val inGridY = 8
        val inGridX = if(width == 32) 0 else 8

        val glyphImage = BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY, Glyph.COLOR_MODEL)

        val glyphSubimage = gridSubimage.getSubimage(inGridX, inGridY, width, height).pixels()
        glyphSubimage.forEach { (x, y, color) ->
            if(color == Color.BLACK.rgb) {
                glyphImage.setRGB(x, y, Color.BLACK.rgb)
            }
        }

        return glyphImage
    }
}
