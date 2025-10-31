package com.silverguard.cam.core.styles

import android.graphics.Typeface

class CamDefaultFonts(
    private val customFonts: CamFontsInterface? = null
) : CamFontsInterface {
    override val button: CamFontStyles = customFonts?.button ?: CamFontStyles(
        size = 14f,
        style = Typeface.BOLD
    )
    override val body: CamFontStyles = customFonts?.body ?: CamFontStyles(
        size = 14f,
        style = Typeface.NORMAL
    )
    override val headline2: CamFontStyles = customFonts?.headline2 ?: CamFontStyles(
        size = 24f,
        style = Typeface.BOLD
    )
    override val headline3: CamFontStyles = customFonts?.headline3 ?: CamFontStyles(
        size = 20f,
        style = Typeface.BOLD
    )
}

data class CamFontStyles(
    val size: Float,
    val style: Int
)