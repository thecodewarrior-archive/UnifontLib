package com.thecodewarrior.unifontlib

import com.thecodewarrior.unifontlib.commands.IndexColorModel
import com.thecodewarrior.unifontlib.utils.Tokenizer
import com.thecodewarrior.unifontlib.utils.isColor
import java.awt.Color
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.lang.Math.floor

class Glyph(val codepoint: Int, val image: BufferedImage) {
    constructor(codepoint: Int, width: Int, height: Int): this(codepoint,
            BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY, COLOR_MODEL))

    val width = image.width
    val height = image.height

    init {
        if(width % 4 != 0)
            throw IllegalArgumentException("Glyph width not a multiple of 4, it cannot be expressed in hex")
    }

    fun writeToHex(): String {
        val glyphHex = (0 until height).map { y ->
            var row = 0L
            for(x in 0 until width) {
                val mask = 1L shl (width-1-x)
                val pixelSet = image.isColor(x, y, Color.BLACK)
                if(pixelSet) {
                    row = row or mask
                }
            }
            row
        }.joinToString("") {
            it.toString(16).padStart(width/4, '0').toUpperCase()
        }

        val codepointHex = codepoint.toString(16).toUpperCase().let {
            if(it.length > 4)
                it.padStart(6, '0')
            else
                it.padStart(4, '0')
        }

        return "$codepointHex:$glyphHex"
    }

    companion object {
        val COLOR_MODEL = IndexColorModel(Color(1f, 1f, 1f, 0f), Color.BLACK)

        fun readFromHex(line: String): Glyph {
            val tokenizer = Tokenizer(line)
            val codepoint = tokenizer.until(':').toInt(16)
            val height = tokenizer.untilIf('-').let { if(it.isEmpty()) 16 else it.toInt(16)*8 }
            val hex = tokenizer.remaining()

            if(hex.isEmpty() || hex.any { it !in "0123456789abcdefABCDEF" })
                throw IllegalArgumentException("Glyph string `$hex` is not valid hex")
            val width = floor(hex.length*4.0/16).toInt()

            val glyph = Glyph(codepoint, width, height)
            val rows = hex.chunked(hex.length/height).map { it.toLong(16) }

            for (x in 0 until width) {
                for (y in 0 until height) {
                    val mask = 1L shl (width-1-x)
                    val pixelSet = rows[y] and mask != 0L
                    glyph.image.setRGB(x, y, if(pixelSet) Color.BLACK.rgb else Color(1f, 1f, 1f, 0f).rgb)
                }
            }

            return glyph
        }

        fun missing(codepoint: Int): Glyph {
            val glyph = Glyph(codepoint, 16, 16)

            val hexDigits = "%06X".format(codepoint).removePrefix("00")

            val g = glyph.image.graphics
            when(hexDigits.length) {
                4 -> {
                    g.color = Color.BLACK
                    g.fillRect(1, 1, 14, 14)
                    Images.drawMiniText(g, 2, 1, hexDigits.substring(0, 2))
                    Images.drawMiniText(g, 2, 8, hexDigits.substring(2, 4))
                }
                6 -> {
                    Images.drawMiniText(g, 0, 1, hexDigits.substring(0, 3))
                    Images.drawMiniText(g, 0, 8, hexDigits.substring(3, 6))
                }
                else -> {
                    drawErrorGlyph(g, 0x0001)
                }
            }
            g.dispose()

            return glyph
        }

        private fun drawErrorGlyph(g: Graphics, error: Int) {
            val hexDigits = "%04X".format(error)
            g.drawImage(Images["error_base"], 0, 0, null)

            Images.drawMiniChar(g,  1, 1, hexDigits[0])
            Images.drawMiniChar(g, 10, 1, hexDigits[1])
            Images.drawMiniChar(g,  1, 8, hexDigits[2])
            Images.drawMiniChar(g, 10, 8, hexDigits[3])
        }
    }
}

