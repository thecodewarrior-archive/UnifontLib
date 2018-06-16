package com.thecodewarrior.unifontlib.commands

import com.github.ajalt.clikt.parameters.options.RawOption
import com.github.ajalt.clikt.parameters.options.convert
import java.awt.Color
import java.awt.Graphics

fun RawOption.hex() = this.convert {
    it.toInt(16)
}

fun Graphics.drawPixel(x: Int, y: Int, color: Color) {
    val oldColor = this.color

    this.color = color
    this.fillRect(x, y, 1, 1)

    this.color = oldColor
}
