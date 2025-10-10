package com.silverguard.cam.core.styles

object Stylesheet {
    var colors: CAMColorsInterface = CAMDefaultColors()
    var fonts: CAMFontsInterface = CAMDefaultFonts()

    fun setCAMColors(colors: CAMColorsInterface) {
        this.colors = colors
    }

    fun setCAMFonts(fonts: CAMFontsInterface) {
        this.fonts = fonts
    }
}