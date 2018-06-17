package com.thecodewarrior.unifontlib.commands

import com.github.ajalt.clikt.core.BadParameterValue
import com.github.ajalt.clikt.parameters.options.FlagOption
import com.github.ajalt.clikt.parameters.options.RawOption
import com.github.ajalt.clikt.parameters.options.convert
import java.awt.Color
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.awt.image.IndexColorModel

fun RawOption.hex() = this.convert {
    it.toInt(16)
}

fun Graphics.drawPixel(x: Int, y: Int, color: Color) {
    val oldColor = this.color

    this.color = color
    this.fillRect(x, y, 1, 1)

    this.color = oldColor
}


fun RawOption.nullableFlag(vararg secondaryNames: String): FlagOption<Boolean?> {
    return FlagOption(names, secondaryNames.toSet(), help, hidden, envvar,
            transformEnvvar = {
                when (it.toLowerCase()) {
                    "true", "t", "1", "yes", "y", "on" -> true
                    "false", "f", "0", "no", "n", "off" -> false
                    else -> throw BadParameterValue("${System.getenv(envvar)} is not a valid boolean", this)
                }
            },
            transformAll = {
                it.lastOrNull()?.let { it !in secondaryNames }
            })
}

fun IndexColorModel(vararg palette: Color): IndexColorModel {
    if(palette.size < 2 || palette.size > 65536) {
        throw IllegalArgumentException("Palette size ${palette.size} not in range [2, 65536].")
    }
    var bits = 0
    var maxIndex = palette.size-1
    while(maxIndex != 0) {
        bits++
        maxIndex = maxIndex ushr 1
    }

    val reds   = palette.map { it.red.toByte()   }.toByteArray()
    val greens = palette.map { it.green.toByte() }.toByteArray()
    val blues  = palette.map { it.blue.toByte()  }.toByteArray()
    val alphas = palette.map { it.alpha.toByte() }.toByteArray()
    return IndexColorModel(bits, palette.size, reds, greens, blues, alphas)
}

