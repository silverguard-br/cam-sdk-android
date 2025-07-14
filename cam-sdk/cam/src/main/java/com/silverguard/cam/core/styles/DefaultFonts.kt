package com.silverguard.cam.core.styles

import android.graphics.Typeface

class DefaultFonts() : FontsInterface {
    override val button: Typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    override val body: Typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    override val headline2: Typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    override val headline3: Typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
}