package com.example.easypeasyenglish.styles

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.selectors.hover

val EditorKeyStyle = CssStyle {
    base {
        Modifier
            .backgroundColor(Colors.Transparent)
            .styleModifier {
                property("background", "all 0.3s ease")
            }
    }
    hover {
        Modifier.backgroundColor(Colors.Blue)
    }
}