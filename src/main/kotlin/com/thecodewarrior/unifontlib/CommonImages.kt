package com.thecodewarrior.unifontlib

import javax.imageio.ImageIO

object CommonImages {
    var miniDigits = "0123456789abcdef".map { ImageIO.read(resource("mini_digit_$it.png")) }
    var digits = "0123456789abcdef".map { ImageIO.read(resource("digit_$it.png")) }
}