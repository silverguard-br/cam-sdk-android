package com.silverguard.cam.core.styles

object Stylesheet {
    var colors: CamColorsInterface = CamDefaultColors()
    var fonts: CamFontsInterface = CamDefaultFonts()

    fun setCamColors(colors: CamColorsInterface) {
        this.colors = colors
    }

    fun setCamFonts(fonts: CamFontsInterface) {
        this.fonts = fonts
    }
}