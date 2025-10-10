package com.silverguard.cam.core.styles

import android.graphics.Typeface

class CAMDefaultFonts(
    private val customFonts: CAMFontsInterface? = null
) : CAMFontsInterface {
    override val button: CAMFontStyles = customFonts?.button ?: CAMFontStyles(
        size = 14f,
        style = Typeface.BOLD
    )
    override val body: CAMFontStyles = customFonts?.body ?: CAMFontStyles(
        size = 14f,
        style = Typeface.NORMAL
    )
    override val headline2: CAMFontStyles = customFonts?.headline2 ?: CAMFontStyles(
        size = 24f,
        style = Typeface.BOLD
    )
    override val headline3: CAMFontStyles = customFonts?.headline3 ?: CAMFontStyles(
        size = 20f,
        style = Typeface.BOLD
    )
}

data class CAMFontStyles(
    val size: Float,
    val style: Int
)