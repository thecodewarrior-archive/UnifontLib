package com.thecodewarrior.unifontlib

import com.thecodewarrior.unifontlib.utils.resource
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.FileNotFoundException
import java.io.IOException
import javax.imageio.ImageIO
import kotlin.math.min

object Images {
    val imageCache = mutableMapOf<String, BufferedImage>()

    val font = this["font"]
    val miniFont = this["font_mini"]

    operator fun get(name: String): BufferedImage {
        val img: BufferedImage
        val cached = imageCache[name]
        if(cached != null) {
            img = cached
        } else {
            img = try {
                val r = resource("$name.png") ?: throw FileNotFoundException("!/$name.png")
                ImageIO.read(r)
            } catch (e: IOException) {
                val pixel = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
                pixel.setRGB(0, 0, 0xFF00FF and (0xFF shl 24))
                pixel
            }
            imageCache[name] = img
        }
        return img
    }

    fun drawText(g: Graphics, x: Int, y: Int, text: String, tracking: Int = 0) {
        var currentX = x
        text.forEach {
            drawChar(g, currentX, y, it)
            currentX += 8 + tracking
        }
    }

    fun drawChar(g: Graphics, x: Int, y: Int, char: Char) {
        val glyphNumber = min(char.toInt(), 255)
        val glyphX = (glyphNumber and 0x0F) * 16
        val glyphY = (glyphNumber shr 4 and 0x0F) * 16
        g.drawImage(font, x, y, x+8, y+16, glyphX, glyphY, glyphX+8, glyphY+16, null)
    }

    fun drawMiniText(g: Graphics, x: Int, y: Int, text: String, tracking: Int = 0) {
        var currentX = x
        text.forEach {
            drawMiniChar(g, currentX, y, it)
            currentX += 6 + tracking
        }
    }

    fun drawMiniChar(g: Graphics, x: Int, y: Int, char: Char) {
        val glyphNumber = min(char.toInt(), 255)
        val glyphX = (glyphNumber and 0x0F) * 6
        val glyphY = (glyphNumber shr 4 and 0x0F) * 7
        g.drawImage(font, x, y, x+6, y+7, glyphX, glyphY, glyphX+6, glyphY+7, null)
    }
}