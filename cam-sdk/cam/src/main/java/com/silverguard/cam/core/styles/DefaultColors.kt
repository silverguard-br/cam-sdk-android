package com.silverguard.cam.core.styles

import com.silverguard.cam.core.extensions.toColorOrDefault

class DefaultColors : ColorsInterface {
    override val background: Int = "#FFFFFF".toColorOrDefault(android.graphics.Color.WHITE)

    override val primary: Int = "#1B264F".toColorOrDefault(android.graphics.Color.BLUE)
    override val primary04: Int = "#F0F3FB".toColorOrDefault(android.graphics.Color.BLUE)

    override val label: Int = "#282828".toColorOrDefault(android.graphics.Color.BLACK)
    override val buttonTitle: Int = "#FEFEFE".toColorOrDefault(android.graphics.Color.WHITE)

    override val surface: Int = "#212121".toColorOrDefault(android.graphics.Color.DKGRAY)

    override val buttonEnabled: Int = "#1B264F".toColorOrDefault(android.graphics.Color.BLUE)
    override val buttonDisabled: Int = "#767D95".toColorOrDefault(android.graphics.Color.GRAY)
}