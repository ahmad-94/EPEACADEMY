package com.example.easypeasyenglish.styles

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.scale
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.selectors.hover
import org.jetbrains.compose.web.css.percent

val PostPreviewStyle = CssStyle {
    base {
        Modifier
            .scale(100.percent)
            .styleModifier {
        property("transition", "all 0.3s ease")
    }
    }

    hover {
        Modifier
            .scale(102.percent)
    }
}