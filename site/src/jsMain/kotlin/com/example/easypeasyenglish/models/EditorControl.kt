package com.example.easypeasyenglish.models

import com.example.easypeasyenglish.utils.Res

enum class EditorControl(
    val icon: String
) {
    Bold(icon = Res.Icon.BOLD),
    Italic(icon = Res.Icon.ITALIC),
    Link(icon = Res.Icon.LINK),
    Title(icon = Res.Icon.TITLE),
    Subtitle(icon = Res.Icon.SUBTITLE),
    Quote(icon = Res.Icon.QUOTE),
    Code(icon = Res.Icon.CODE),
    Image(icon = Res.Icon.IMG),
}