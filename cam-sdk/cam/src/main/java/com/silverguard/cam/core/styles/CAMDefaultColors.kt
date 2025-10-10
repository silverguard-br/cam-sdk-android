package com.silverguard.cam.core.styles

import androidx.core.graphics.toColorInt

class CAMDefaultColors(
    private val customColors: CAMColorsInterface? = null
) : CAMColorsInterface {
    override val background: Int =
        customColors?.background ?: "#FFFFFF".toColorInt()

    override val primary: Int =
        customColors?.primary ?: "#1B264F".toColorInt()

    override val label: Int =
        customColors?.label ?: "#282828".toColorInt()

    override val buttonTitle: Int =
        customColors?.buttonTitle ?: "#FEFEFE".toColorInt()
    override val buttonEnabled: Int =
        customColors?.buttonEnabled ?: "#1B264F".toColorInt()
    override val buttonDisabled: Int =
        customColors?.buttonDisabled ?: "#767D95".toColorInt()
}