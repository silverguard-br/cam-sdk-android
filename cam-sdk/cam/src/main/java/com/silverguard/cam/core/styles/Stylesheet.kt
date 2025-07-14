package com.silverguard.cam.core.styles

interface Styling {
    fun setColors(colors: ColorsInterface)
    fun setFonts(fonts: FontsInterface)
}

object Stylesheet {
    /*var colors: ColorsInterface = DefaultColors()
    var fonts: FontsInterface? = null*/

    fun setColors(colors: ColorsInterface) {
        //this.colors = colors
    }

    fun setFonts(fonts: FontsInterface) {
        //this.fonts = fonts
    }
}